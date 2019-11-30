package hr.ferit.matijasokol.firebasenotesapp.ui.activities.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        setUpUi()
    }

    abstract fun getLayoutResourceId(): Int

    abstract fun setUpUi()
}