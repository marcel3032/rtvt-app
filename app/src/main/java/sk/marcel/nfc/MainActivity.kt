package sk.marcel.nfc

import android.content.Intent
import android.content.res.Resources
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial


class MainActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        createPixels()
    }

    private fun createPixels(){
        val frameLayout = findViewById<FrameLayout>(R.id.frame)
        frameLayout.removeAllViews()
        val rowCount = 5
        val colCount = 4
        val grid = GridLayout(this)
        grid.columnCount = colCount
        grid.rowCount = rowCount
        val dim = minOf(Resources.getSystem().displayMetrics.widthPixels/colCount, Resources.getSystem().displayMetrics.widthPixels/ colCount)
        grid.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        for(i in 0 until rowCount){
            for(j in 0 until colCount){
                val pixel = ImageView(this)
                pixel.layoutParams = ViewGroup.LayoutParams(dim, dim)
                if((i+j)%2==0)
                    pixel.setBackgroundColor(getColor(R.color.purple_200))
                else
                    pixel.setBackgroundColor(getColor(R.color.teal_700))
                grid.addView(pixel)
            }
        }
        frameLayout.addView(grid)
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