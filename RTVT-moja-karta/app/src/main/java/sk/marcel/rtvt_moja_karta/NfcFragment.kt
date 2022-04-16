package sk.marcel.rtvt_moja_karta

import android.content.Intent

interface NfcFragment {
    abstract fun doNfcIntent(intent: Intent)
}