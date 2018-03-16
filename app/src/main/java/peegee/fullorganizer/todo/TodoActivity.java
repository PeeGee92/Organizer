package peegee.fullorganizer.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        ButterKnife.inject(this);

        // Database
        todoListDBList = MainActivity.db.todoListDAO().getAll();

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
