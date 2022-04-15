package sk.marcel.rtvt_editor

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

    override fun doNfcIntent(intent: Intent) {
        val res = NFC.removeMoney(intent, adapter.sum)
        if(res){
            alertDialog?.cancel()
            alertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success!")
                .setContentText("Items bought")
            alertDialog?.show()

            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.ack)
            mp.start()
            mp.setOnCompletionListener { mp.release() }

        } else {
            alertDialog?.cancel()
            alertDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Nope")
                .setContentText("Something failed (not enough money on card?)")
            alertDialog?.show()
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.error)
            mp.start()
            mp.setOnCompletionListener { mp.release() }
        }
    }

    fun updateSum(sum: Long){
        view?.findViewById<TextView>(R.id.sum)!!.text = "Sum: $sum"
    }
}