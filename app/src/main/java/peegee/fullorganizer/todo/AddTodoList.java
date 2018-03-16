package peegee.fullorganizer.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoDB;
import peegee.fullorganizer.room_db.todo.TodoListDB;
import peegee.fullorganizer.service.TodoAdapter;

public class AddTodoList extends AppCompatActivity {

    List<TodoDB> todoDBList = new ArrayList<>();

    @InjectView(R.id.rvTasks)
    RecyclerView rvTasks;

    RecyclerView.Adapter adapter;
    @InjectView(R.id.btnSaveList)
    Button btnSaveList;
    @InjectView(R.id.btnCancelList)
    Button btnCancelList;
    @InjectView(R.id.etListName)
    EditText etListName;

    private boolean update = false;

    private int listId;
    TodoListDB list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_list);
        ButterKnife.inject(this);

        // Get Intent extra to know which note to load
        // or to start a new note
        Intent intent = getIntent();
        listId = intent.getIntExtra("LIST_ID", -1);

        if (listId != -1) {
            // Database
            list = MainActivity.db.todoListDAO().getListById(listId);
            todoDBList = MainActivity.db.todoDAO().loadByListId(listId);
            update = true;
        } else {
            list = new TodoListDB("");
            // Database
            listId = (int) MainActivity.db.todoListDAO().insert(list);
        }

        // RecyclerView setup
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(todoDBList);
        rvTasks.setAdapter(adapter);

        FloatingActionButton fbAddTodoList = findViewById(R.id.fbAddTodoTask);
        fbAddTodoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTodoItemFragment addTodoItemFragment = new AddTodoItemFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("LIST_ID", listId);
                addTodoItemFragment.setArguments(bundle);
                addTodoItemFragment.show(getFragmentManager(), "ADD_TASK");

                // TODO Refresh list without reloading activity
            }
        });

    }

    @OnClick({R.id.btnSaveList, R.id.btnCancelList})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSaveList:
                saveList();
                startActivity(new Intent(AddTodoList.this, TodoActivity.class));
                break;
            case R.id.btnCancelList:
                canceList();
                startActivity(new Intent(AddTodoList.this, TodoActivity.class));
                break;
        }
    }

    private void canceList() {
        // TODO If not update ONLY
        MainActivity.db.todoListDAO().delete(list);
    }

    private void saveList() {
        list = MainActivity.db.todoListDAO().getListById(listId);
        // TODO Implement if list doesn't have a title
        list.setTodoListTitle(etListName.getText().toString());
        // TODO Set items done and not done
        MainActivity.db.todoListDAO().update(list);
    }
}
