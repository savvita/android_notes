package com.savita.aspapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.savita.aspapp.NoteActivity;
import com.savita.aspapp.R;
import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.controllers.NoteController;
import com.savita.aspapp.models.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NoteAdapter extends ArrayAdapter<Note> {
    private List<Note> notes;
    private Context context;
    private LayoutInflater inflater;
    private int layout;
    private Notification onDataChanged;
    private static final String LOG_TAG = "NoteAdapter_tag";
    private Consumer<String> onTagClick;

    public NoteAdapter(@NonNull Context context, int resource, List<Note> notes, Consumer<String> onTagClick) {
        super(context, resource);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.notes = notes;
        this.onTagClick = onTagClick;
    }

    public Notification getOnDataChanged() {
        return onDataChanged;
    }

    public void setOnDataChanged(Notification onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent) {
        ViewHolder holder;

        if(convertedView == null) {
            convertedView = inflater.inflate(layout, parent, false);
            holder = new ViewHolder(convertedView);
            convertedView.setTag(holder);
        } else {
            holder = (ViewHolder) convertedView.getTag();
        }

        Note note = notes.get(position);

        holder.title.setText(note.getTitle());
        holder.date.setText(AppConfig.dateFormat.format(note.getDate()));
        holder.removeBtn.setOnClickListener(view -> removeNote(note));
        holder.container.setOnClickListener(view -> openNoteActivity(note));

        if(note.getTags().size() > 0) {
            holder.tagsRecycler.setVisibility(View.VISIBLE);
            holder.tags.clear();
            holder.tags.addAll(note.getTags());
        } else {
            holder.tagsRecycler.setVisibility(View.GONE);
        }
        holder.adapter.notifyDataSetChanged();

        return convertedView;
    }

    private void openNoteActivity(Note note) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(AppConfig.NOTE_ID, note.getId());
        intent.putExtra(AppConfig.NOTE_TITLE, note.getTitle());
        intent.putExtra(AppConfig.NOTE_DATE, AppConfig.dateFormat.format(note.getDate()));
        intent.putExtra(AppConfig.NOTE_TEXT, note.getText());
        intent.putExtra(AppConfig.NOTE_TAGS, String.join(";", note.getTags()));
        context.startActivity(intent);
    }

    private void removeNote(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.delete_note))
                .setMessage(context.getResources().getString(R.string.delete_note_confirmation))
                .setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> {
                    new Thread(() -> {
                        NoteController.remove(
                                note,
                                context.getSharedPreferences(AppConfig.APP_PREFERENCES, Context.MODE_PRIVATE)
                                        .getString(AppConfig.APP_PREFERENCES_TOKEN, null));
                        if (onDataChanged != null) {
                            onDataChanged.Notify();
                        }
                    }).start();
                })
                .setNegativeButton(context.getResources().getString(R.string.no), (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    private class ViewHolder {
        private TextView title;
        private TextView date;
        private ConstraintLayout container;
        private ImageButton removeBtn;
        private RecyclerView tagsRecycler;
        private TagAdapter adapter;
        private List<String> tags;
        private ViewHolder(View view) {
            title = view.findViewById(R.id.note_item_title);
            date = view.findViewById(R.id.note_item_date);
            container = view.findViewById(R.id.note_item_container);
            removeBtn = view.findViewById(R.id.note_item_remove_btn);
            tagsRecycler = view.findViewById((R.id.note_item_tags_recycler));
            tags = new ArrayList<>();
            adapter = new TagAdapter(context, tags, onTagClick);

            RecyclerView.LayoutManager manager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            tagsRecycler.setLayoutManager(manager);
            tagsRecycler.setAdapter(adapter);
        }
    }

    @FunctionalInterface
    public interface Notification {
        void Notify();
    }
}
