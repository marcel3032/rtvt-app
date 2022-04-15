package sk.marcel.rtvt_editor

import android.content.Intent

interface NfcFragment {
    abstract fun doNfcIntent(intent: Intent)
}