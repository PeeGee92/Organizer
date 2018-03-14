package peegee.fullorganizer.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoDB;
import peegee.fullorganizer.service.TodoAdapter;

public class AddTodoList extends AppCompatActivity {

    List<TodoDB> todoDBList = new ArrayList<>();

    @InjectView(R.id.rvTasks)
    RecyclerView rvTasks;

    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_list);
        ButterKnife.inject(this);

        //Database Synchronized
//        synchronized (MainActivity.db) {
//            synchronized (todoDBList) {
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... voids) {
//                        Log.d("Database:", "LoadFromDB");
//                        todoDBList = MainActivity.db.todoDAO().getAll();
//                        return null;
//                    }
//                }.execute();
//            }
//        }

        //Database
        todoDBList = MainActivity.db.todoDAO().getAll();

        // RecyclerView setup
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(todoDBList);
        rvTasks.setAdapter(adapter);

        FloatingActionButton fbAddTodoList = findViewById(R.id.fbAddTodoTask);
        fbAddTodoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTodoItemFragment addTodoItemFragment = new AddTodoItemFragment();
                addTodoItemFragment.show(getFragmentManager(), "ADD_TASK");
            }
        });

    }
}
