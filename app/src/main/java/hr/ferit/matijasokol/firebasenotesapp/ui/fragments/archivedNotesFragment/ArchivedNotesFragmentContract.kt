package hr.ferit.matijasokol.firebasenotesapp.ui.fragments.archivedNotesFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.matijasokol.firebasenotesapp.model.Note

interface ArchivedNotesFragmentContract {

    interface View {
        fun onGetFirebaseAuthInstance(firebaseAuth: FirebaseAuth, key: String)
        fun onGetCurrentUser(firebaseUser: FirebaseUser?)
        fun onNoteDeletedInFirestoreSuccessfully(documentSnapshot: DocumentSnapshot)
        fun onNoteDeleteInFirestoreFailed()
        fun onGetFirestoreInstance(firebaseUser: FirebaseUser, firebaseFirestore: FirebaseFirestore)
    }

    interface Presenter {
        fun getFirebaseAuthInstance(key: String)
        fun getCurrentUser()
        fun getFirestoreInstance(firebaseUser: FirebaseUser)
        fun changeIsCompleted(documentSnapshot: DocumentSnapshot, isCompleted: Boolean)
        fun changeIsArchived(documentSnapshot: DocumentSnapshot, isArchived: Boolean)
        fun deleteNote(documentSnapshot: DocumentSnapshot)
        fun deleteImageFromStorage(note: Note)
        fun undoDelete(documentSnapshot: DocumentSnapshot)
    }
}