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

    private int listId;

    public static AddTodoItemFragment getInstance(String idTask) {
        Bundle bundle = new Bundle();
        bundle.putString("ID_TASK", idTask);
        AddTodoItemFragment fragment = new AddTodoItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    // TODO Use dismiss instead of loading intent
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        intent = new Intent(getContext(), AddTodoList.class);
        etAddTodo = new TextInputEditText(this.getContext());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            listId = bundle.getInt("LIST_ID", -1);
            Log.d("FRAGMENT", "listId: " + listId);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.add_todo_fragment);
        builder.setTitle("Add new task");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etAddTodo = getDialog().findViewById(R.id.etAddTodo);
                String tempDesc = etAddTodo.getText().toString();
                // TODO Check boolean input for done
                todoDB = new TodoDB(tempDesc, false, listId);

                //Database
                MainActivity.db.todoDAO().insertAll(todoDB);

                startActivity(new Intent(getContext(), AddTodoList.class));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getContext(), AddTodoList.class));
            }
        });

        return builder.create();
    }
}
