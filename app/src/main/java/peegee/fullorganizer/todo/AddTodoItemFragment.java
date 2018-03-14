package peegee.fullorganizer.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.todo.TodoDB;

/**
 * Created by GhalysOnly on 11/03/2018.
 */

public class AddTodoItemFragment extends DialogFragment {

    Intent intent;
    TodoDB todoDB;

    public TextInputEditText etAddTodo;

    public static AddTodoItemFragment getInstance(String idTask) {
        Bundle bundle = new Bundle();
        bundle.putString("ID_TASK", idTask);
        AddTodoItemFragment fragment = new AddTodoItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        intent = new Intent(getContext(), AddTodoList.class);
        etAddTodo = new TextInputEditText(this.getContext());

        // TODO put extra list id

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.add_todo_fragment);
        builder.setTitle("Add new task");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etAddTodo = getDialog().findViewById(R.id.etAddTodo);
                String tempDesc = etAddTodo.getText().toString();
                // TODO Check boolean input for done
                todoDB = new TodoDB(tempDesc, false);

                //Database Synchronized
//                synchronized (MainActivity.db) {
//                    Log.d("AddTodoItem:", "Synchronized");
//                    MainActivity.db.todoDAO().insertAll(todoDB);
//                }

                //Database
                MainActivity.db.todoDAO().insertAll(todoDB);

                startActivity(intent);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(intent);
            }
        });

        return builder.create();
    }
}
