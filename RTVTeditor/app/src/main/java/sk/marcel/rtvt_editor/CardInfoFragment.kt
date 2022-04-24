package sk.marcel.rtvt_editor

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog

class CardInfoFragment : Fragment(), NfcFragment{
    companion object {
        const val TITLE = "Card Info"
        fun newInstance() = CardInfoFragment()
    }

    var alertDialog: SweetAlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        return view
    }

    override fun doNfcIntent(intent: Intent) {
        val res = NFC.getInfo(intent)
        if(res!=null){
            val person = MainActivity.jsonsHelpers.getPersonByIdObject(res.first) ?: return
            view?.findViewById<TextView>(R.id.name)!!.text = person.name
            view?.findViewById<TextView>(R.id.group)!!.text = person.group
            view?.findViewById<TextView>(R.id.money)!!.text = String.format("%,d peňazí", res.second)

            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.ack)
            mp.start()
            mp.setOnCompletionListener { mp.release() }
        } else {
            alertDialog?.cancel()
            alertDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Nope")
                .setContentText("Something failed")
            alertDialog?.show()

            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.error)
            mp.start()
            mp.setOnCompletionListener { mp.release() }
        }
    }
}