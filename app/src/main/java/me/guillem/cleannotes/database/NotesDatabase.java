package me.guillem.cleannotes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import me.guillem.cleannotes.dao.NoteDAO;
import me.guillem.cleannotes.entities.Note;

/**
 * * Created by Guillem on 25/12/20.
 */
@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase notesDatabase;

    public static synchronized NotesDatabase getDabase(Context context){
        if (notesDatabase == null){
            notesDatabase = Room.databaseBuilder(
                    context,
                    NotesDatabase.class,
                    "notes_db"
            ).build();

        }
        return notesDatabase;
    }

    public abstract NoteDAO noteDao();
}
