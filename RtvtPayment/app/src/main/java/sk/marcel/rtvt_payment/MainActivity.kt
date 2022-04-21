package sk.marcel.rtvt_payment

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var jsonsHelpers: JsonsHelpers
        private var mNfcAdapter: NfcAdapter? = null
    }

    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var viewPager: ViewPager
    var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonsHelpers = JsonsHelpers(this)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        viewPager = findViewById<View>(R.id.pager) as ViewPager
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter

        val tabLayout = findViewById<View>(R.id.tab) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        Downloaders.ShopDownloadTask().execute("https://people.ksp.sk/~marcel/shop.json")
        Downloaders.PeopleDownloadTask().execute("https://people.ksp.sk/~marcel/people.json")
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
                (viewPagerAdapter.getItem(viewPager.currentItem) as NfcFragment).doNfcIntent(intent)
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