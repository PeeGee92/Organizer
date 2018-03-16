package peegee.fullorganizer.service;

import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoListDB;
import peegee.fullorganizer.todo.AddTodoList;

/**
 * TODO Query to get list with done, and list with not done
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    List<TodoListDB> todoListDBList;
    int done, notDone, progress;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    private final View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            TodoListDB item = todoListDBList.get(itemPosition);

            // Load this item data in the next activity
            int tempId = item.getTodoListId();

            Log.d("ON_CLICK_LIST", "id: " + tempId);

//            Intent intent = new Intent(view.getContext(), AddTodoList.class);
//            intent.putExtra("NOTE_ID", tempId);
//            view.getContext().startActivity(intent);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodoListDB temp = todoListDBList.get(position);

        Log.d("ON_BIND_LIST", "title: " + temp.getTodoListTitle());

        holder.tvTitle.setText(temp.getTodoListTitle());
        // TODO Get done and not done
        // TODO Set progress
    }

    @Override
    public int getItemCount() {
        return todoListDBList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDone, tvNotDone;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDone = itemView.findViewById(R.id.tvDone);
            tvNotDone = itemView.findViewById(R.id.tvNotDone);
            progressBar = itemView.findViewById(R.id.progress);

        }
    }
}
