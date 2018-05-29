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
import peegee.fullorganizer.firebase_db.TodoItemDB;
import peegee.fullorganizer.todo.AddTodoList;

/**
 * Recycler view adapter class for to-do items
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    List<TodoItemDB> todoDBList;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    boolean onBind; // To handle exception

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
     * @param todoDBList the to-do list items list
     */
    public TodoAdapter(List<TodoItemDB> todoDBList){
        this.todoDBList = todoDBList;
        sort();
    }

    /**
     * onCreateViewHolder method
     * <p>
     * @param viewGroup
     * @param i view type
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_item, viewGroup, false);
        adapter = this;
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder method
     * @param viewHolder
     * @param i position
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.cbTodoDone.setText(todoDBList.get(i).todoDescription);
        onBind = true;
        viewHolder.cbTodoDone.setChecked(todoDBList.get(i).done);
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
                                TodoItemDB item = todoDBList.get(i);

                                // Firebase
                                synchronized (MainActivity.FBLOCK) {
                                    if (item.getItemId() != null)
                                        MainActivity.todoItemRef.child(item.getItemId()).removeValue();
                                }
                                AddTodoList.addedItemsList.remove(item);
                                if (MainActivity.todoItemsList.contains(item))
                                    MainActivity.todoItemsList.remove(item);

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

    /**
     * getItemCount method
     * <p>
     * @return the adapter list size
     */
    @Override
    public int getItemCount() {
        if (todoDBList != null)
            return todoDBList.size();
        else
            return 0;
    }

    // Sort list by checked items
    private void sortAndNotify() {
        Collections.sort(todoDBList, listComparator);
        adapter.notifyDataSetChanged();
    }

    private void sort() {
        if (todoDBList != null)
            Collections.sort(todoDBList, listComparator);
    }

    private Comparator<TodoItemDB> listComparator = new Comparator<TodoItemDB>() {
        @Override
        public int compare(TodoItemDB t1, TodoItemDB t2) {
            if(t1.done == t2.done){
                return 0;
            }
            return t1.done ? 1 : -1;
        }
    };

    /**
     * View holder inner class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbTodoDone;
        ImageButton btnDelete;

        /**
         * Constructor
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            cbTodoDone = itemView.findViewById(R.id.cbTodoDone);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            cbTodoDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    boolean checked = cbTodoDone.isChecked();
                    TodoItemDB item = todoDBList.get(getAdapterPosition());

                    //Firebase
                    int index = AddTodoList.addedItemsList.indexOf(item);
                    if (index != -1) {
                        AddTodoList.addedItemsList.get(index).done = checked;
                    }
                    else {
                        index = MainActivity.todoItemsList.indexOf(item);
                        MainActivity.todoItemsList.get(index).done = checked;

                        if (item.getItemId() != null) {
                            item.done = checked;
                            synchronized (MainActivity.FBLOCK) {
                                MainActivity.todoItemRef.child(item.getItemId()).setValue(item);
                            }
                        }
                    }

                    if (!onBind)
                        sortAndNotify();
                }
            });
        }
    }
}
