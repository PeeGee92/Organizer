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
import butterknife.ButterKnife;
import butterknife.InjectView;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.service.adapters.TodoListAdapter;

/**
 * To-Do List Activity
 * The main activity for the to-do list function
 * Launched once the corresponding image button is pressed in Main Activity
 */
public class TodoActivity extends AppCompatActivity {

    @InjectView(R.id.rvTodo)
    RecyclerView rvTodo;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    RecyclerView.Adapter adapter;

    /**
     * onCreate method
     * <p>
     * @param savedInstanceState
     */
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

        // RecyclerView setup
        rvTodo.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoListAdapter(MainActivity.todoListList);
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