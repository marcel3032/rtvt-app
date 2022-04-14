package sk.marcel.rtvt_payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class TransferMoneyFragment : Fragment(), NfcFragment {
    companion object {
        const val TITLE = "Transfer money"
        fun newInstance() = TransferMoneyFragment()
    }

    var amountTaken = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_transfer_money, container, false)

        view.findViewById<CheckBox>(R.id.all_money).setOnCheckedChangeListener{ buttonView, isChecked ->
            view.findViewById<EditText>(R.id.money).isEnabled = !isChecked
        }

        return view
    }

    override fun doNfcIntent(intent: Intent) {
        if(amountTaken==0L) {
            var moneyToTranfer:Long? = null
            if(view?.findViewById<CheckBox>(R.id.all_money)?.isChecked == false){
                moneyToTranfer = view?.findViewById<EditText>(R.id.money)?.text.toString().toLong()
            }
            amountTaken = NFC.getMoney(intent,moneyToTranfer)
            Toast.makeText(context, amountTaken.toString(), Toast.LENGTH_SHORT).show()
        } else {
            val res = NFC.putMoney(intent, amountTaken)
            if(res==null)
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            else {
                amountTaken = 0
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            }
        }
    }
}