package hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseUser
import hr.ferit.matijasokol.firebasenotesapp.R
import hr.ferit.matijasokol.firebasenotesapp.common.displayToast
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractor
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.model.Note
import hr.ferit.matijasokol.firebasenotesapp.presenters.AddNoteFragmentDialogPresenter
import hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs.addNoteFragmentDialog.AddNoteFragmentDialogContract
import hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs.base.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_new_task.*
import kotlinx.android.synthetic.main.fragment_dialog_new_task.view.*

class AddNoteFragmentDialog : BaseDialogFragment(), AddNoteFragmentDialogContract.View {

    interface OnNoteAddedFromDialogListener {
        fun onNoteAddedFromDialog(note: Note)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 2
        fun newInstance(): AddNoteFragmentDialog = AddNoteFragmentDialog()
    }

    private val TAG = "[DEBUG] AddNoteFragment"
    private var imageUri: Uri? = null
    private var onNoteAddedFromDialogListener: OnNoteAddedFromDialogListener? = null
    private val presenter: AddNoteFragmentDialogContract.Presenter by lazy {
        AddNoteFragmentDialogPresenter(this)
    }

    override fun getLayoutResourceId(): Int = R.layout.fragment_dialog_new_task

    override fun setUpUi() {
        setListeners()
    }

    //region UI control
    private fun setListeners() {
        buttonChoseImage.setOnClickListener { handleChooseImage() }
        buttonSaveNote.setOnClickListener { handleSaveNote() }
    }

    fun setOnNoteAddedFromDialogListener(onNoteAddedFromDialogListener: OnNoteAddedFromDialogListener) {
        this.onNoteAddedFromDialogListener = onNoteAddedFromDialogListener
    }

    private fun handleChooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_IMAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    imageUri = data.data as Uri
                    Log.d(TAG, "onActivityResult: $imageUri")
                    with(layoutView) {
                        imageMark.setImageResource(R.drawable.ic_uploaded)
                    }
                } else {
                    val response = IdpResponse.fromResultIntent(data)
                    if (response == null) {
                        Log.d(TAG, "onActivityResult: user has cancelled pick image request")
                    } else {
                        Log.d(TAG, "onActivityResult: " + response.error)
                    }
                }
            }
        }
    }

    private fun checkForValidInputs(): Boolean {
        if (editTextTitle.text.isEmpty() || editTextDescription.text.isEmpty()) {
            return false
        }

        return true
    }

    private fun handleSaveNote() {
        if (!checkForValidInputs()) {
            activity?.displayToast(getString(R.string.data_missing))
            return
        }

        presenter.getCurrentUser()
    }
    //endregion

    //region Presenter callbacks
    override fun onGetCurrentUser(firebaseUser: FirebaseUser?) {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val userId = firebaseUser!!.uid

        val note = Note(title, description, userId)
        if (imageUri != null) {
            note.imageUri = imageUri.toString()
            imageUri = null
        }

        Log.d(TAG, "handleSaveNote: " + note.imageUri)

        onNoteAddedFromDialogListener?.onNoteAddedFromDialog(note)

        dismiss()
    }
    //endregion
}