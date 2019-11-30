package hr.ferit.matijasokol.firebasenotesapp.ui.activities.loginRegistryActivity

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser

interface LoginRegistryActivityContract {

    interface View {
        fun onGetCurrentUser(firebaseUser: FirebaseUser?, key: String)
        fun onGetAuthUIInstance(authUI: AuthUI)
    }

    interface Presenter {
        fun getCurrentUser(key: String)
        fun getAuthUIInstance()
    }
}