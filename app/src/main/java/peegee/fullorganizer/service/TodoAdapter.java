package peegee.fullorganizer.service;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoDB;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    List<TodoDB> todoDBList;

    public TodoAdapter(List<TodoDB> todoDBList){
        this.todoDBList = todoDBList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvTodoDescription.setText(todoDBList.get(i).getTodoDescription());
        viewHolder.cbTodoDone.setChecked(todoDBList.get(i).isTodoDone());
    }

    @Override
    public int getItemCount() {
        Log.d("TODO_ITEMS", "Count: " + todoDBList.size());
        return todoDBList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbTodoDone;
        TextView tvTodoDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            cbTodoDone = itemView.findViewById(R.id.cbTodoDone);
            tvTodoDescription = itemView.findViewById(R.id.TodoDescription);
        }
    }
}
