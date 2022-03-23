package sk.marcel.rtvt_submit

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private var mNfcAdapter: NfcAdapter? = null
    private var needColors = HashMap<Pair<Int, Int>, String>()
    private lateinit var jsonsHelpers: JsonsHelpers


    private fun makePixelView(dim: Int, pixel: JSONObject): ImageView {
        val pixelView = ImageView(this)
        pixelView.layoutParams = ViewGroup.LayoutParams(dim, dim)
        pixelView.setBackgroundColor(Color.parseColor(pixel.getString(Constants.displayColor)))
        return pixelView
    }

    private fun createPixels(i: Int){
        needColors = HashMap()
        val json = jsonsHelpers.getPicturesJson(i) ?: return
        val frameLayout = findViewById<FrameLayout>(R.id.frame)
        frameLayout.removeAllViews()

        val grid = GridLayout(this)
        grid.rowCount = json.length()
        grid.columnCount = json.getJSONArray(0).length()
        grid.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val dim = minOf(
            Resources.getSystem().displayMetrics.widthPixels / grid.columnCount,
            Resources.getSystem().displayMetrics.widthPixels / grid.rowCount
        )

        for(i in 0 until grid.rowCount)
            for(j in 0 until grid.columnCount)
                grid.addView(makePixelView(dim, json.getJSONArray(i).getJSONObject(j)))

        frameLayout.addView(grid)
    }

    private fun reset(){
        findViewById<FrameLayout>(R.id.frame).removeAllViews()
        findViewById<TextView>(R.id.submit_text_view).text=""
    }

    fun resetResultsFile(v: View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Reset?")
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                jsonsHelpers.resetResultsFile()
                reset()
            }
            .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    fun displayLastPicture(){
        val results = jsonsHelpers.getResultsJson()
        if(results.length()>0)
            createPixels(results.getJSONObject(results.length()-1).getInt("picture-number"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        jsonsHelpers = JsonsHelpers(this)
        displayLastPicture()
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
                val res = NFC.read(intent)
                if(res!=null) {
                    val solvedPicture = JSONObject(res)
                    if(jsonsHelpers.isAlreadySolved(solvedPicture)){
                        Toast.makeText(this, "Already submitted picture", Toast.LENGTH_SHORT).show()
                        return
                    }

                    createPixels(solvedPicture.getInt("picture-number"))
                    solvedPicture.put("datetime", Calendar.getInstance().time.toGMTString())
                    jsonsHelpers.addSolvedPicture(solvedPicture)
                    findViewById<TextView>(R.id.submit_text_view).text = solvedPicture.toString(2)
                }
                else
                    Toast.makeText(this, "reading failed", Toast.LENGTH_SHORT).show()
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