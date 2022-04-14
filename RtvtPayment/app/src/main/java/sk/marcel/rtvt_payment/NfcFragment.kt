package sk.marcel.rtvt_payment

import android.content.Intent

interface NfcFragment {
    abstract fun doNfcIntent(intent: Intent)
}