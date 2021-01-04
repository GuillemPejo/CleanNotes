package me.guillem.cleannotes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import me.guillem.cleannotes.R;
import me.guillem.cleannotes.adapters.NotesAdapter;
import me.guillem.cleannotes.callbacks.CallBackNoteTouch;
import me.guillem.cleannotes.callbacks.MyNoteTouchHelperCallback;
import me.guillem.cleannotes.database.NotesDatabase;
import me.guillem.cleannotes.entities.Note;
import me.guillem.cleannotes.listeners.NotesListener;

import static me.guillem.cleannotes.adapters.NotesAdapter.SPAN_COUNT_ONE;
import static me.guillem.cleannotes.adapters.NotesAdapter.SPAN_COUNT_TWO;


public class MainActivity extends AppCompatActivity implements NotesListener, CallBackNoteTouch {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;


    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;
    private StaggeredGridLayoutManager notesLayoutManager;

    private ImageView changeLayoutList;
    private View principalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        principalLayout = findViewById(R.id.principalLayout);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener((v) -> {
            startActivityForResult(
                    new Intent(getApplicationContext(), CreateNoteActivity.class),
                    REQUEST_CODE_ADD_NOTE
            );
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT_TWO,StaggeredGridLayoutManager.VERTICAL);
        //notesRecyclerView.setLayoutManager(
         //       new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        //);
        notesRecyclerView.setLayoutManager(notesLayoutManager);

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);
        ItemTouchHelper.Callback callback = new MyNoteTouchHelperCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(notesRecyclerView);

        getNotes(REQUEST_CODE_SHOW_NOTE,false);

        changeLayoutList = findViewById(R.id.changeLayoutList);

        changeLayoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchLayout();
                switchIcon();
            }
        });



        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancellTime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size()!=0){
                    notesAdapter.searchNotes(s.toString());
                }

            }
        });
    }

    private void switchIcon() {
        if (notesLayoutManager.getSpanCount () == SPAN_COUNT_TWO) {
            changeLayoutList.setImageResource(R.drawable.ic_grid);

        }else {
            changeLayoutList.setImageResource(R.drawable.ic_list);
        }
    }

    private void switchLayout() {
        if (notesLayoutManager.getSpanCount () == SPAN_COUNT_ONE) {
            notesLayoutManager.setSpanCount(SPAN_COUNT_TWO);
        }else {
            notesLayoutManager.setSpanCount(SPAN_COUNT_ONE);
        }
        notesAdapter.notifyItemRangeChanged(0, notesAdapter.getItemCount());
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);

    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_CODE_SHOW_NOTE) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else if(requestCode== REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);
                    if (isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        }else if (requestCode == REQUEST_CODE_UPDATE_NOTE  && resultCode == RESULT_OK){
            if (data!=null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }


    @Override
    public void itemTouchOnMove(int oldPositions, int newPosition) {
        noteList.add(newPosition, noteList.remove(oldPositions));
        notesAdapter.notifyItemMoved(oldPositions, newPosition);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
        String name = noteList.get(viewHolder.getAdapterPosition()).getTitle();

        final Note deletedNote = noteList.get(viewHolder.getAdapterPosition());
        final int deletedIndex = viewHolder.getAdapterPosition();

        notesAdapter.removeNote(viewHolder.getAdapterPosition());
        Snackbar snackbar = Snackbar.make(principalLayout, R.string.deleted_note, Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesAdapter.restoreNote(deletedNote, deletedIndex);

            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }
}