package peegee.fullorganizer.todo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoListDB;
import peegee.fullorganizer.service.TodoListAdapter;

public class TodoActivity extends AppCompatActivity {

    List<TodoListDB> todoListDBList = new ArrayList<>();

    @InjectView(R.id.rvTodo)
    RecyclerView rvTodo;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TodoActivity.this, MainActivity.class));
            }
        });

        // Database
        synchronized (MainActivity.DBLOCK) {
            todoListDBList = MainActivity.db.todoListDAO().getAll();
        }

        // RecyclerView setup
        rvTodo.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoListAdapter(todoListDBList);
        rvTodo.setAdapter(adapter);

        FloatingActionButton fbAddTodoList = findViewById(R.id.fbAddTodoList);
        fbAddTodoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodoActivity.this, AddTodoList.class);
                startActivity(intent);
            }
        });
    }
}
