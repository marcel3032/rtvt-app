package sk.marcel.rtvt_payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import androidx.fragment.app.Fragment

class TransferMoneyFragment(): Fragment() {
    companion object {
        const val TITLE = "Transfer money"
        fun newInstance() = TransferMoneyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_transfer_money, container, false)

        view.findViewById<CheckBox>(R.id.all_money).setOnCheckedChangeListener{ buttonView, isChecked ->
            view.findViewById<EditText>(R.id.money).isEnabled = !isChecked
        }

        return view
    }
}