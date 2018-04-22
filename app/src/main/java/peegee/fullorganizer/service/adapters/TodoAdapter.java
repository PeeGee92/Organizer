package peegee.fullorganizer.service.adapters;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoDB;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    List<TodoDB> todoDBList;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    boolean onBind;


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView =recyclerView;
    }

    public TodoAdapter(List<TodoDB> todoDBList){
        this.todoDBList = todoDBList;
        sort();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_item, viewGroup, false);
        adapter = this;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.cbTodoDone.setText(todoDBList.get(i).getTodoDescription());
        onBind = true;
        viewHolder.cbTodoDone.setChecked(todoDBList.get(i).isTodoDone());
        onBind = false;

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

    // Sort list by checked items
    private void sortAndNotify() {
        Collections.sort(todoDBList, listComparator);
        adapter.notifyDataSetChanged();
    }

    private void sort() {
        Collections.sort(todoDBList, listComparator);
    }

    private Comparator<TodoDB> listComparator = new Comparator<TodoDB>() {
        @Override
        public int compare(TodoDB t1, TodoDB t2) {
            if(t1.isTodoDone() == t2.isTodoDone()){
                return 0;
            }
            return t1.isTodoDone() ? -1 : 1;
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbTodoDone;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            cbTodoDone = itemView.findViewById(R.id.cbTodoDone);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            cbTodoDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!onBind) {
                        boolean checked = cbTodoDone.isChecked();
                        TodoDB item = todoDBList.get(getAdapterPosition());
                        item.setTodoDone(checked);
                        MainActivity.db.todoDAO().update(item);

                        sortAndNotify();
                    }
                }
            });
        }
    }
}
