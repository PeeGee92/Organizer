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
import java.text.SimpleDateFormat;
import java.util.List;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.ReminderDB;
import peegee.fullorganizer.reminder.AddReminder;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {

    List<ReminderDB> remindersDBList;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    private final View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            ReminderDB item = remindersDBList.get(itemPosition);

            // Load this item data in the next activity
            String tempId = item.getReminderId();
            Intent intent = new Intent(view.getContext(), AddReminder.class);
            intent.putExtra("REMINDER_ID", tempId);
            view.getContext().startActivity(intent);
        }
    };

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView =recyclerView;
    }

    public RemindersAdapter(List<ReminderDB> remindersDBList) {
        this.remindersDBList = remindersDBList;
    }

    @Override
    public RemindersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        view.setOnClickListener(myOnClickListener);
        adapter = this;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RemindersAdapter.ViewHolder holder, final int position) {

        ReminderDB remindersDB = remindersDBList.get(position);

        holder.tvTitle.setText(remindersDB.reminderTitle);
        holder.tvDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(remindersDB.reminderDate));
        holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(remindersDB.reminderDate));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete reminder")
                        .setMessage("Are you sure you want to delete this reminder permanently?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ReminderDB item = remindersDBList.get(position);

                                // Firebase
                                synchronized (MainActivity.FBLOCK) {
                                    MainActivity.reminderRef.child(item.getReminderId()).removeValue();
                                }

                                // Update RecyclerView
                                recyclerView.removeViewAt(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, remindersDBList.size());
                                adapter.notifyDataSetChanged();
                            }})
                        .setNegativeButton("Cancel", null).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return remindersDBList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvTime;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
}
