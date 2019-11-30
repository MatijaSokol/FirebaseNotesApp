package hr.ferit.matijasokol.firebasenotesapp.presenters

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractor
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.model.Note
import hr.ferit.matijasokol.firebasenotesapp.ui.fragments.archivedNotesFragment.ArchivedNotesFragmentContract

class ArchivedNotesFragmentPresenter(private val view: ArchivedNotesFragmentContract.View) : ArchivedNotesFragmentContract.Presenter, FirebaseInteractorImpl.ArchivedNotesFragmentPresenterListener {

    private val firebaseInteractor: FirebaseInteractor by lazy { FirebaseInteractorImpl.HOLDER.getInstance(archivedNotesFragmentPresenterListener = this) }

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
    override fun onNoteDeletedInFirestoreSuccessfully(documentSnapshot: DocumentSnapshot) {
        view.onNoteDeletedInFirestoreSuccessfully(documentSnapshot)
    }

    override fun onNoteDeleteInFirestoreFailed() {
        view.onNoteDeleteInFirestoreFailed()
    }
    //endregion
}