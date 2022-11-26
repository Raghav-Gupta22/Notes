package com.personal.notes.ui.addnote;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.personal.notes.data.dbutils.ImageList;
import com.personal.notes.data.entities.Note;
import com.personal.notes.repository.NotesRepository;
import com.personal.notes.utils.BitmapResizer;

import java.util.ArrayList;

import static com.personal.notes.utils.Constants.NOT_A_NOTE_ID;

public class AddNoteActivityViewModel extends AndroidViewModel {
    Integer noteId = null;
    public String title;
    public String description;
    ArrayList<Bitmap> imageLists;
    NotesRepository notesRepository;
    String TAG = AddNoteActivityViewModel.class.getSimpleName();

    public AddNoteActivityViewModel(@NonNull Application application) {
        super(application);
        imageLists = new ArrayList<>();
        notesRepository = new NotesRepository(application);
    }


    public void saveNote() {

        if (noteId == NOT_A_NOTE_ID) {
            Log.d(TAG, "saveNote: new " + noteId);
            Note note = new Note(null, notesRepository.getCurrentUserId(), this.title, this.description, new ImageList(imageLists));

            notesRepository.addNote(note);
        } else {
            Log.d(TAG, "saveNote: updaate " + noteId);
            Note note = new Note(noteId, notesRepository.getCurrentUserId(), this.title, this.description, new ImageList(imageLists));

            notesRepository.updateNote(note);
        }
    }


    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Bitmap> getImageLists() {
        return imageLists;
    }

    public void setImageLists(ArrayList<Bitmap> imageLists) {
        this.imageLists = imageLists;
    }

    public void appendBitmap(Bitmap bitmap, int maxWidth) {
        if (imageLists.size() < 10)
            imageLists.add(BitmapResizer.getResizedBitmap(bitmap, maxWidth));
    }

    public LiveData<Note> getNote() {
        return notesRepository.getCurrentNote(this.noteId);
    }
}
