package sk.marcel.rtvt_editor

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog

class TransferMoneyFragment : Fragment(), NfcFragment {
    companion object {
        const val TITLE = "Transfer money"
        fun newInstance() = TransferMoneyFragment()
    }

    var amountTaken = 0L
    var alertDialog: SweetAlertDialog? = null

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
                val moneyView = view?.findViewById<EditText>(R.id.money)
                moneyToTranfer = if(moneyView?.text.toString()=="") 0L  else moneyView?.text.toString().toLong()
            }
            amountTaken = NFC.transferMoneyFromCard(intent, moneyToTranfer)

            alertDialog?.cancel()
            alertDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Now second card")
                .setContentText("Money taken: $amountTaken")
            alertDialog?.show()

            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.ack)
            mp.start()
            mp.setOnCompletionListener { mp.release() }

        } else {

            val res = NFC.transferMoneyToCard(intent, null, amountTaken)
            if(res==null) {
                alertDialog?.cancel()
                alertDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Nope")
                    .setContentText("Something failed")
                alertDialog?.show()
                alertDialog?.setCanceledOnTouchOutside(false)
                val mp: MediaPlayer = MediaPlayer.create(context, R.raw.error)
                mp.start()
                mp.setOnCompletionListener { mp.release() }
            }
            else {
                amountTaken = 0
                alertDialog?.cancel()
                alertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success!")
                    .setContentText("Money transferred")
                alertDialog?.show()

                val mp: MediaPlayer = MediaPlayer.create(context, R.raw.ack)
                mp.start()
                mp.setOnCompletionListener { mp.release() }
            }

        }
    }
}