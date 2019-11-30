package hr.ferit.matijasokol.firebasenotesapp.ui.activities.loginRegistryActivity

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.ferit.matijasokol.firebasenotesapp.R
import hr.ferit.matijasokol.firebasenotesapp.common.displayToast
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractor
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.presenters.LoginRegistryActivityPresenter
import hr.ferit.matijasokol.firebasenotesapp.ui.activities.MainActivity
import hr.ferit.matijasokol.firebasenotesapp.ui.activities.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login_register_content.*
import kotlinx.android.synthetic.main.toolbar.*

class LoginRegisterActivity : BaseActivity(), LoginRegistryActivityContract.View {

    companion object {
        const val LOGIN_REGISTER_REQUEST_CODE = 1
        const val START = "start"
        const val ACTIVITY_RESULT = "activity result"
    }

    private val TAG = "[DEBUG] LoginRegisterAc"
    private val presenter: LoginRegistryActivityContract.Presenter by lazy {
        LoginRegistryActivityPresenter(this)
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_login_register

    override fun setUpUi() {
        presenter.getCurrentUser(START)
    }

    //region UI control
    private fun setListeners() {
        buttonLoginRegister.setOnClickListener { handleLoginRegister() }
    }

    private fun handleLoginRegister() {
        presenter.getAuthUIInstance()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            LOGIN_REGISTER_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.getCurrentUser(ACTIVITY_RESULT)
                } else {
                    val response = IdpResponse.fromResultIntent(data)
                    if (response == null) {
                        Log.d(TAG, "onActivityResult: user has cancelled sing in request")
                    } else {
                        Log.d(TAG, "onActivityResult: " + response.error)
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    //endregion

    //region Presenter callbacks
    override fun onGetCurrentUser(firebaseUser: FirebaseUser?, key: String) {
        if (key == START) {
            if (firebaseUser != null) {
                startMainActivity()
            }

            setToolbar()
            setListeners()
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        } else {
            firebaseUser?.metadata?.let {
                if (it.creationTimestamp == it.lastSignInTimestamp) {
                    displayToast("Welcome new user")
                } else {
                    displayToast("Welcome back again")
                }
            }

            startMainActivity()
        }
    }

    override fun onGetAuthUIInstance(authUI: AuthUI) {
        val listOfLoginRegisterProviders = listOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val intent = authUI
            .createSignInIntentBuilder()
            .setAvailableProviders(listOfLoginRegisterProviders)
            .setLogo(R.drawable.notes_app_logo)
            .setAlwaysShowSignInMethodScreen(true)
            .build()

        startActivityForResult(intent,
            LOGIN_REGISTER_REQUEST_CODE
        )
    }
    //endregion
}
