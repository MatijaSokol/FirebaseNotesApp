package hr.ferit.matijasokol.firebasenotesapp.firebase

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import hr.ferit.matijasokol.firebasenotesapp.helpers.FileManager
import hr.ferit.matijasokol.firebasenotesapp.model.Note
import java.io.ByteArrayOutputStream

class FirebaseInteractorImpl private constructor(
    private val allNotesFragmentPresenterListener: AllNotesFragmentPresenterListener? = null,
    private val archivedNotesFragmentPresenterListener: ArchivedNotesFragmentPresenterListener? = null) : FirebaseInteractor {

    interface AllNotesFragmentPresenterListener {
        fun onNoteAddedInFirestoreSuccessfully(note: Note)
        fun onNoteAddInFirestoreFailed()
        fun onNoteUpdatedInFirestoreSuccessfully()
        fun onNoteUpdateInFirestoreFailed()
        fun onNoteDeletedInFirestoreSuccessfully(documentSnapshot: DocumentSnapshot)
        fun onNoteDeleteInFirestoreFailed()
    }

    interface ArchivedNotesFragmentPresenterListener {
        fun onNoteDeletedInFirestoreSuccessfully(documentSnapshot: DocumentSnapshot)
        fun onNoteDeleteInFirestoreFailed()
    }

    object HOLDER {
        fun getInstance(allNotesFragmentPresenterListener: AllNotesFragmentPresenterListener? = null,
                        archivedNotesFragmentPresenterListener: ArchivedNotesFragmentPresenterListener? = null): FirebaseInteractor {
            val interactor by lazy { FirebaseInteractorImpl(allNotesFragmentPresenterListener, archivedNotesFragmentPresenterListener) }
            return interactor
        }
    }

    companion object {
        private const val STORAGE_REFERENCE_PATH = "NotesImages"
        const val NOTES_COLLECTION_PATH = "Notes"
        const val IS_COMPLETED_FIELD_NAME = "completed"
        const val USER_ID_FIELD_NAME = "userId"
        const val TIME_CREATED_FIELD_NAME = "timeCreated"
        const val IS_ARCHIVED_FIELD_NAME = "archived"

        private const val IMAGE_QUALITY = 20
    }

    private val TAG = "[DEBUG] FirebaseInterac"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override fun getFirebaseAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()

    override fun getFirestoreInstance(): FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun saveImage(note: Note, contentResolver: ContentResolver) {
        val imageUri = Uri.parse(note.imageUri)
        val bitmapImage = FileManager.getBitmapFromUri(imageUri, contentResolver)
        val baos = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, baos)
        val pathName = "${System.currentTimeMillis()}.${FileManager.getFileExtension(imageUri, contentResolver)}"
        storage.getReference(STORAGE_REFERENCE_PATH).child(pathName).putBytes(baos.toByteArray())
            .addOnSuccessListener {
                val uri = it.storage.downloadUrl
                while (!uri.isComplete) { }
                note.imageUrl = uri.result.toString()
                Log.d(TAG, "saveImage: successfully")
                addNote(note)
            }
            .addOnFailureListener {
                Log.d(TAG, "saveImage: failed" + it.message)
            }
    }

    override fun addNote(note: Note) {
        firestore.collection(NOTES_COLLECTION_PATH).add(note)
            .addOnSuccessListener {
                Log.d(TAG, "addNote: successfully")
                allNotesFragmentPresenterListener?.onNoteAddedInFirestoreSuccessfully(note)
            }
            .addOnFailureListener {
                Log.d(TAG, "addNote failed: " + it.message)
                allNotesFragmentPresenterListener?.onNoteAddInFirestoreFailed()
            }
    }

    override fun changeIsCompleted(documentSnapshot: DocumentSnapshot, isCompleted: Boolean) {
        documentSnapshot.reference.update(IS_COMPLETED_FIELD_NAME, isCompleted)
            .addOnSuccessListener {
                Log.d(TAG, "changeIsCompleted: successfully")
            }.addOnFailureListener {
                Log.d(TAG, "changeIsCompleted: failed: ${it.message}")
            }
    }

    override fun changeIsArchived(documentSnapshot: DocumentSnapshot, isArchived: Boolean) {
        documentSnapshot.reference.update(IS_ARCHIVED_FIELD_NAME, isArchived)
            .addOnSuccessListener {
                Log.d(TAG, "changeIsArchived: successfully")
            }.addOnFailureListener {
                Log.d(TAG, "changeIsArchived: failed: ${it.message}")
            }
    }

    override fun updateNote(note: Note, documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.set(note)
            .addOnSuccessListener {
                Log.d(TAG, "updateNote: successfully")
                allNotesFragmentPresenterListener?.onNoteUpdatedInFirestoreSuccessfully()
            }
            .addOnFailureListener {
                Log.d(TAG, "updateNote: failed: ${it.message}")
                allNotesFragmentPresenterListener?.onNoteUpdateInFirestoreFailed()
            }
    }

    override fun deleteNote(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.delete()
            .addOnSuccessListener {
                Log.d(TAG, "deleteNote: successfully")
                allNotesFragmentPresenterListener?.onNoteDeletedInFirestoreSuccessfully(documentSnapshot)
                archivedNotesFragmentPresenterListener?.onNoteDeletedInFirestoreSuccessfully(documentSnapshot)
            }
            .addOnFailureListener {
                Log.d(TAG, "deleteNote: failed: ${it.message}")
                allNotesFragmentPresenterListener?.onNoteDeleteInFirestoreFailed()
                archivedNotesFragmentPresenterListener?.onNoteDeleteInFirestoreFailed()
            }
    }

    override fun deleteImageFromStorage(note: Note) {
        storage.getReferenceFromUrl(note.imageUrl!!).delete()
            .addOnSuccessListener {
                Log.d(TAG, "deleteImageFromStorage: successfully")
            }
            .addOnFailureListener {
                Log.d(TAG, "deleteImageFromStorage: failed: ${it.message}")
            }
    }

    override fun undoDelete(documentSnapshot: DocumentSnapshot) {
        val note = documentSnapshot.toObject(Note::class.java) as Note
        documentSnapshot.reference.set(note)
            .addOnSuccessListener {
                Log.d(TAG, "undoDelete: successfully")
            }.addOnFailureListener {
                Log.d(TAG, "undoDelete: failed")
            }
    }
}