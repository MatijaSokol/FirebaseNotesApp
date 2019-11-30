package hr.ferit.matijasokol.firebasenotesapp.ui.fragments.archivedNotesFragment

import android.content.Intent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.matijasokol.firebasenotesapp.R
import hr.ferit.matijasokol.firebasenotesapp.common.displayToast
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.model.Note
import hr.ferit.matijasokol.firebasenotesapp.presenters.ArchivedNotesFragmentPresenter
import hr.ferit.matijasokol.firebasenotesapp.ui.activities.loginRegistryActivity.LoginRegisterActivity
import hr.ferit.matijasokol.firebasenotesapp.ui.adapters.ArchivedNotesAdapter
import hr.ferit.matijasokol.firebasenotesapp.ui.adapters.RecyclerMoveSwipeCallback
import hr.ferit.matijasokol.firebasenotesapp.ui.fragments.BaseFragment
import hr.ferit.matijasokol.firebasenotesapp.ui.fragments.allNotesFragment.AllNotesFragment
import kotlinx.android.synthetic.main.fragment_archived_notes.*

class ArchivedNotesFragment : BaseFragment(), FirebaseAuth.AuthStateListener, ArchivedNotesFragmentContract.View {

    companion object {
        const val START = "start"
        const val STOP = "stop"

        fun newInstance(): ArchivedNotesFragment = ArchivedNotesFragment()
    }

    private lateinit var archivedNotesAdapter: ArchivedNotesAdapter
    private val presenter: ArchivedNotesFragmentContract.Presenter by lazy {
        ArchivedNotesFragmentPresenter(this)
    }

    override fun getLayoutResourceId(): Int = R.layout.fragment_archived_notes

    override fun setUpUi() {
    }

    //region UI control
    private fun startLoginRegisterActivity() {
        val intent = Intent(activity, LoginRegisterActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onStart() {
        super.onStart()
        presenter.getFirebaseAuthInstance(START)
    }

    override fun onStop() {
        super.onStop()
        presenter.getFirebaseAuthInstance(STOP)
    }
    //endregion

    //region Recycler
    private fun setRecycler(firebaseUser: FirebaseUser, firebaseFirestore: FirebaseFirestore) {
        val query = firebaseFirestore
            .collection(FirebaseInteractorImpl.NOTES_COLLECTION_PATH)
            .whereEqualTo(FirebaseInteractorImpl.USER_ID_FIELD_NAME, firebaseUser.uid)
            .whereEqualTo(FirebaseInteractorImpl.IS_ARCHIVED_FIELD_NAME, true)
            .orderBy(FirebaseInteractorImpl.IS_COMPLETED_FIELD_NAME)
            .orderBy(FirebaseInteractorImpl.TIME_CREATED_FIELD_NAME)

        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        archivedNotesAdapter = ArchivedNotesAdapter(options) { documentSnapshot, isChecked ->  onCheckBoxCheckedChanged(documentSnapshot, isChecked) }

        recyclerArchivedNotes.apply {
            adapter = archivedNotesAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }

        archivedNotesAdapter.startListening()

        val recyclerMoveSwipeCallback = RecyclerMoveSwipeCallback { position, direction -> onItemSwiped(position, direction) }
        val itemTouchHelper = ItemTouchHelper(recyclerMoveSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerArchivedNotes)
    }

    private fun onItemSwiped(position: Int, direction: Int) {
        val documentSnapshot = archivedNotesAdapter.getItemSnapshot(position)
        if (direction == ItemTouchHelper.LEFT) {
            presenter.deleteNote(documentSnapshot)
        } else if (direction == ItemTouchHelper.RIGHT) {
            presenter.changeIsArchived(documentSnapshot, false)
        }
    }

    private fun onCheckBoxCheckedChanged(documentSnapshot: DocumentSnapshot, isChecked: Boolean) {
        presenter.changeIsCompleted(documentSnapshot, isChecked)
    }
    //endregion

    //region Presenter callbacks
    override fun onGetFirebaseAuthInstance(firebaseAuth: FirebaseAuth, key: String) {
        if (key == AllNotesFragment.START){
            firebaseAuth.addAuthStateListener(this)
        } else {
            firebaseAuth.removeAuthStateListener(this)
            if (::archivedNotesAdapter.isInitialized) {
                archivedNotesAdapter.stopListening()
            }
        }
    }

    override fun onGetCurrentUser(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            startLoginRegisterActivity()
            return
        }

        presenter.getFirestoreInstance(firebaseUser)
    }

    override fun onGetFirestoreInstance(firebaseUser: FirebaseUser, firebaseFirestore: FirebaseFirestore) {
        setRecycler(firebaseUser, firebaseFirestore)
    }

    override fun onNoteDeletedInFirestoreSuccessfully(documentSnapshot: DocumentSnapshot) {
        val note = documentSnapshot.toObject(Note::class.java)!!
        var delete = true

        val snackbar = Snackbar.make(recyclerArchivedNotes, "Item deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                presenter.undoDelete(documentSnapshot)
                delete = false
            }

        snackbar.show()
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (delete && note.imageUrl != null) {
                    presenter.deleteImageFromStorage(note)
                }
            }
        })
    }

    override fun onNoteDeleteInFirestoreFailed() {
        activity?.displayToast(getString(R.string.failed_delete))
    }
    //endregion

    //region Auth callbacks
    override fun onAuthStateChanged(p0: FirebaseAuth) {
        presenter.getCurrentUser()
    }
    //endregion
}