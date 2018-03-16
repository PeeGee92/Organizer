package peegee.fullorganizer.todo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

    TextInputEditText etAddTodo;
    AlertDialog dialog;

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
            Log.d("TODO_LIST_CREATED", "listId: " + listId);
        }

        // RecyclerView setup
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(todoDBList);
        rvTasks.setAdapter(adapter);

        FloatingActionButton fbAddTodoList = findViewById(R.id.fbAddTodoTask);
        fbAddTodoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoList.this);

                // Inflate using dialog themed context.
                final Context context = builder.getContext();
                final LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.add_todo_fragment, null, false);

                builder.setView(dialogView);
                builder.setTitle("Add new task");
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        etAddTodo = dialogView.findViewById(R.id.etAddTodo);
                        String tempDesc = etAddTodo.getText().toString();
                        // TODO Check boolean input for done
                        TodoDB todoDB = new TodoDB(tempDesc, false, listId);

                        //Database
                        MainActivity.db.todoDAO().insertAll(todoDB);

                        todoDBList = MainActivity.db.todoDAO().loadByListId(listId);
                        adapter = new TodoAdapter(todoDBList);
                        rvTasks.setAdapter(adapter);

                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });

                dialog = builder.create();
                dialog.show();
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
