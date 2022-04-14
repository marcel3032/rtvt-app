package sk.marcel.rtvt

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import java.io.IOException

object NFC {
    private val key: ByteArray = MifareClassic.KEY_DEFAULT

    private val wop: Byte = 0xCD.toByte()
    private val wob: Byte = 0xAD.toByte()
    private val aawe: Byte = 0xCE.toByte()
    private val gaf: Byte = 0x51
    private val gre: Byte = 0x41
    private val pgw: Byte = 0xED.toByte()
    private val aew: Byte = 0x11
    private val gep: Byte = 0x3A
    private val mw: Byte = 0xBE.toByte()

    private val som: ByteArray = byteArrayOf(aew, gre, gep, mw, pgw, wop)

    fun writePictureResult(team: String, picture: Int, intent: Intent) : Boolean {
        val mfc = MifareClassic.get(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG))
        mfc.connect()
        if(mfc.authenticateSectorWithKeyB(2, som)){
            val bWriteTeam = ByteArray(16)
            val teamNameByteArray: ByteArray = team.substring(0, minOf(16, team.length)).toByteArray()
            System.arraycopy(teamNameByteArray, 0, bWriteTeam, 0, teamNameByteArray.size)
            mfc.writeBlock(8, bWriteTeam)
            val bWritePicture = ByteArray(16)
            val pictureNumerByteArray = byteArrayOf(picture.toByte())
            System.arraycopy(pictureNumerByteArray, 0, bWritePicture, 0, pictureNumerByteArray.size)
            mfc.writeBlock(9, bWritePicture)
            mfc.close()
        } else {
            Log.e("rtvt", "Cannot authentificate")
            mfc.close()
            return false
        }
        return true
    }

    fun read(intent: Intent):String? {
        // TODO reading based on Krtko's format
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val mfc = MifareClassic.get(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG))
            val team: String
            val picture: Int
            try {
                mfc.connect()
                val auth: Boolean = mfc.authenticateSectorWithKeyB(mfc.blockToSector(8), som)
                if (auth) {
                    team = String(mfc.readBlock(8))
                    picture = mfc.readBlock(9)[0].toInt()
                    return "$team $picture"
                } else {
                    Log.e("rtvt", "Cannot authentificate")
                }
                mfc.close()
            } catch (e: IOException) {
                Log.e("rtvt", e.localizedMessage)
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