package hr.ferit.matijasokol.firebasenotesapp.ui.activities

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import hr.ferit.matijasokol.firebasenotesapp.R
import hr.ferit.matijasokol.firebasenotesapp.common.showFragment
import hr.ferit.matijasokol.firebasenotesapp.ui.activities.base.BaseActivity
import hr.ferit.matijasokol.firebasenotesapp.ui.fragments.allNotesFragment.AllNotesFragment
import hr.ferit.matijasokol.firebasenotesapp.ui.fragments.archivedNotesFragment.ArchivedNotesFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun setUpUi() {
        setToolbarDrawerLayout()
        onNavigationItemSelected(navigationView.menu.findItem(R.id.allNotes))
        navigationView.menu.getItem(0).isChecked = true
    }

    private fun setToolbarDrawerLayout() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.allNotes -> {
                showFragment(R.id.fragmentContainer, AllNotesFragment.newInstance())
            }
            R.id.archivedNotes -> {
                showFragment(R.id.fragmentContainer, ArchivedNotesFragment.newInstance())
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
