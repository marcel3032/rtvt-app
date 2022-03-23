package sk.marcel.rtvt

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
    private var needColors = HashMap<Pair<Int, Int>, String>()
    private lateinit var jsonsHelpers: JsonsHelpers
    private var reading = true

    private fun displayNeededColors(){
        findViewById<FrameLayout>(R.id.need_colors).removeAllViews()
        if(needColors.isEmpty()){
            writeSubmitNFC()
            return
        }
        val grid = GridLayout(this)
        grid.columnCount = 3
        grid.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        for(color in needColors.values){
            val colorView = this.layoutInflater.inflate(R.layout.needed_color, findViewById(R.id.need_colors), false)
            colorView.findViewById<ImageView>(R.id.color_image_view).setBackgroundColor(Color.parseColor(Constants.colorsMap[color]))
            colorView.findViewById<TextView>(R.id.color_text_view).text = color
            grid.addView(colorView)
        }
        findViewById<FrameLayout>(R.id.need_colors).addView(grid)
    }

    private fun makePixelView(dim: Int, pixel: JSONObject, i: Int, j: Int): ImageView {
        val pixelView = ImageView(this)
        val coloredPixels = jsonsHelpers.getColoredPixels()
        pixelView.layoutParams = ViewGroup.LayoutParams(dim, dim)
        if(pixel.getBoolean(Constants.precolored) || coloredPixels.contains(Pair(i,j)))
            pixelView.setBackgroundColor(Color.parseColor(pixel.getString(Constants.displayColor)))
        else{
            needColors[Pair(i,j)] = pixel.getString(Constants.needColor)
            pixelView.setBackgroundColor(Color.parseColor(Constants.alpha+(pixel.getString(Constants.displayColor)).substring(1)))
        }
        return pixelView
    }

    private fun createPixels(){
        needColors = HashMap()
        val json = jsonsHelpers.getPicturesJson(jsonsHelpers.getProgressNum()) ?: return
        val frameLayout = findViewById<FrameLayout>(R.id.frame)
        frameLayout.removeAllViews()

        val grid = GridLayout(this)
        grid.rowCount = json.length()
        grid.columnCount = json.getJSONArray(0).length()
        grid.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        val dim = minOf(
            (Resources.getSystem().displayMetrics.widthPixels-70) / grid.columnCount,
            (Resources.getSystem().displayMetrics.heightPixels-70) / grid.rowCount
        )

        for(i in 0 until grid.rowCount)
            for(j in 0 until grid.columnCount)
                grid.addView(makePixelView(dim, json.getJSONArray(i).getJSONObject(j), i, j))

        frameLayout.addView(grid)
    }

    fun addColorOnClick(v: View){
        val colorTextView = findViewById<EditText>(R.id.color)
        val addedColor = colorTextView.text.toString()
        colorTextView.text = SpannableStringBuilder("")

        addColor(addedColor)
    }

    fun addColor(color: String){
        for(key in needColors.keys){
            if(needColors[key] == color) {
                needColors.remove(key)
                jsonsHelpers.addSolvedColor(key.first, key.second)
                break
            }
        }
        createPixels()
        displayNeededColors()

        if(needColors.isEmpty()){
            writeSubmitNFC()
        }
    }

    fun writeSubmitNFC(){
        reading = false
        val textView = TextView(this)
        textView.text = "Picture finished, please submit it"
        textView.textSize = 35f
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        findViewById<FrameLayout>(R.id.need_colors).addView(textView)
    }

    fun resetProgressFile(v: View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Reset?")
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                jsonsHelpers.resetProgressFile()
                createPixels()
                displayNeededColors()
            }
            .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    fun setTeamName(v: View){
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)
        editText.id = View.generateViewId()
        editText.text = SpannableStringBuilder(jsonsHelpers.getTeamName())
        builder.setMessage("Set team name?")
            .setPositiveButton("Save") { _: DialogInterface?, _: Int ->
                jsonsHelpers.setTeamName(editText.text.toString())
            }
            .setView(editText)
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        jsonsHelpers = JsonsHelpers(this)
        createPixels()
        displayNeededColors()
    }

    override fun onResume() {
        super.onResume()

        mNfcAdapter?.let {
            NFC.enableNFCInForeground(it, this,javaClass)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if(intent!=null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
                if(reading) {
                    val res = NFC.read(intent)
                    if(res!=null)
                        Toast.makeText(this, "on card: $res", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "reading failed", Toast.LENGTH_SHORT).show()
                } else {
                    val jsonToWrite = JSONObject()
                    jsonToWrite.put("picture-number", jsonsHelpers.getProgressNum())
                    jsonToWrite.put("team", jsonsHelpers.getTeamName())
                    if(NFC.write(jsonToWrite.toString(), intent)){
                        Toast.makeText(this, "Data written", Toast.LENGTH_SHORT).show()
                        jsonsHelpers.startNewPicture()
                        reading = true
                        createPixels()
                        displayNeededColors()
                    }
                    else
                        Toast.makeText(this, "write failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.let {
            NFC.disableNFCInForeground(it,this)
        }
    }
}