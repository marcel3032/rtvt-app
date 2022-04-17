package sk.marcel.rtvt_moja_karta

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog

class ShopFragment : Fragment(), NfcFragment{
    companion object {
        const val TITLE = "Shop"
        fun newInstance() = ShopFragment()
    }

    lateinit var adapter: ItemsAdapter
    var alertDialog: SweetAlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        if(context!=null) {
            adapter = ItemsAdapter(this, R.layout.item_layout, MainActivity.jsonsHelpers.getItemsList())
        }
        val listView = view.findViewById<ListView>(R.id.item_list)
        listView.adapter = adapter

        return view
    }

    override fun doNfcIntent(intent: Intent) { }
}