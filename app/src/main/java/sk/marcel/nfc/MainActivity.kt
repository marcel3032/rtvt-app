package sk.marcel.nfc

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File


class MainActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
    private var needColors = ArrayList<String>()
    private val neededColorsToColor = mapOf(
        Pair("red", "#ff0000"),
        Pair("green", "#00ff00"),
        Pair("blue", "#0000ff"),
        Pair("yellow", "#f6ff00"),
        Pair("cyan", "#00ddff"),
        Pair("magenta", "#ff00a2"),
        Pair("orange", "#ff7b00"),
        Pair("white", "#ffffff"),
        Pair("brown", "#853100"),
        Pair("light green", "#a9ff91"),
        Pair("dark green", "#0e4200"),
        Pair("azure", "#628bc4"),
        Pair("purple", "#8d0aff")
    )

    private fun displayNeededColors(){
        val grid = GridLayout(this)
        grid.columnCount = 4
        grid.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        for(color in needColors){
            val colorView = this.layoutInflater.inflate(R.layout.needed_color, findViewById(R.id.need_colors), false)
            colorView.findViewById<ImageView>(R.id.color_image_view).setBackgroundColor(Color.parseColor(neededColorsToColor[color]))
            colorView.findViewById<TextView>(R.id.color_text_view).text = color
            grid.addView(colorView)
        }
        findViewById<FrameLayout>(R.id.need_colors).addView(grid)
    }

    private fun getPicturesJson(i:Int): JSONArray? {
        BufferedReader(assets.open("pictures.json").reader()).use { reader ->
            return JSONArray(reader.readText()).getJSONArray(i)
        }
    }

    private fun getProgressJson(): JSONArray {
        BufferedReader(File(this.filesDir.absolutePath, "progress.json").reader()).use { reader ->
            return JSONArray(reader.readText())
        }
    }

    private fun makePixelView(dim: Int, pixel: JSONObject): ImageView {
        val pixelView = ImageView(this)
        pixelView.layoutParams = ViewGroup.LayoutParams(dim, dim)
        if(pixel.getBoolean("precolored"))
            pixelView.setBackgroundColor(Color.parseColor(pixel.getString("display_color")))
        else{
            needColors.add(pixel.getString("need_color"))
            pixelView.setBackgroundColor(Color.parseColor("#22"+(pixel.getString("display_color")).substring(1)))
        }
        return pixelView
    }

    private fun createPixels(){
        needColors = ArrayList()
        val json = getPicturesJson(1) ?: return
        val frameLayout = findViewById<FrameLayout>(R.id.frame)
        frameLayout.removeAllViews()

        val grid = GridLayout(this)
        grid.rowCount = json.length()
        grid.columnCount = json.getJSONArray(0).length()
        grid.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        val dim = minOf(
            (Resources.getSystem().displayMetrics.widthPixels)/grid.columnCount,
            Resources.getSystem().displayMetrics.widthPixels/ grid.rowCount
        )

        for(i in 0 until grid.rowCount)
            for(j in 0 until grid.columnCount)
                grid.addView(makePixelView(dim, json.getJSONArray(i).getJSONObject(j)))

        frameLayout.addView(grid)
    }

    private fun initProgress(){
        File(this.filesDir.absolutePath, "progress.json").createNewFile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        initProgress()
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
                if(findViewById<SwitchMaterial>(R.id.switch1).isChecked) {
                    if(NFC.write(findViewById<EditText>(R.id.write).text.toString(), intent))
                        Toast.makeText(this, "written: ${findViewById<EditText>(R.id.write).text}", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "write failed", Toast.LENGTH_SHORT).show()
                } else {
                    val res = NFC.read(intent)
                    if(res!=null)
                        Toast.makeText(this, "on card: $res", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "reading failed", Toast.LENGTH_SHORT).show()
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