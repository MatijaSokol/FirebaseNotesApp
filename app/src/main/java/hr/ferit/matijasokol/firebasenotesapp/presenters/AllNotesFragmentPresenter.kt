package hr.ferit.matijasokol.firebasenotesapp.presenters

import android.content.ContentResolver
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import hr.ferit.matijasokol.firebasenotesapp.ui.fragments.allNotesFragment.AllNotesFragmentContract
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.model.Note

class AllNotesFragmentPresenter(private val view: AllNotesFragmentContract.View) : AllNotesFragmentContract.Presenter, FirebaseInteractorImpl.AllNotesFragmentPresenterListener {

    private val firebaseInteractor by lazy { FirebaseInteractorImpl.HOLDER.getInstance(this) }

    //region Presenter methods
    override fun getFirebaseAuthInstance(key: String) {
        val auth = firebaseInteractor.getFirebaseAuthInstance()
        view.onGetFirebaseAuthInstance(auth, key)
    }

    override fun getCurrentUser() {
        val user = firebaseInteractor.getCurrentUser()
        view.onGetCurrentUser(user)
    }

    override fun getFirestoreInstance(firebaseUser: FirebaseUser) {
        val firestore = firebaseInteractor.getFirestoreInstance()
        view.onGetFirestoreInstance(firebaseUser, firestore)
    }

    override fun addNote(note: Note) {
        firebaseInteractor.addNote(note)
    }

    override fun saveImage(note: Note, contentResolver: ContentResolver) {
        firebaseInteractor.saveImage(note, contentResolver)
    }

    override fun updateNote(note: Note, documentSnapshot: DocumentSnapshot) {
        firebaseInteractor.updateNote(note, documentSnapshot)
    }

    override fun changeIsCompleted(documentSnapshot: DocumentSnapshot, isCompleted: Boolean) {
        firebaseInteractor.changeIsCompleted(documentSnapshot, isCompleted)
    }

    override fun changeIsArchived(documentSnapshot: DocumentSnapshot, isArchived: Boolean) {
        firebaseInteractor.changeIsArchived(documentSnapshot, isArchived)
    }

    override fun deleteNote(documentSnapshot: DocumentSnapshot) {
        firebaseInteractor.deleteNote(documentSnapshot)
    }

    override fun deleteImageFromStorage(note: Note) {
        firebaseInteractor.deleteImageFromStorage(note)
    }

    override fun undoDelete(documentSnapshot: DocumentSnapshot) {
        firebaseInteractor.undoDelete(documentSnapshot)
    }
    //endregion

    //region Interactor callbacks
    override fun onNoteAddedInFirestoreSuccessfully(note: Note) {
        view.onNoteAddedInFirestoreSuccessfully(note)
    }

    override fun onNoteAddInFirestoreFailed() {
        view.onNoteAddInFirestoreFailed()
    }

    override fun onNoteUpdatedInFirestoreSuccessfully() {
        view.onNoteUpdatedInFirestoreSuccessfully()
    }

    override fun onNoteUpdateInFirestoreFailed() {
        view.onNoteUpdateInFirestoreFailed()
    }

    override fun onNoteDeletedInFirestoreSuccessfully(documentSnapshot: DocumentSnapshot) {
        view.onNoteDeletedInFirestoreSuccessfully(documentSnapshot)
    }

    override fun onNoteDeleteInFirestoreFailed() {
        view.onNoteDeleteInFirestoreFailed()
    }
    //endregion
}