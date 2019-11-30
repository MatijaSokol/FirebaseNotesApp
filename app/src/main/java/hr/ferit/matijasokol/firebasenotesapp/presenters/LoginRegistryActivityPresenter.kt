package hr.ferit.matijasokol.firebasenotesapp.presenters

import com.firebase.ui.auth.AuthUI
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractor
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.ui.activities.loginRegistryActivity.LoginRegistryActivityContract

class LoginRegistryActivityPresenter(private val view: LoginRegistryActivityContract.View) : LoginRegistryActivityContract.Presenter {

    val firebaseInteractor: FirebaseInteractor by lazy { FirebaseInteractorImpl.HOLDER.getInstance() }

    override fun getCurrentUser(key: String) {
        val user = firebaseInteractor.getCurrentUser()
        view.onGetCurrentUser(user, key)
    }

    override fun getAuthUIInstance() {
        val authUI = AuthUI.getInstance()
        view.onGetAuthUIInstance(authUI)
    }
}