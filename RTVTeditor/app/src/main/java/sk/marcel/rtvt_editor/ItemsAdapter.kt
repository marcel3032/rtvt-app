package sk.marcel.rtvt_editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView

class ItemsAdapter(private val mContext: ShopFragment, private val resourceLayout: Int, items: List<Item>) :
    ArrayAdapter<Item>(mContext.requireContext(), resourceLayout, items) {

    var sum = 0L;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(mContext.context).inflate(resourceLayout, parent, false)
        }
        val label = v!!.findViewById<CheckBox>(R.id.label)
        val price = v.findViewById<TextView>(R.id.price)
        val p: Item? = getItem(position)
        if (p != null) {
            label.text = p.name
            label.textSize = 20f
            label.isChecked = false
            label.setOnCheckedChangeListener{ button, checked -> updateSum(p.price, checked) }
            price.text = p.price.toString()
            price.textSize = 25f
        }
        return v
    }

    fun updateSum(price: Long, checked:Boolean){
        if(checked)
            sum+=price
        else
            sum-=price
        mContext.updateSum(sum)
    }
}