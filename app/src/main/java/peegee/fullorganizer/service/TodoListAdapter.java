package peegee.fullorganizer.service;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoListDB;
import peegee.fullorganizer.room_db.todo.TodoListWithItems;

/**
 * TODO Query to get list with done, and list with not done
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    List<TodoListWithItems> todoListDBList;
    int done, notDone, progress;

    public TodoListAdapter(List<TodoListWithItems> todoListDBList) {
        this.todoListDBList = todoListDBList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO
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
