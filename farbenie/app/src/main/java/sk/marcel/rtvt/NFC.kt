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
import java.nio.ByteBuffer

object NFC {
    private const val teamSector = 8
    private const val pictureSector = 9
    private const val sectorSize = 16
    private const val pictureOffset = 473

    private val key: ByteArray = MifareClassic.KEY_DEFAULT

    private const val wop: Byte = 0xCD.toByte()
    private const val wob: Byte = 0xAD.toByte()
    private const val aawe: Byte = 0xCE.toByte()
    private const val gaf: Byte = 0x51
    private const val gre: Byte = 0x41
    private const val pgw: Byte = 0xED.toByte()
    private const val aew: Byte = 0x11
    private const val gep: Byte = 0x3A
    private const val mw: Byte = 0xBE.toByte()

    private val som: ByteArray = byteArrayOf(aew, gre, gep, mw, pgw, wop)

    private fun getByteArrayFromNumber(x: Int): ByteArray {
        val buffer: ByteBuffer = ByteBuffer.allocate(sectorSize)
        buffer.putInt(x)
        return buffer.array()
    }

    private fun getNumberFromByteArray(bytes: ByteArray): Int {
        val buffer: ByteBuffer = ByteBuffer.allocate(sectorSize)
        buffer.put(bytes)
        buffer.flip()
        return buffer.int
    }

    fun writePictureResult(team: String, picture: Int, intent: Intent) : Boolean {
        val mfc = MifareClassic.get(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG))
        mfc.connect()
        if(mfc.authenticateSectorWithKeyB(mfc.blockToSector(teamSector), som)){
            val bWriteTeam = ByteArray(sectorSize)
            val teamNameByteArray: ByteArray = team.substring(0, minOf(sectorSize, team.length)).toByteArray()
            System.arraycopy(teamNameByteArray, 0, bWriteTeam, 0, teamNameByteArray.size)
            mfc.writeBlock(teamSector, bWriteTeam)
            mfc.writeBlock(pictureSector, getByteArrayFromNumber(picture+ pictureOffset))
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
                val auth: Boolean = mfc.authenticateSectorWithKeyB(mfc.blockToSector(teamSector), som)
                if (auth) {
                    team = String(mfc.readBlock(teamSector))
                    picture = mfc.readBlock(pictureSector)[0].toInt()
                    return "$team $picture"
                } else {
                    Log.e("rtvt", "Cannot authentificate")
                }
                mfc.close()
            } catch (e: IOException) {
                Log.e("rtvt", e.localizedMessage?:"")
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