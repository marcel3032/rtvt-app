package sk.marcel.rtvt

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
import java.io.IOException

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

    fun disableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity) {
        nfcAdapter.disableForegroundDispatch(activity)
    }
    fun <T>enableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity, classType : Class<T>) {
        val pendingIntent = PendingIntent.getActivity(activity, 0, Intent(activity,classType).addFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
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