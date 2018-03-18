package peegee.fullorganizer.service;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoDB;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    List<TodoDB> todoDBList;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView =recyclerView;
    }

    public TodoAdapter(List<TodoDB> todoDBList){
        this.todoDBList = todoDBList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_item, viewGroup, false);
        adapter = this;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.tvTodoDescription.setText(todoDBList.get(i).getTodoDescription());
        viewHolder.cbTodoDone.setChecked(todoDBList.get(i).isTodoDone());
        viewHolder.cbTodoDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean checked = viewHolder.cbTodoDone.isChecked();
                Log.d("CHECKED_CHANGED", "checked: " + checked);
                TodoDB item = todoDBList.get(i);
                item.setTodoDone(checked);
                MainActivity.db.todoDAO().update(item);
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete Todo Item")
                        .setMessage("Are you sure you want to delete this todo item permanently?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                TodoDB item = todoDBList.get(i);

                                // Database
                                MainActivity.db.todoDAO().delete(item);

                                // Update RecyclerView
                                todoDBList.remove(i);
                                recyclerView.removeViewAt(i);
                                adapter.notifyItemRemoved(i);
                                adapter.notifyItemRangeChanged(i, todoDBList.size());
                                adapter.notifyDataSetChanged();
                            }})
                        .setNegativeButton("Cancel", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoDBList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbTodoDone;
        TextView tvTodoDescription;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            cbTodoDone = itemView.findViewById(R.id.cbTodoDone);
            tvTodoDescription = itemView.findViewById(R.id.tvTodoDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
