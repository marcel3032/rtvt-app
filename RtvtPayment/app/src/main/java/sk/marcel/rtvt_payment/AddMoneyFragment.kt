package sk.marcel.rtvt_payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddMoneyFragment : Fragment(), NfcFragment {
    companion object {
        const val TITLE = "Add money"
        fun newInstance() = AddMoneyFragment()
    }

    private val cardIdToCheckboxId = HashMap<String, Int>()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_add_money, container, false)

        for(person in MainActivity.jsonsHelpers.getPeopleList()){
            val checkBox = CheckBox(context)
            checkBox.id = View.generateViewId()
            checkBox.text = person.name
            checkBox.isChecked = false

            cardIdToCheckboxId[person.id] = checkBox.id

            view.findViewById<LinearLayout>(R.id.people_list).addView(checkBox)
        }

        return view
    }

    private fun getAllowedCardIds(): Set<String>{
        val res = HashSet<String>()
        for((key, value) in cardIdToCheckboxId){
            if(view?.findViewById<CheckBox>(value)?.isChecked == true) {
                res.add(key)
            }
        }
        return res
    }

    private fun uncheck(id:String){
        cardIdToCheckboxId[id]?.let { view?.findViewById<CheckBox>(it)?.isChecked = false }
    }

    override fun doNfcIntent(intent: Intent) {
        val moneyView = view?.findViewById<EditText>(R.id.money)
        val moneyToAdd = if(moneyView?.text.toString()=="") 0L  else moneyView?.text.toString().toLong()
        val res = NFC.transferMoneyToCard(intent, getAllowedCardIds(), moneyToAdd)
        if(res!=null){
            Toast.makeText(context, res.toString(), Toast.LENGTH_SHORT).show()
            if(view?.findViewById<CheckBox>(R.id.uncheck_after_write)?.isChecked!!)
                uncheck(res.first)
        } else {
            Toast.makeText(context, "Nope", Toast.LENGTH_SHORT).show()
        }
    }
}