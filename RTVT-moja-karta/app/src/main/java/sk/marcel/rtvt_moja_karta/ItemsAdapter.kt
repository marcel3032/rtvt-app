package sk.marcel.rtvt_moja_karta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import sk.marcel.rtvt_moja_karta.Item
import sk.marcel.rtvt_moja_karta.R
import sk.marcel.rtvt_moja_karta.ShopFragment

class ItemsAdapter(private val mContext: ShopFragment, private val resourceLayout: Int, items: List<Item>) :
    ArrayAdapter<Item>(mContext.requireContext(), resourceLayout, items) {

    var sum = 0L;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(mContext.context).inflate(resourceLayout, parent, false)
        }
        val label = v!!.findViewById<TextView>(R.id.label)
        val price = v.findViewById<TextView>(R.id.price)
        val p: Item? = getItem(position)
        if (p != null) {
            label.text = p.name
            label.textSize = 20f
            price.text = p.price.toString()
            price.textSize = 25f
        }
        return v
    }
}