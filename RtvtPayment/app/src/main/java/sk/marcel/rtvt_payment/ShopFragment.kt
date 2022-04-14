package sk.marcel.rtvt_payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class ShopFragment : Fragment(), NfcFragment{
    companion object {
        const val TITLE = "Shop"
        fun newInstance() = ShopFragment()
    }

    lateinit var adapter: ItemsAdapter

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
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Nope (not enough money on card?)", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateSum(sum: Long){
        view?.findViewById<TextView>(R.id.sum)!!.text = "Sum: $sum"
    }
}