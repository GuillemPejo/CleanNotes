package me.guillem.cleannotes.listeners;

import me.guillem.cleannotes.entities.Note;

/**
 * * Created by Guillem on 26/12/20.
 */
public interface NotesListener {

    void onNoteClicked(Note note, int position);
}
