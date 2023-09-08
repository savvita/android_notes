package com.savita.aspapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.savita.aspapp.adapters.TagAdapter;
import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.controllers.NoteController;
import com.savita.aspapp.controllers.TagController;
import com.savita.aspapp.models.Note;
import com.savita.aspapp.models.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NoteActivity extends AppCompatActivity {
    private TextInputEditText title;
    private TextInputEditText date;
    private TextInputEditText text;
    private TextInputEditText tag;
    private Button saveBtn;
    private Button cancelBtn;
    private Integer id = null;
    private RecyclerView selectedTagsRecycler;
    private TagAdapter tagAdapter;
    private List<String> addedTags;
    private static final String LOG_TAG = "NoteActivity_tag";

    private Button addTagsBtn;
    private Button createTagBtn;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        preferences = getSharedPreferences(AppConfig.APP_PREFERENCES, MODE_PRIVATE);


        title = findViewById(R.id.note_title_text_input);
        date = findViewById(R.id.note_date_text_input);
        text = findViewById(R.id.note_text_text_input);

        saveBtn = findViewById(R.id.save_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        saveBtn.setOnClickListener(view -> save());
        cancelBtn.setOnClickListener(view -> cancel());

        addTagsBtn = findViewById(R.id.note_item_add_tags_btn);
        addTagsBtn.setOnClickListener(view -> openTagsDialog());

        addedTags = new ArrayList<>();

        selectedTagsRecycler = findViewById(R.id.note_selected_tags_recycler);
        tagAdapter = new TagAdapter(this, addedTags, null);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        selectedTagsRecycler.setLayoutManager(manager);
        selectedTagsRecycler.setAdapter(tagAdapter);

        createTagBtn = findViewById(R.id.note_item_create_tag_btn);
        createTagBtn.setOnClickListener(view -> createTag());

        tag = findViewById(R.id.note_item_new_tag_input);
        initialize();
    }

    private void createTag() {
        String tagText = tag.getText().toString();
        if(tagText == null || tagText.length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.required_tag), Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            Response<String> response = TagController.create(
                    tagText,
                    getSharedPreferences(AppConfig.APP_PREFERENCES, Context.MODE_PRIVATE)
                            .getString(AppConfig.APP_PREFERENCES_TOKEN, null));
            if(response == null) {
                tag.post(() -> Toast.makeText(this, R.string.saving_failed, Toast.LENGTH_LONG).show());
            } else {
                String savedTags = "";

                if(preferences.contains(AppConfig.APP_PREFERENCES_TAGS)) {
                    savedTags = preferences.getString(AppConfig.APP_PREFERENCES_TAGS, "");
                }
                if(savedTags.equals("")) {
                    savedTags = tagText;
                } else {
                    savedTags += ";" + tagText;
                }
                SharedPreferences.Editor editor = getSharedPreferences(AppConfig.APP_PREFERENCES, MODE_PRIVATE).edit();

                editor.putString(AppConfig.APP_PREFERENCES_TAGS, savedTags);
                editor.apply();
                addedTags.add(tagText);
                tag.post(() -> tagAdapter.notifyDataSetChanged());

                tag.setText("");
            }
        }).start();
    }

    private void openTagsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        builder.setTitle("Add tags");

        String[] temp = new String[0];

        if(preferences.contains(AppConfig.APP_PREFERENCES_TAGS)) {
            String tagsStr = preferences.getString(AppConfig.APP_PREFERENCES_TAGS, null);
            if(tagsStr != null) {
                temp = tagsStr.split(";");
            }
        }

        String[] tags = temp;

        boolean[] selectedTags = new boolean[tags.length];

        for(int i = 0; i < tags.length; i++) {
            selectedTags[i] = addedTags.indexOf(tags[i]) >= 0;
        }

        builder.setMultiChoiceItems(tags, selectedTags, (dialog, which, isChecked) -> {
            selectedTags[which] = isChecked;
        });

        builder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
            addedTags.clear();
            for (int i = 0; i < selectedTags.length; i++) {
                if (selectedTags[i]) {
                    addedTags.add(tags[i]);
                }
            }
            tagAdapter.notifyDataSetChanged();
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {});
        builder.setNeutralButton(getResources().getString(R.string.clear_all), (dialog, which) -> {
            Arrays.fill(selectedTags, false);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private void logOut() {
        SharedPreferences.Editor editor = getSharedPreferences(AppConfig.APP_PREFERENCES, MODE_PRIVATE).edit();
        editor.remove(AppConfig.APP_PREFERENCES_TOKEN);
        editor.remove(AppConfig.APP_PREFERENCES_TAGS);
        editor.commit();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void initialize() {
        Intent intent = getIntent();
        if(intent.hasExtra(AppConfig.NOTE_ID)) {
            id = intent.getIntExtra(AppConfig.NOTE_ID, -1);
        }
        if(intent.hasExtra(AppConfig.NOTE_TITLE)) {
            title.setText(intent.getStringExtra(AppConfig.NOTE_TITLE));
        }
        if(intent.hasExtra(AppConfig.NOTE_DATE)) {
            date.setVisibility(View.VISIBLE);
            date.setText(intent.getStringExtra(AppConfig.NOTE_DATE));
        } else {
            date.setVisibility(View.GONE);
        }
        if(intent.hasExtra(AppConfig.NOTE_TEXT)) {
            text.setText(intent.getStringExtra(AppConfig.NOTE_TEXT));
        }
        if(intent.hasExtra(AppConfig.NOTE_TAGS)) {
            String tagsStr = intent.getStringExtra(AppConfig.NOTE_TAGS);
            Log.d(LOG_TAG, "Tags: " + tagsStr);
            if(tagsStr != null && tagsStr.trim().length() != 0) {
                addedTags.addAll(Arrays.stream(tagsStr.split(";")).collect(Collectors.toList()));
                tagAdapter.notifyDataSetChanged();
            }
        }
    }

    private void save() {
        Note note = createNote();

        if(id != null && id >= 0) {
            note.setId(id);
            update(note);
        } else {
            create(note);
        }
    }

    private Note createNote() {
        Note note = new Note();
        note.setTitle(title.getText().toString());
        note.setText(text.getText().toString());
        note.getTags().clear();
        note.getTags().addAll(addedTags);
        note.prepare();
        return note;
    }

    private void update(Note note) {
        List<String> errors = checkInputs(note);
        if(errors.size() > 0) {
            Toast.makeText(this, String.join("\n", errors), Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            Response<Note> response = NoteController.update(
                    note,
                    getSharedPreferences(AppConfig.APP_PREFERENCES, Context.MODE_PRIVATE)
                        .getString(AppConfig.APP_PREFERENCES_TOKEN, null));
            if(response == null) {
                saveBtn.post(() -> Toast.makeText(this, R.string.saving_failed, Toast.LENGTH_LONG).show());
            } else {
                openNotesListActivity();
            }
        }).start();
    }
    private void create(Note note) {
        List<String> errors = checkInputs(note);
        if(errors.size() > 0) {
            Toast.makeText(this, String.join("\n", errors), Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            Response<Note> response = NoteController.create(
                    note,
                    getSharedPreferences(AppConfig.APP_PREFERENCES, Context.MODE_PRIVATE)
                            .getString(AppConfig.APP_PREFERENCES_TOKEN, null));
            if(response == null) {
                saveBtn.post(() -> Toast.makeText(this, R.string.saving_failed, Toast.LENGTH_LONG).show());
            } else {
                openNotesListActivity();
            }
        }).start();
    }

    private List<String> checkInputs(Note note) {
        List<String> errors = new ArrayList<>();

        if(note.getTitle() == null || note.getTitle().length() == 0) {
            errors.add(getResources().getString(R.string.required_title));
        }

        return errors;
    }

    private void cancel() {
        openNotesListActivity();
    }

    private void openNotesListActivity() {
        Intent intent = new Intent(this, NotesListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}