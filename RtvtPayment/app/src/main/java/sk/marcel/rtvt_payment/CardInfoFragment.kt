package sk.marcel.rtvt_payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class CardInfoFragment : Fragment(), NfcFragment{
    companion object {
        const val TITLE = "Card Info"
        fun newInstance() = CardInfoFragment()
    }

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
            view?.findViewById<TextView>(R.id.money)!!.text = "${res.second} peňazí"
        } else {
            Toast.makeText(context, "Nope. Try again", Toast.LENGTH_SHORT).show()
        }
    }
}