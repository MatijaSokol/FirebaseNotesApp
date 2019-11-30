package hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs

import com.google.firebase.firestore.DocumentSnapshot
import hr.ferit.matijasokol.firebasenotesapp.R
import hr.ferit.matijasokol.firebasenotesapp.common.displayToast
import hr.ferit.matijasokol.firebasenotesapp.model.Note
import hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs.base.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_update_task.*

class UpdateNoteFragmentDialog : BaseDialogFragment() {

    interface OnNoteUpdatedFromDialogListener {
        fun onNoteUpdatedFromDialog(note: Note, documentSnapshot: DocumentSnapshot)
    }

    companion object {
        fun newInstance(): UpdateNoteFragmentDialog = UpdateNoteFragmentDialog()
    }

    private lateinit var documentSnapshot: DocumentSnapshot
    private lateinit var note: Note
    private var onNoteUpdatedFromDialogListener: OnNoteUpdatedFromDialogListener? = null

    fun setOnNoteUpdatedFromDialogListener(onNoteUpdatedFromDialogListener: OnNoteUpdatedFromDialogListener) {
        this.onNoteUpdatedFromDialogListener = onNoteUpdatedFromDialogListener
    }

    fun setDocumentSnapshot(documentSnapshot: DocumentSnapshot) {
        this.documentSnapshot = documentSnapshot
    }

    override fun getLayoutResourceId(): Int = R.layout.fragment_dialog_update_task

    override fun setUpUi() {
        note = documentSnapshot.toObject(Note::class.java)!!
        setListeners()
        setEditTexts()
    }

    private fun setEditTexts() {
        editTextTitle.setText(note.title)
        editTextDescription.setText(note.desciption)
    }

    private fun setListeners() {
        buttonUpdateNote.setOnClickListener { handleUpdateNote() }
    }

    private fun checkForValidInputs(): Boolean {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            return false
        }

        return true
    }

    private fun checkForSameInputs(): Boolean {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if (title == note.title && description == note.desciption) {
            return false
        }

        return true
    }

    private fun handleUpdateNote() {
        if (!checkForValidInputs()) {
            activity?.displayToast(getString(R.string.data_missing))
            return
        }

        if (!checkForSameInputs()) {
            dismiss()
            return
        }

        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        note.title = title
        note.desciption = description

        onNoteUpdatedFromDialogListener?.onNoteUpdatedFromDialog(note, documentSnapshot)

        dismiss()
    }
}