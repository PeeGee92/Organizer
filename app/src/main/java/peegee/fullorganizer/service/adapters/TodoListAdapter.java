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
import java.util.List;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.TodoItemDB;
import peegee.fullorganizer.firebase_db.TodoListDB;
import peegee.fullorganizer.todo.AddTodoList;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    List<TodoListDB> todoListDBList;
    int done, notDone, progress;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    String id;

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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView =recyclerView;
    }

    public TodoListAdapter(List<TodoListDB> todoListDBList) {
        this.todoListDBList = todoListDBList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
        view.setOnClickListener(myOnClickListener);
        adapter = this;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TodoListDB temp = todoListDBList.get(position);

        holder.tvTitle.setText(temp.todoListTitle);
        done = getByDoneCount(position, true);
        notDone = getByDoneCount(position, false);
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
                                TodoListDB item = todoListDBList.get(position);

                                // Firebase
                                synchronized (MainActivity.FBLOCK) {
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

    private int getByDoneCount(int itemPosition, boolean done) {
        int doneItems = 0;
        TodoListDB listItem = todoListDBList.get(itemPosition);
        List<TodoItemDB> itemsList= listItem.todoItemList;

        for (TodoItemDB item: itemsList) {
            if (item.done)
                doneItems++;
        }

        return doneItems;
    }

    @Override
    public int getItemCount() {
        return todoListDBList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDone, tvNotDone;
        ProgressBar progressBar;
        ImageButton btnDelete;

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
