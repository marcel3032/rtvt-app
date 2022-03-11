package sk.marcel.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null

    object NFC {
        fun write(payload: String, intent: Intent) : Boolean {
            val nfcRecord = NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload.toByteArray())
            val nfcMessage = NdefMessage(arrayOf(nfcRecord))
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            return  writeMessageToTag(nfcMessage, tag)
        }

        fun read(intent: Intent):String? {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                    val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                    return String(messages[0].records[0].payload)
                }
            }
            return null
        }

        fun disableNFCInForeground(nfcAdapter: NfcAdapter,activity: Activity) {
            nfcAdapter.disableForegroundDispatch(activity)
        }
        fun <T>enableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity, classType : Class<T>) {
            val pendingIntent = PendingIntent.getActivity(activity, 0, Intent(activity,classType).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
            val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            val filters = arrayOf(nfcIntentFilter)

            val techLists = arrayOf(arrayOf(Ndef::class.java.name), arrayOf(NdefFormatable::class.java.name))

            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists)
        }

        private fun writeMessageToBasicTag(nfcMessage: NdefMessage, tag: Ndef?): Boolean {
            tag?.let {
                it.connect()

                if (nfcMessage.toByteArray().size < it.maxSize && it.isWritable) {
                    it.writeNdefMessage(nfcMessage)
                    it.close()
                    return true
                }
            }
            return false
        }

        private fun writeMessageToFormatableTag(nfcMessage: NdefMessage, tag: NdefFormatable?):Boolean{
            tag?.let {
                try {
                    it.connect()
                    it.format(nfcMessage)
                    it.close()
                    return true
                } catch (e: IOException) {}
            }
            return false
        }

        private fun writeMessageToTag(nfcMessage: NdefMessage, tag: Tag?): Boolean {
            try {
                return writeMessageToBasicTag(nfcMessage, Ndef.get(tag)) || writeMessageToFormatableTag(nfcMessage, NdefFormatable.get(tag))
            } catch (e: Exception) {}
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
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