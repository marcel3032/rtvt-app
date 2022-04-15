package sk.marcel.rtvt_submit

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
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
            (Resources.getSystem().displayMetrics.widthPixels-100) / grid.columnCount,
            (Resources.getSystem().displayMetrics.heightPixels-100) / grid.rowCount
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

    private fun displayLastPicture(){
        val results = jsonsHelpers.getResultsJson()
        if(results.length()>0) {
            createPixels(results.getJSONObject(results.length() - 1).getInt(Constants.pictureNumber))
            val lastPicture = results.getJSONObject(results.length() - 1)
            findViewById<TextView>(R.id.submit_text_view).text = "picture: ${lastPicture.getString(Constants.pictureNumber)}\nteam: ${lastPicture.getString("team")}\ntime: ${lastPicture.getString("datetime")}"
        }
        displayProgresses()
    }

    fun displayProgresses(){
        val progressesView = findViewById<LinearLayout>(R.id.progress_bars)
        progressesView.removeAllViews()
        val teamProgresses = HashMap<String, Int>()
        val resultsJson = jsonsHelpers.getResultsJson()
        for(i in 0 until resultsJson.length()){
            val teamName = resultsJson.getJSONObject(i).getString("team")
            if(! teamProgresses.containsKey(teamName)){
                teamProgresses[teamName] = 0
            }
            teamProgresses[teamName] = teamProgresses[teamName]!! + 1
        }
        for(team in teamProgresses.keys){
            val progress = layoutInflater.inflate(R.layout.team_progress, progressesView, false)
            val textView = progress.findViewById<TextView>(R.id.team_name)
            textView.text = team
            textView.textSize = 20f
            textView.setTypeface(textView.typeface, Typeface.BOLD)
            progress.findViewById<ProgressBar>(R.id.team_progress).progress = 100 * teamProgresses[team]!! /jsonsHelpers.getPicturesNum()
            progressesView.addView(progress)
        }
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
                    val jsonRes = JSONObject()
                    jsonRes.put(Constants.team, res.first)
                    jsonRes.put(Constants.pictureNumber, res.second)
                    if(jsonsHelpers.isAlreadySolved(jsonRes)){
                        Toast.makeText(this, "Already submitted picture", Toast.LENGTH_LONG).show()
                        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.error)
                        mp.start()
                        mp.setOnCompletionListener { mp.release() }
                        return
                    }

                    jsonRes.put("datetime", Calendar.getInstance().time.toGMTString())
                    jsonsHelpers.addSolvedPicture(jsonRes)
                    displayLastPicture()

                    val mp: MediaPlayer = MediaPlayer.create(this, R.raw.ack)
                    mp.start()
                    mp.setOnCompletionListener { mp.release() }
                } else {
                    Toast.makeText(this, "reading failed", Toast.LENGTH_LONG).show()

                    val mp: MediaPlayer = MediaPlayer.create(this, R.raw.error)
                    mp.start()
                    mp.setOnCompletionListener { mp.release() }
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