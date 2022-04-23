package sk.marcel.rtvt_rozcvicky

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import java.io.IOException
import java.nio.ByteBuffer

object NFC {
    private const val moneySector = 4
    private const val sectorSize = 16

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

    private fun getByteArrayFromNumber(x: Long): ByteArray {
        val buffer: ByteBuffer = ByteBuffer.allocate(sectorSize)
        buffer.putLong(x)
        return buffer.array()
    }

    private fun getNumberFromByteArray(bytes: ByteArray): Long {
        val buffer: ByteBuffer = ByteBuffer.allocate(sectorSize)
        buffer.put(bytes)
        buffer.flip()
        return buffer.long
    }

    fun readId(intent: Intent):String? {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val myTag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?
            if (myTag != null) {
                return byteArrayToHexString(myTag.id)
            }
        }
        return null
    }

    private fun byteArrayToHexString(inarray: ByteArray): String {
        var i: Int
        var `in`: Int
        val hex = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A","B", "C", "D", "E", "F")
        var out = ""
        var j: Int = 0
        while (j < inarray.size) {
            `in` = inarray[j].toInt() and 0xff
            i = `in` shr 4 and 0x0f
            out += hex[i]
            i = `in` and 0x0f
            out += hex[i]
            ++j
        }
        return out
    }

    fun addMoney(intent: Intent, amount:Long): Pair<String, Long>?{
        val mfc = MifareClassic.get(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG))
        val myTag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?
        try {
            mfc.connect()
            val auth: Boolean = mfc.authenticateSectorWithKeyB(mfc.blockToSector(moneySector), som)
            if (auth) {
                val previousAmount = getNumberFromByteArray(mfc.readBlock(moneySector))
                val newAmount = maxOf(0, previousAmount + amount)
                mfc.writeBlock(moneySector, getByteArrayFromNumber(newAmount))
                return Pair(byteArrayToHexString(myTag!!.id), newAmount)
            } else {
                Log.e("rtvt", "Cannot authentificate")
            }
            mfc.close()
        } catch (e: IOException) {
            Log.e("rtvt", e.localizedMessage?:"")
        }
        return null
    }

    fun disableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity) {
        nfcAdapter.disableForegroundDispatch(activity)
    }
    fun <T>enableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity, classType : Class<T>) {
        val pendingIntent = PendingIntent.getActivity(activity, 0, Intent(activity,classType).addFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val filters = arrayOf(nfcIntentFilter)

        val techLists = arrayOf(arrayOf(Ndef::class.java.name), arrayOf(NdefFormatable::class.java.name))

        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists)
    }
}
