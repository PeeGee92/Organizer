package peegee.fullorganizer.service.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import java.util.List;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.TodoItemDB;
import peegee.fullorganizer.firebase_db.TodoListDB;
import peegee.fullorganizer.todo.AddTodoList;

/**
 * Recycler view adapter class for to-do lists
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    List<TodoListDB> todoListDBList;
    int done, notDone;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    /**
     * OnClick listener method for recycler view
     */
    private final View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            TodoListDB item = todoListDBList.get(itemPosition);

            // Load this item data in the next activity
            String tempId = item.getTodoListId();

            Intent intent = new Intent(view.getContext(), AddTodoList.class);
            intent.putExtra("LIST_ID", tempId);
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
     * @param todoListDBList the to-do lists list
     */
    public TodoListAdapter(List<TodoListDB> todoListDBList) {
        this.todoListDBList = todoListDBList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TodoListDB temp = todoListDBList.get(position);

        holder.tvTitle.setText(temp.todoListTitle);
        done = getByDoneItemsCount(position, true);
        notDone = getByDoneItemsCount(position, false);
        holder.tvDone.setText("Tasks done: " + done);
        holder.tvNotDone.setText("Tasks not done: " + notDone);
        float totalTasks = done + notDone;
        setProgress(holder, totalTasks);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete Todo List")
                        .setMessage("Are you sure you want to delete this todo list permanently?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final TodoListDB item = todoListDBList.get(position);
                                List<TodoItemDB> itemsList;

                                // Firebase
                                synchronized (MainActivity.FBLOCK) {
                                    Predicate condition = new Predicate() {
                                        public boolean evaluate(Object sample) {
                                            return ((TodoItemDB)sample).getListId().equals(item.getTodoListId());
                                        }
                                    };
                                    itemsList = (List<TodoItemDB>) CollectionUtils.select( MainActivity.todoItemsList, condition );

                                    for (TodoItemDB itemDB: itemsList) {
                                        MainActivity.todoItemRef.child(itemDB.getItemId()).removeValue();
                                    }

                                    MainActivity.todoListRef.child(item.getTodoListId()).removeValue();
                                }

                                // Update RecyclerView
                                todoListDBList.remove(position);
                                recyclerView.removeViewAt(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, todoListDBList.size());
                                adapter.notifyDataSetChanged();
                            }})
                        .setNegativeButton("Cancel", null).show();
            }
        });
    }

    /**
     * Set the progress of the to-do list
     * <p>
     * @param viewHolder
     * @param totalTasks the total number of tasks in the list
     */
    private void setProgress(ViewHolder viewHolder, float totalTasks) {
        float progress;

        if (totalTasks == 0) // List is empty
        {
            progress = 100;
        }
        else // List is not empty
        {
            if (notDone == 0) // All tasks are done
            {
                progress = 100;
            }
            else // Not all tasks are done
            {
                progress = ( done / totalTasks) * 100;
            }
        }

        viewHolder.progressBar.setProgress((int) progress);
    }

    /**
     * Get the number of the done / not done items in this list
     * @param itemPosition the item position in the adapter
     * @param done boolean according to which the done or the not done number is returned
     * @return the number of done / not done items in the list according to <code>done</code> value
     */
    private int getByDoneItemsCount(int itemPosition, boolean done) {
        int items = 0;
        final TodoListDB listItem = todoListDBList.get(itemPosition);

        // Get items
        Predicate condition = new Predicate() {
            public boolean evaluate(Object sample) {
                return ((TodoItemDB)sample).getListId().equals(listItem.getTodoListId());
            }
        };
        List<TodoItemDB> itemsList = (List<TodoItemDB>) CollectionUtils.select( MainActivity.todoItemsList, condition );

        if (itemsList != null) {
            for (TodoItemDB item : itemsList) {
                if (item.done == done)
                    items++;
            }
        }

        return items;
    }

    /**
     * getItemCount method
     * <p>
     * @return the adapter list size
     */
    @Override
    public int getItemCount() {
        return todoListDBList.size();
    }

    /**
     * View holder inner class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDone, tvNotDone;
        ProgressBar progressBar;
        ImageButton btnDelete;

        /**
         * Constructor
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDone = itemView.findViewById(R.id.tvDone);
            tvNotDone = itemView.findViewById(R.id.tvNotDone);
            progressBar = itemView.findViewById(R.id.progress);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
}
