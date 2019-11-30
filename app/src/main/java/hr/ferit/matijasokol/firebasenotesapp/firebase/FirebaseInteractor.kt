package hr.ferit.matijasokol.firebasenotesapp.firebase

import android.content.ContentResolver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.matijasokol.firebasenotesapp.model.Note

interface FirebaseInteractor {

    fun getCurrentUserId(): String?
    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuthInstance(): FirebaseAuth
    fun getFirestoreInstance(): FirebaseFirestore
    fun saveImage(note: Note, contentResolver: ContentResolver)
    fun addNote(note: Note)
    fun changeIsCompleted(documentSnapshot: DocumentSnapshot, isCompleted: Boolean)
    fun changeIsArchived(documentSnapshot: DocumentSnapshot, isArchived: Boolean)
    fun updateNote(note: Note, documentSnapshot: DocumentSnapshot)
    fun deleteNote(documentSnapshot: DocumentSnapshot)
    fun deleteImageFromStorage(note: Note)
    fun undoDelete(documentSnapshot: DocumentSnapshot)
}