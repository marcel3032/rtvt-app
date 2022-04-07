package sk.marcel.rtvtAttendance

import android.R.attr.path
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var jsonsHelpers : JsonsHelpers
    private var mNfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonsHelpers = JsonsHelpers(this)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setPeopleList()
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
                val res = NFC.readId(intent)
                if(res!=null){
                    Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                    if(jsonsHelpers.isNotIdInResults(res)){
                        jsonsHelpers.addPerson(res)
                        setPeopleList()
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