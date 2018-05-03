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
import android.widget.Toast;

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

    List<TodoItemDB> adapterItemsList = new ArrayList<>();
    public static List<TodoItemDB> addedItemsList = new ArrayList<>();
    
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
    List<TodoListDB> listResult; // Used to retrieve list by id
    List<TodoItemDB> itemsResult; // Used to retrieve items by id
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
            // Get list
            Predicate condition = new Predicate() {
                public boolean evaluate(Object sample) {
                    return ((TodoListDB)sample).getTodoListId().equals(listId);
                }
            };
            listResult = (List<TodoListDB>) CollectionUtils.select( MainActivity.todoListList, condition );
            list = listResult.get(0);

            // Get items
            condition = new Predicate() {
                public boolean evaluate(Object sample) {
                    return ((TodoItemDB)sample).getListId().equals(listId);
                }
            };
            itemsResult = (List<TodoItemDB>) CollectionUtils.select( MainActivity.todoItemsList, condition );

            update = true;
            etListName.setText(list.todoListTitle);
        }

        if (update) { // Amend existing data to adapter list
            if (itemsResult.size() > 0)
                adapterItemsList.addAll(itemsResult);
        }

        // RecyclerView setup
        addedItemsList.clear();
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        if (adapterItemsList == null)
            adapter = new TodoAdapter(new ArrayList<TodoItemDB>());
        else
            adapter = new TodoAdapter(adapterItemsList);
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
                        String tempDesc = etAddTodo.getText().toString().trim();

                        // Check input
                        if (tempDesc.isEmpty()) {
                            Toast.makeText(getApplication(), "Cannot add empty item!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TodoItemDB todoDB = new TodoItemDB(false, tempDesc);
                        addedItemsList.add(todoDB);

                        adapterItemsList.add(todoDB);
                        adapter = new TodoAdapter(adapterItemsList);
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

        // Input errors
        if (etListName.getText().toString().trim().isEmpty()) {
            etListName.setHint("Please choose a title for you todo list");
            etListName.setHintTextColor(Color.RED);
            return;
        }

        // Firebase
        if (update) {
            list.todoListTitle = etListName.getText().toString(); // update title

            synchronized (MainActivity.FBLOCK) {
                MainActivity.todoListRef.child(listId).setValue(list); // update list item
            }

            if (addedItemsList.size() > 0) {
                for (TodoItemDB item : addedItemsList) {
                    item.setListId(listId); // set new added items listId
                }
            }

            // Firebase
            if (addedItemsList.size() > 0) {
                for (TodoItemDB item : addedItemsList) {
                    String itemId = MainActivity.todoItemRef.push().getKey();
                    item.setItemId(itemId);
                    MainActivity.todoItemRef.child(itemId).setValue(item);
                }
            }
        }
        else {
            String tempTitle = etListName.getText().toString();
            list = new TodoListDB(tempTitle); // create new list with title
            list.setUid(MainActivity.getCurrentUid());

            // Firebase
            synchronized (MainActivity.FBLOCK) {
                listId = MainActivity.todoListRef.push().getKey();
                list.setTodoListId(listId);
                MainActivity.todoListRef.child(listId).setValue(list); // add new list to DB

                for (TodoItemDB item : addedItemsList) {
                    item.setListId(listId); // set new added items listId
                    MainActivity.todoItemsList.add(item);
                }

                // Firebase
                if (addedItemsList != null) {
                    for (TodoItemDB item : addedItemsList) {
                        String itemId = MainActivity.todoItemRef.push().getKey();
                        item.setItemId(itemId);
                        MainActivity.todoItemRef.child(itemId).setValue(item);
                    }
                }
            }
        }

        startActivity(new Intent(AddTodoList.this, TodoActivity.class));
    }
}
