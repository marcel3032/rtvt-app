package sk.marcel.rtvt_rozcvicky

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources


class PeopleAdapter(private val mContext: MainActivity, private val resourceLayout: Int, items: List<Person>) :
    ArrayAdapter<Person>(mContext, resourceLayout, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false)
        }
        val label = v!!.findViewById<CheckedTextView>(R.id.name)
        val group = v.findViewById<TextView>(R.id.group)
        val p: Person? = getItem(position)
        if (p != null) {
            label.text = p.name
            label.textSize = 25f
            if(MainActivity.jsonsHelpers.isCheckedOut(p.id)) {
                label.checkMarkDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_check)
                label.checkMarkTintList = ColorStateList.valueOf(mContext.resources.getColor(R.color.blue))
            }else if(MainActivity.jsonsHelpers.isCheckedIn(p.id) != null) {
                label.checkMarkDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_check)
                label.checkMarkTintList = ColorStateList.valueOf(mContext.resources.getColor(R.color.green))
            }else {
                label.checkMarkDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_cross)
                label.checkMarkTintList = ColorStateList.valueOf(mContext.resources.getColor(R.color.red))
            }
            group.text = p.group
            group.textSize = 18f
        }
        return v
    }
}