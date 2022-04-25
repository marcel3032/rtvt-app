package sk.marcel.rtvtAttendance

import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import java.io.File


class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var jsonsHelpers: JsonsHelpers
    }
    private var mNfcAdapter: NfcAdapter? = null
    var alertDialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonsHelpers = JsonsHelpers(this)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setPeopleList()
        Downloaders.PeopleDownloadTask().execute("https://people.ksp.sk/~marcel/people.json")
    }

    override fun onResume() {
        super.onResume()

        mNfcAdapter?.let {
            NFC.enableNFCInForeground(it, this,javaClass)
        }
    }

    private fun createAnnouncement(alertType:Int, titleText:String, contextText:String, sound:Int){
        alertDialog?.cancel()
        alertDialog = SweetAlertDialog(this, alertType)
            .setTitleText(titleText)
            .setContentText(contextText)
        alertDialog?.show()

        val mp: MediaPlayer = MediaPlayer.create(this, sound)
        mp.start()
        mp.setOnCompletionListener { mp.release() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if(intent!=null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
                val id = NFC.readId(intent)
                if(id!=null){
                    if(jsonsHelpers.isNotIdInResults(id)){
                        val moneyView = findViewById<EditText>(R.id.money)
                        val moneyToAdd = if(moneyView.text.toString()=="") 0L  else moneyView.text.toString().toLong()
                        val res = NFC.addMoney(intent, moneyToAdd)
                        if (res != null) {
                            jsonsHelpers.addPerson(res.first, res.second)

                            createAnnouncement(SweetAlertDialog.SUCCESS_TYPE, "Success!", "Money added - ${jsonsHelpers.getPersonById(res.first)?.getString("name")}", R.raw.ack)
                        } else {
                            createAnnouncement(SweetAlertDialog.ERROR_TYPE, "Nope", "Something failed", R.raw.error)
                        }
                        setPeopleList()
                    } else {
                        createAnnouncement(SweetAlertDialog.WARNING_TYPE, "Nope", "Already scanned", R.raw.error)
                    }
                }
            }
        }
    }

    fun setPeopleList(){
        val adapter: ArrayAdapter<Person> = PeopleAdapter(this, R.layout.person_layout, jsonsHelpers.getPeopleList())

        val listView = findViewById<ListView>(R.id.people_list)
        listView.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.let {
            NFC.disableNFCInForeground(it,this)
        }
    }

    fun reset(v: View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Reset?")
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                jsonsHelpers.resetResultsFile()
                setPeopleList()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    fun newActivity(v: View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("New?")
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                jsonsHelpers.addNewActivity()
                setPeopleList()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    fun share(v:View){
        shareFile(File(filesDir.absolutePath, "attendance.json"))
    }

    private fun shareFile(file: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val screenshotUri = FileProvider.getUriForFile(this,"sk.marcel.rtvtAttendance",file)
        sharingIntent.type = "*/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
        startActivity(Intent.createChooser(sharingIntent, "Share image using"))
    }
}