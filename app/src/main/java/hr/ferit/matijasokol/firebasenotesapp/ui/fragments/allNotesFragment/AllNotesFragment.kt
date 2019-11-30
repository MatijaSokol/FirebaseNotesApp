package hr.ferit.matijasokol.firebasenotesapp.ui.fragments.allNotesFragment

import android.content.Intent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.matijasokol.firebasenotesapp.presenters.AllNotesFragmentPresenter
import hr.ferit.matijasokol.firebasenotesapp.R
import hr.ferit.matijasokol.firebasenotesapp.common.displayToast
import hr.ferit.matijasokol.firebasenotesapp.common.gone
import hr.ferit.matijasokol.firebasenotesapp.common.visible
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.model.Note
import hr.ferit.matijasokol.firebasenotesapp.ui.activities.loginRegistryActivity.LoginRegisterActivity
import hr.ferit.matijasokol.firebasenotesapp.ui.adapters.AllNotesAdapter
import hr.ferit.matijasokol.firebasenotesapp.ui.adapters.RecyclerMoveSwipeCallback
import hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs.AddNoteFragmentDialog
import hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs.UpdateNoteFragmentDialog
import hr.ferit.matijasokol.firebasenotesapp.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_all_notes.*

class AllNotesFragment : BaseFragment(), FirebaseAuth.AuthStateListener, AllNotesFragmentContract.View, AddNoteFragmentDialog.OnNoteAddedFromDialogListener,
UpdateNoteFragmentDialog.OnNoteUpdatedFromDialogListener {

    companion object {
        const val START = "start"
        const val STOP = "stop"

        fun newInstance() = AllNotesFragment()
    }

    private lateinit var allNotesAdapter: AllNotesAdapter
    private val presenter: AllNotesFragmentContract.Presenter by lazy {
        AllNotesFragmentPresenter(this)
    }

    override fun getLayoutResourceId(): Int = R.layout.fragment_all_notes

    override fun setUpUi() {
        setListeners()
    }

    //region UI control
    private fun setListeners() {
        buttonOpenAddNoteDialog.setOnClickListener { handleOpenAddNoteDialog() }
    }

    private fun handleOpenAddNoteDialog() {
        val dialog = AddNoteFragmentDialog.newInstance()
        dialog.setOnNoteAddedFromDialogListener(this)
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun handleOpenUpdateNoteDialog(documentSnapshot: DocumentSnapshot) {
        val dialog = UpdateNoteFragmentDialog.newInstance()
        dialog.setOnNoteUpdatedFromDialogListener(this)
        dialog.setDocumentSnapshot(documentSnapshot)
        dialog.show(childFragmentManager, dialog.tag)
    }

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
            .whereEqualTo(FirebaseInteractorImpl.IS_ARCHIVED_FIELD_NAME, false)
            .orderBy(FirebaseInteractorImpl.IS_COMPLETED_FIELD_NAME)
            .orderBy(FirebaseInteractorImpl.TIME_CREATED_FIELD_NAME)

        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        allNotesAdapter = AllNotesAdapter(options, { documentSnapshot ->  onItemLongClicked(documentSnapshot) },
            { documentSnapshot, isChecked ->  onCheckBoxCheckedChanged(documentSnapshot, isChecked) })

        recyclerAllNotes.apply {
            adapter = allNotesAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }

        allNotesAdapter.startListening()

        val recyclerMoveSwipeCallback = RecyclerMoveSwipeCallback { position, direction -> onItemSwiped(position, direction) }
        val itemTouchHelper = ItemTouchHelper(recyclerMoveSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerAllNotes)
    }

    private fun onItemSwiped(position: Int, direction: Int) {
        val documentSnapshot = allNotesAdapter.getItemSnapshot(position)
        if (direction == ItemTouchHelper.LEFT) {
            presenter.deleteNote(documentSnapshot)
        } else if (direction == ItemTouchHelper.RIGHT) {
            presenter.changeIsArchived(documentSnapshot, true)
        }
    }

    private fun onItemLongClicked(documentSnapshot: DocumentSnapshot) {
        handleOpenUpdateNoteDialog(documentSnapshot)
    }

    private fun onCheckBoxCheckedChanged(documentSnapshot: DocumentSnapshot, isCompleted: Boolean) {
        presenter.changeIsCompleted(documentSnapshot, isCompleted)
    }
    //endregion

    //region Dialog callbacks
    override fun onNoteAddedFromDialog(note: Note) {
        if (note.imageUri != null) {
            progressBar.visible()
            presenter.saveImage(note, activity!!.contentResolver)
        } else {
            presenter.addNote(note)
        }
    }

    override fun onNoteUpdatedFromDialog(note: Note, documentSnapshot: DocumentSnapshot) {
        presenter.updateNote(note, documentSnapshot)
    }
    //endregion

    //region Presenter callbacks
    override fun onGetFirebaseAuthInstance(firebaseAuth: FirebaseAuth, key: String) {
        if (key == START){
            firebaseAuth.addAuthStateListener(this)
        } else {
            firebaseAuth.removeAuthStateListener(this)
            if (::allNotesAdapter.isInitialized) {
                allNotesAdapter.stopListening()
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

    override fun onNoteAddedInFirestoreSuccessfully(note: Note) {
        if (progressBar.isVisible) { progressBar.gone() }
        activity?.displayToast(getString(R.string.item_added_successfully))
    }

    override fun onNoteAddInFirestoreFailed() {
        if (progressBar.isVisible) { progressBar.gone() }
        activity?.displayToast(getString(R.string.item_added_failed))
    }

    override fun onNoteUpdatedInFirestoreSuccessfully() {
        activity?.displayToast(getString(R.string.note_updated_successfully))
    }

    override fun onNoteUpdateInFirestoreFailed() {
        activity?.displayToast(getString(R.string.failed_update))
    }

    override fun onNoteDeletedInFirestoreSuccessfully(documentSnapshot: DocumentSnapshot) {
        val note = documentSnapshot.toObject(Note::class.java)!!
        var delete = true

        val snackbar = Snackbar.make(recyclerAllNotes, "Item deleted", Snackbar.LENGTH_LONG)
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
    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        presenter.getCurrentUser()
    }
    //endregion
}