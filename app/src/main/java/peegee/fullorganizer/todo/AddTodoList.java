package peegee.fullorganizer.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.TodoItemDB;
import peegee.fullorganizer.firebase_db.TodoListDB;
import peegee.fullorganizer.service.adapters.TodoAdapter;

public class AddTodoList extends AppCompatActivity {

    List<TodoItemDB> todoDBList = new ArrayList<>();
    List<TodoItemDB> addedItemsList = new ArrayList<>();
    
    @InjectView(R.id.rvTasks)
    RecyclerView rvTasks;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    RecyclerView.Adapter adapter;
    @InjectView(R.id.btnSaveList)
    Button btnSaveList;
    @InjectView(R.id.btnCancelList)
    Button btnCancelList;
    @InjectView(R.id.etListName)
    EditText etListName;

    private boolean update = false;

    private String listId;
    List<TodoListDB> evaluateResult; // Used to retrieve item by id
    TodoListDB list;

    TextInputEditText etAddTodo;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_list);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddTodoList.this, TodoActivity.class));
            }
        });

        // Get Intent extra to know which note to load
        // or to start a new note
        Intent intent = getIntent();
        listId = intent.getStringExtra("LIST_ID");

        if (listId != null) {
            Predicate condition = new Predicate() {
                public boolean evaluate(Object sample) {
                    return ((TodoListDB)sample).getTodoListId().equals(listId);
                }
            };
            evaluateResult = (List<TodoListDB>) CollectionUtils.select( MainActivity.todoListList, condition );
            list = evaluateResult.get(0);
            update = true;
            etListName.setText(list.todoListTitle);
        }

        if (update) { // Amend added items to existing list
            Predicate condition = new Predicate() {
                public boolean evaluate(Object sample) {
                    return ((TodoListDB) sample).getTodoListId().equals(listId);
                }
            };
            evaluateResult = (List<TodoListDB>) CollectionUtils.select(MainActivity.todoListList, condition);
            list = evaluateResult.get(0);
            if (list.todoItemList != null)
                todoDBList.addAll(list.todoItemList);
        }

        // RecyclerView setup
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        if (list == null)
            adapter = new TodoAdapter(new ArrayList<TodoItemDB>());
        else
            adapter = new TodoAdapter(list.todoItemList);
        rvTasks.setAdapter(adapter);

        FloatingActionButton fbAddTodoList = findViewById(R.id.fbAddTodoTask);
        fbAddTodoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoList.this);

                final Context context = builder.getContext();
                final LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.add_todo_dialog, null, false);

                builder.setView(dialogView);
                builder.setTitle("Add new task");
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        etAddTodo = dialogView.findViewById(R.id.etAddTodo);
                        String tempDesc = etAddTodo.getText().toString();
                        TodoItemDB todoDB = new TodoItemDB(false, tempDesc);
                        addedItemsList.add(todoDB);

                        todoDBList.add(todoDB);
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
                break;
            case R.id.btnCancelList:
                startActivity(new Intent(AddTodoList.this, TodoActivity.class));
                break;
        }
    }

    private void saveList() {
        if (etListName.getText().toString().trim().isEmpty()) {
            etListName.setHint("Please choose a title for you todo list");
            etListName.setHintTextColor(Color.RED);
            return;
        }

        // Firebase
        if (update) {
            list.todoListTitle = etListName.getText().toString();
            for (TodoItemDB item : addedItemsList) {
                item.setListId(listId);
            }
            if (addedItemsList != null) {
                if (list.todoItemList == null)
                    list.todoItemList = new ArrayList<>();
                list.todoItemList.addAll(addedItemsList);
            }
            synchronized (MainActivity.FBLOCK) {
                MainActivity.todoListRef.child(list.getTodoListId()).setValue(list);
            }
        }
        else {
            String tempTitle = etListName.getText().toString();
            list = new TodoListDB(tempTitle);
            synchronized (MainActivity.FBLOCK) {
                listId = MainActivity.todoListRef.push().getKey();
                for (TodoItemDB item : addedItemsList) {
                    item.setListId(listId);
                }
                list.todoItemList.addAll(addedItemsList);
                MainActivity.todoListRef.child(listId).setValue(list);
            }
        }

        startActivity(new Intent(AddTodoList.this, TodoActivity.class));
    }
}
