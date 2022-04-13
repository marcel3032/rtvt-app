package sk.marcel.rtvt_payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class AddMoneyFragment(): Fragment() {
    companion object {
        const val TITLE = "Add money"
        fun newInstance() = AddMoneyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_add_money, container, false)

        for(person in MainActivity.jsonsHelpers.getPeopleList()){
            val checkBox = CheckBox(context)
            checkBox.text = person.name
            checkBox.isChecked = false

            view.findViewById<LinearLayout>(R.id.people_list).addView(checkBox)
        }

        return view
    }
}