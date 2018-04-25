/**
 * Final Project
 * Owner: Pierre Ghaly
 * Professor: Constandinos Mavromoustakis
 * Student ID: U153N0003
 * Description: A full organizer which includes: alarm, notes, to do list and reminders
 */

package peegee.fullorganizer;

import android.app.job.JobScheduler;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.alarm.AlarmActivity;
import peegee.fullorganizer.firebase_db.AlarmDB;
import peegee.fullorganizer.firebase_db.NotesDB;
import peegee.fullorganizer.firebase_db.ReminderDB;
import peegee.fullorganizer.firebase_db.TodoItemDB;
import peegee.fullorganizer.firebase_db.TodoListDB;
import peegee.fullorganizer.notes.NotesActivity;
import peegee.fullorganizer.reminder.ReminderActivity;
import peegee.fullorganizer.todo.TodoActivity;

/**
 * Main Activity
 * Loads the main UI
 */
public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @InjectView(R.id.btnAlarm)
    ImageButton btnAlarm;
    @InjectView(R.id.btnNotes)
    ImageButton btnNotes;
    @InjectView(R.id.btnToDo)
    ImageButton btnToDo;
    @InjectView(R.id.btnReminders)
    ImageButton btnReminders;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    public static final Object DBLOCK = new Object(); // Database Lock
    public static final Object FBLOCK = new Object(); // Firebase Lock
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;
    public static DatabaseReference rootRef;
    public static DatabaseReference userRef;
    public static DatabaseReference alarmRef;
    public static DatabaseReference notesRef;
    public static DatabaseReference reminderRef;
    public static DatabaseReference todoListRef;
    public static DatabaseReference todoItemRef;

    // DB locally saved lists
    public static List<NotesDB> notesList = new ArrayList<>();
    public static List<AlarmDB> alarmsList = new ArrayList<>();
    public static List<TodoListDB> todoListList = new ArrayList<>();
    public static List<TodoItemDB> todoItemsList = new ArrayList<>();
    public static List<ReminderDB> reminderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.rotate_logout);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SIGNOUT", "onClick: IN!");
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out?")
                        .setIcon(R.drawable.rotate_logout)
                        .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                AuthUI.getInstance()
                                        .signOut(getApplicationContext())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                authenticate();
                                            }
                                        });
                            }})
                        .setNeutralButton("Delete User", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseUser.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    authenticate();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });

        // Authentication
        if (firebaseUser == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            authenticate();
        }

        // Firebase Initialization
        if (rootRef == null) {
            initFirebase();
        }

    }

    private void initFirebase() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("User");
        alarmRef = userRef.child("Alarm");
        alarmRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AlarmDB tempItem = dataSnapshot.getValue(AlarmDB.class);
                tempItem.setAlarmId(dataSnapshot.getKey());
                alarmsList.add(tempItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        notesRef = userRef.child("Notes");
        notesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NotesDB tempItem = dataSnapshot.getValue(NotesDB.class);
                tempItem.setNoteId(dataSnapshot.getKey());
                notesList.add(tempItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        todoListRef = userRef.child("Todo List");
        todoListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TodoListDB tempList = dataSnapshot.getValue(TodoListDB.class);
                tempList.setTodoListId(dataSnapshot.getKey());
                if (!tempList.getTodoListId().equals("Todo Items"))
                    todoListList.add(tempList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        todoItemRef = todoListRef.child("Todo Items");
        todoItemRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TodoItemDB tempItem = dataSnapshot.getValue(TodoItemDB.class);
                tempItem.setItemId(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reminderRef = userRef.child("Reminder");
        reminderRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ReminderDB tempItem = dataSnapshot.getValue(ReminderDB.class);
                tempItem.setReminderId(dataSnapshot.getKey());
                reminderList.add(tempItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void authenticate() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(true)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                // TODO
//                loadDBAccordingToUser();
            } else {
                Toast.makeText(getApplication(), "Sign in failed!", Toast.LENGTH_SHORT).show();
                authenticate();
            }
        }
    }

    /**
     * onClick method for the buttons
     * @param view the UI view
     */
    @OnClick({R.id.btnAlarm, R.id.btnNotes, R.id.btnToDo, R.id.btnReminders})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAlarm:
                startActivity(new Intent(this, AlarmActivity.class));
                break;
            case R.id.btnNotes:
                startActivity(new Intent(this, NotesActivity.class));
                break;
            case R.id.btnToDo:
                startActivity(new Intent(this, TodoActivity.class));
                break;
            case R.id.btnReminders:
                startActivity(new Intent(this, ReminderActivity.class));
                break;
        }
    }

}
