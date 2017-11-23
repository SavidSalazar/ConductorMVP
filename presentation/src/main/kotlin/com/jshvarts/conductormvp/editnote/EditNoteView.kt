package com.jshvarts.conductormvp.editnote

import android.support.design.widget.TextInputEditText
import android.view.View
import android.view.inputmethod.EditorInfo
import butterknife.BindView
import butterknife.OnEditorAction
import com.jshvarts.conductormvp.NotesApp
import com.jshvarts.conductormvp.R
import com.jshvarts.conductormvp.mvp.BaseView
import com.jshvarts.notedomain.Note
import javax.inject.Inject

class EditNoteView : BaseView(), EditNoteContract.View {

    companion object {
        const val EXTRA_NOTE_ID = "EditNoteView.noteId"
    }

    @Inject
    lateinit var presenter: EditNotePresenter

    @BindView(R.id.edit_note_edit_text)
    lateinit var editNoteEditText: TextInputEditText

    override fun onAttach(view: View) {
        super.onAttach(view)

        DaggerEditNoteComponent.builder()
                .appComponent(NotesApp.component)
                .editNoteModule(EditNoteModule())
                .build()
                .inject(this)

        presenter.attachView(this)
        presenter.loadNote(args.getLong(EXTRA_NOTE_ID))
    }

    override fun onLoadNoteSuccess(note: Note) {
        editNoteEditText.setText(note.noteText)
    }

    override fun onEditNoteSuccess() {
        showMessage(R.string.note_edit_success)
        router.popCurrentController()
    }

    override fun onEditNoteError() {
        showMessage(R.string.note_edit_failed)
    }

    override fun onNoteLookupError() {
        showMessage(R.string.note_edit_lookup_failed)
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        presenter.detachView()
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun getLayoutId() = R.layout.edit_note

    override fun getToolbarTitleId() = R.string.screen_title_edit_note

    @OnEditorAction(R.id.edit_note_edit_text)
    override fun onEditNoteAction(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            this.view?.hideKeyboard()
            presenter.editNote(args.getLong(EXTRA_NOTE_ID), editNoteEditText.text.toString())
            return true
        }
        return false
    }

    override fun onNoteValidationFailed() {
        showMessage(R.string.note_add_validation_failed)
    }
}