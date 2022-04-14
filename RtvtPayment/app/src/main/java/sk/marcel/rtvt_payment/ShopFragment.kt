package sk.marcel.rtvt_payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment

class ShopFragment : Fragment(), NfcFragment{
    companion object {
        const val TITLE = "Shop"
        fun newInstance() = ShopFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        lateinit var adapter: ArrayAdapter<Item>
        if(context!=null) {
            adapter = ItemsAdapter(requireContext(), R.layout.item_layout, MainActivity.jsonsHelpers.getItemsList())
        }
        val listView = view.findViewById<ListView>(R.id.item_list)
        listView.adapter = adapter

        return view
    }

    override fun doNfcIntent(intent: Intent) {
        TODO("Not yet implemented")
    }
}