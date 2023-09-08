package com.savita.aspapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.savita.aspapp.R;

import java.util.List;
import java.util.function.Consumer;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private Context context;
    private List<String> tags;
    private LayoutInflater inflater;
    private Consumer<String> onItemClick;

    public TagAdapter(Context context, List<String> tags, Consumer<String> onItemClick) {
        this.context = context;
        this.tags = tags;
        this.inflater = LayoutInflater.from(context);
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.note_item_view_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.tag.setText(tags.get(position));
        if(onItemClick != null) {
            holder.container.setOnClickListener((view) -> onItemClick.accept(tags.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }


    class TagViewHolder extends RecyclerView.ViewHolder {
        private TextView tag;
        private ConstraintLayout container;
        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.note_item_tag_item);
            container = itemView.findViewById(R.id.note_item_tag_container);
        }
    }
}
