package hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs.addNoteFragmentDialog

import com.google.firebase.auth.FirebaseUser

interface AddNoteFragmentDialogContract {

    interface View {
        fun onGetCurrentUser(firebaseUser: FirebaseUser?)
    }

    interface Presenter {
        fun getCurrentUser()
    }
}