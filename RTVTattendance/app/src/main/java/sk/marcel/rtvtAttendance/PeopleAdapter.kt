package sk.marcel.rtvtAttendance

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
            if(MainActivity.jsonsHelpers.isNotIdInResults(p.id))
                label.checkMarkDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_cross)
            else
                label.checkMarkDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_check)
            group.text = p.group
            group.textSize = 18f
        }
        return v
    }
}