package sk.marcel.rtvt_payment

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import sk.marcel.rtvtAttendance.NFC

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var jsonsHelpers: JsonsHelpers
        private var mNfcAdapter: NfcAdapter? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonsHelpers = JsonsHelpers(this)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val viewPager = findViewById<View>(R.id.pager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter

        val tabLayout = findViewById<View>(R.id.tab) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }

    fun checkAll(v:View){
        if(findViewById<LinearLayout>(R.id.people_list).children.all { view -> (view as CheckBox).isChecked }){
            findViewById<LinearLayout>(R.id.people_list).children.forEach { view -> (view as CheckBox).isChecked=false }
        } else {
            findViewById<LinearLayout>(R.id.people_list).children.forEach { view -> (view as CheckBox).isChecked=true }
        }
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
                    }
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