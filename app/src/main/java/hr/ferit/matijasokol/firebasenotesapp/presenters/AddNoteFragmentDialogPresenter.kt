package hr.ferit.matijasokol.firebasenotesapp.presenters

import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractor
import hr.ferit.matijasokol.firebasenotesapp.firebase.FirebaseInteractorImpl
import hr.ferit.matijasokol.firebasenotesapp.ui.fragmentDialogs.addNoteFragmentDialog.AddNoteFragmentDialogContract

class AddNoteFragmentDialogPresenter(private val view: AddNoteFragmentDialogContract.View) : AddNoteFragmentDialogContract.Presenter {

    private val firebaseInteractor: FirebaseInteractor by lazy { FirebaseInteractorImpl.HOLDER.getInstance() }

    override fun getCurrentUser() {
        val user = firebaseInteractor.getCurrentUser()
        view.onGetCurrentUser(user)
    }
}