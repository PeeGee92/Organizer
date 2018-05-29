package peegee.fullorganizer.service.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.NotesDB;
import peegee.fullorganizer.notes.AddNote;

/**
 * Recycler view adapter class for notes
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    List<NotesDB> notesDBList;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    /**
     * OnClick listener method for recycler view
     */
    private final View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            peegee.fullorganizer.firebase_db.NotesDB item = notesDBList.get(itemPosition);

            // Load this item data in the next activity
            String tempId = item.getNoteId();
            Intent intent = new Intent(view.getContext(), AddNote.class);
            intent.putExtra("NOTE_ID", tempId);
            view.getContext().startActivity(intent);
        }
    };

    /**
     * onAttachedRecyclerView method
     * <p>
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView =recyclerView;
    }

    /**
     * Non-default constructor
     * <p>
     * @param notesDBList the notes list
     */
    public NotesAdapter(List<NotesDB> notesDBList) {
        this.notesDBList = notesDBList;
    }

    /**
     * onCreateViewHolder method
     * <p>
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        view.setOnClickListener(myOnClickListener);
        adapter = this;
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder method
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final NotesAdapter.ViewHolder holder, final int position) {
        if (notesDBList.get(position).noteTitle.isEmpty()) {
            holder.tvTitle.setText(notesDBList.get(position).noteText);
        }
        else {
            holder.tvTitle.setText(notesDBList.get(position).noteTitle);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete note")
                        .setMessage("Are you sure you want to delete this note permanently?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                NotesDB item = notesDBList.get(position);

                                // Firebase
                                synchronized (MainActivity.FBLOCK) {
                                    MainActivity.notesRef.child(item.getNoteId()).removeValue();
                                }

                                // Update RecyclerView
                                notesDBList.remove(position);
                                recyclerView.removeViewAt(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, notesDBList.size());
                                adapter.notifyDataSetChanged();
                            }})
                        .setNegativeButton("Cancel", null).show();
            }
        });
    }

    /**
     * getItemCount method
     * <p>
     * @return the adapter list size
     */
    @Override
    public int getItemCount() {
        if (notesDBList == null)
            return 0;
        else
            return notesDBList.size();
    }

    /**
     * View holder inner class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageButton btnDelete;

        /**
         * Constructor
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
