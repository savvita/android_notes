package com.savita.aspapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.savita.aspapp.adapters.NoteAdapter;
import com.savita.aspapp.adapters.TagAdapter;
import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.controllers.NoteController;
import com.savita.aspapp.models.Note;
import com.savita.aspapp.models.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NotesListActivity extends AppCompatActivity {
    private List<Note> notes;
    private List<Note> selectedNotes;
    private ListView notesView;
    private TextView noContentTextView;
    private NoteAdapter adapter;
    private SharedPreferences preferences;
    private ImageButton addBtn;
    private TextInputEditText searchTxt;
    private RecyclerView tagsRecycler;
    private TagAdapter tagAdapter;
    private List<String> tags = new ArrayList<>();
    private String selectedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        notes = new ArrayList<>();
        selectedNotes = new ArrayList<>();

        notesView = findViewById(R.id.notes_list_view);
        noContentTextView = findViewById(R.id.no_content_text_view);
        addBtn = findViewById(R.id.add_note_btn);
        tagsRecycler = findViewById(R.id.note_tags_recycler_view);

        searchTxt = findViewById(R.id.search_text_input);
        searchTxt.addTextChangedListener(new SearchTextWatcher());
        
        addBtn.setOnClickListener(view -> openEmptyNoteActivity());

        adapter = new NoteAdapter(this, R.layout.note_item_view, selectedNotes, (tag) -> filterByTag(tag));
        adapter.setOnDataChanged(this::refresh);
        notesView.setAdapter(adapter);

        preferences = getSharedPreferences(AppConfig.APP_PREFERENCES, Context.MODE_PRIVATE);
        initializeTags();
    }

    private void filterByTag(String tag) {
        String search = searchTxt.getText().toString().toLowerCase();
        selectedTag = tag;

        Predicate<Note> findText = (note) -> search == null || search.trim().length() == 0 ? true : note.getText().toLowerCase().contains(search.trim()) || note.getTitle().toLowerCase().contains(search.trim());

        String all_tags = getResources().getString(R.string.all);
        selectedNotes.clear();
        if(tag.equals(all_tags)) {
            selectedNotes.addAll(notes.stream().filter(note -> findText.test(note)).collect(Collectors.toList()));
        } else {
            selectedNotes.addAll(notes.stream()
                    .filter(note -> note.getTags().stream()
                            .filter(t -> t.equals(tag))
                            .findFirst()
                            .isPresent()
                    )
                            .filter(note -> findText.test(note))
                    .collect(Collectors.toList())
            );
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_log_out) {
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeTags() {
        tags.clear();
        tags.add(getResources().getString(R.string.all));

        if(preferences.contains(AppConfig.APP_PREFERENCES_TAGS)) {
            String tagsStr = preferences.getString(AppConfig.APP_PREFERENCES_TAGS, null);
            if(tagsStr != null) {
                tags.addAll(Arrays.stream(tagsStr.split(";")).collect(Collectors.toList()));
            }
        }

        tagAdapter = new TagAdapter(this, tags, (tag) -> filterByTag(tag));

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        tagsRecycler.setLayoutManager(manager);
        tagsRecycler.setAdapter(tagAdapter);
    }

    private void openEmptyNoteActivity() {
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        SharedPreferences.Editor editor = getSharedPreferences(AppConfig.APP_PREFERENCES, MODE_PRIVATE).edit();
        editor.remove(AppConfig.APP_PREFERENCES_TOKEN);
        editor.remove(AppConfig.APP_PREFERENCES_TAGS);
        editor.commit();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        refresh();
        initializeTags();
        super.onResume();
    }

    private void refresh() {
        new Thread(() -> {
            Response<List<Note>> response = NoteController.get(preferences.getString(AppConfig.APP_PREFERENCES_TOKEN, null));
            if(response == null || response.getHits() == 0) {
                noContentTextView.post(() -> noContentTextView.setVisibility(View.VISIBLE));
                notesView.post(() -> notesView.setVisibility(View.GONE));
            } else {
                noContentTextView.post(() -> noContentTextView.setVisibility(View.GONE));
                notesView.post(() -> notesView.setVisibility(View.VISIBLE));
                notes.clear();
                notes.addAll(response.getValue());
                notesView.post(() -> search());
            }

        }).start();
    }

    private void search() {
        if(selectedTag != null) {
            filterByTag(selectedTag);
        } else {
            if(searchTxt.getText().length() == 0) {
                selectedNotes.clear();
                selectedNotes.addAll(notes);
            } else {
                String search = searchTxt.getText().toString().toLowerCase();
                selectedNotes.clear();
                selectedNotes.addAll(notes.stream().filter(x ->
                        x.getTitle().toLowerCase()
                                .contains(search) || (x.getText() != null && x.getText().toLowerCase().contains(search)))
                                .collect(Collectors.toList()));
            }

            adapter.notifyDataSetChanged();
        }
    }

    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            search();
        }
    }
}