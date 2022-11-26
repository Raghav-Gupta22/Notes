package com.personal.notes.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorWindow;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.notes.R;
import com.personal.notes.data.entities.Note;
import com.personal.notes.databinding.ActivityMainBinding;
import com.personal.notes.ui.addnote.AddNoteActivity;
import com.personal.notes.ui.auth.AuthActivity;
import com.personal.notes.ui.notedetails.NoteDetailsActivity;
import com.personal.notes.ui.recyclerview.NotesAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.personal.notes.utils.Constants.NOTE_ID;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnItemClickListener {

    private ActivityMainBinding binding;

    MainActivityViewModel mainActivityViewModel;
    private MainActivityClickHandlers mainActivityClickHandlers;
    ArrayList<Note> notes;
    RecyclerView recyclerView;
    NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityClickHandlers = new MainActivityClickHandlers(this);
        binding.setMainActivityClickHandlers(mainActivityClickHandlers);
        setContentView(binding.getRoot());

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        recyclerView = binding.rvUserNotes;
        loadUserNotes(mainActivityViewModel.getCurrentUserId());

        setSupportActionBar(binding.toolbar);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void loadRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        notesAdapter = new NotesAdapter();
        recyclerView.setAdapter(notesAdapter);
        notesAdapter.setListener(MainActivity.this);
        notesAdapter.setNotes(notes);
    }

    private void loadUserNotes(int userId) {
        mainActivityViewModel.geUserNotes(userId).observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                MainActivity.this.notes = (ArrayList<Note>) notes;
                loadRecyclerView();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("LOGOUT")
                    .setMessage("Do you really want to logout?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            mainActivityViewModel.logout();
                            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mainActivityViewModel.isUserLoggedIn()) {
            startLogin();
        }
    }

    private void startLogin() {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(Note note) {
//        Toast.makeText(this, note.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, NoteDetailsActivity.class);
        intent.putExtra(NOTE_ID, note.noteId);
        startActivity(intent);

    }

    public class MainActivityClickHandlers {
        Context context;

        public MainActivityClickHandlers(Context context) {
            this.context = context;
        }

        public void onFabClicked(View view) {

            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);

        }
    }
}