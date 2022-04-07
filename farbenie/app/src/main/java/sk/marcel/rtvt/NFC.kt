package sk.marcel.rtvt

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import java.io.IOException

object NFC {
    private val key: ByteArray = MifareClassic.KEY_DEFAULT

    fun writePictureResult(team: String, picture: Int, intent: Intent) : Boolean {
        val mfc = MifareClassic.get(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG))
        mfc.connect()
        if(mfc.authenticateSectorWithKeyB(2, key)){
            val bWriteTeam = ByteArray(16)
            val teamNameByteArray: ByteArray = team.substring(0, 16).toByteArray()
            System.arraycopy(teamNameByteArray, 0, bWriteTeam, 0, 16)
            mfc.writeBlock(8, bWriteTeam)
            val bWritePicture = ByteArray(16)
            val pictureNumerByteArray = byteArrayOf(picture.toByte())
            System.arraycopy(pictureNumerByteArray, 0, bWritePicture, 0, pictureNumerByteArray.size)
            mfc.writeBlock(9, bWritePicture)
            mfc.close()
        } else {
            Log.e("pokus", "Cannot authentificate")
            mfc.close()
            return false
        }
        return true
    }

    fun read(intent: Intent):String? {
        // TODO reading based on Krtko's format
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
}