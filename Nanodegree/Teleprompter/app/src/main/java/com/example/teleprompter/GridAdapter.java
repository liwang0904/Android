package com.example.teleprompter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.DocumentGridViewHolder> {
    private List<Document> documents;
    private DocumentGridAdapterCallbacks callback;
    private Cursor cursor;
    private int pinnedId;

    public GridAdapter(DocumentGridAdapterCallbacks callback, Cursor cursor) {
        documents = new ArrayList<>();
        this.callback = callback;
        this.cursor = cursor;
        pinnedId = SharedPreferenceUtils.getPinnedId((Context) callback);
    }

    public interface DocumentGridAdapterCallbacks {
        void onDocumentClicked(Document document);
    }

    public class DocumentGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.doc_title)
        TextView title;
        @BindView(R.id.preview)
        TextView preview;
        @BindView(R.id.grid_item_constraint_layout)
        ConstraintLayout layoutContainer;
        @BindView(R.id.pin_button)
        ImageView pinButton;
        @BindInt(R.integer.title_size_small)
        int titleSizeSmall;
        @BindInt(R.integer.title_size_medium)
        int titleSizeMedium;
        @BindInt(R.integer.title_size_large)
        int titleSizeLarge;

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Document document = documents.get(position);
            int id = view.getId();
            if (id == R.id.grid_card_view) callback.onDocumentClicked(document);
            else if (id == R.id.pin_button) {
                SharedPreferenceUtils.setPinnedId(view.getContext(), document.getId());
                pinnedId = document.getId();
                notifyDataSetChanged();
                WidgetService.updateWidget(view.getContext());
            }
        }

        public DocumentGridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
            pinButton.setOnClickListener(this);
        }
    }

    @NonNull
    @Override
    public DocumentGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new DocumentGridViewHolder(LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentGridViewHolder holder, int position) {
        Document document = documents.get(position);
        setTitleSize(document, holder);

        holder.title.setText(document.getTitle());
        holder.preview.setText(document.getText());
        holder.itemView.setTag(document.getId());

        if (document.getId() == pinnedId) holder.pinButton.setImageResource(R.drawable.ic_pin_selected);
        else holder.pinButton.setImageResource(R.drawable.ic_pin_unselected);
        holder.layoutContainer.setBackgroundColor(Color.WHITE);
    }

    private void setTitleSize(Document document, DocumentGridViewHolder holder) {
        String title = document.getTitle();
        int size;
        if (title == null || title.isEmpty()) return;
        int length = title.length();
        if (length > 30) size = holder.titleSizeSmall;
        else if (length > 16) size = holder.titleSizeMedium;
        else size = holder.titleSizeLarge;

        holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void move(int position1, int position2) {
        Collections.swap(documents, position1, position2);
        notifyItemMoved(position1, position2);
    }

    public void deletePosition(int position) {
        notifyItemRemoved(position);
    }

    public Document getDocumentAtPosition(int position) {
        return documents.get(position);
    }

    @Override
    public int getItemCount() {
        if (documents == null) return 0;
        return documents.size();
    }

    private void populateListFromCursor() {
        if (cursor == null) documents = null;
        else {
            documents = new ArrayList<>();
            while (cursor.moveToNext()) {
                Document d = new Document(cursor);
                documents.add(d);
            }
        }
        notifyDataSetChanged();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor = null;
        cursor = newCursor;
        populateListFromCursor();
    }
}