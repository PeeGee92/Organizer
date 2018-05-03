/**
 * Full Organizer is an application wich contains four main functions:
 * Alarm, notes, to-do list and reminders
 * <p>
 * Professor: Constandinos Mavromoustakis
 * Student ID: U153N0003
 *
 * @author Pierre Ghaly
 * @version 1.0
 */

package peegee.fullorganizer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.alarm.AddAlarm;
import peegee.fullorganizer.alarm.AlarmActivity;
import peegee.fullorganizer.alarm.AlarmReceiver;
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

    public static final Object FBLOCK = new Object(); // Firebase Lock
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;
    public static DatabaseReference rootRef;
    public static DatabaseReference userRef;
    public static DatabaseReference userDataRef;
    public static DatabaseReference alarmRef;
    public static DatabaseReference notesRef;
    public static DatabaseReference reminderRef;
    public static DatabaseReference todoListRef;
    public static DatabaseReference todoItemRef;
    public static DatabaseReference requestCodeRef;

    // DB locally saved lists
    public static List<NotesDB> notesList = new ArrayList<>();
    public static List<AlarmDB> alarmsList = new ArrayList<>();
    public static List<TodoListDB> todoListList = new ArrayList<>();
    public static List<TodoItemDB> todoItemsList = new ArrayList<>();
    public static List<ReminderDB> reminderList = new ArrayList<>();

    // Alarm ID
    private static int requestCode; // Used as a unique auto incremented request code for intents

    // Shared Preferences
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

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

        // SharedPreferences
        sharedPreferences = getSharedPreferences("REQUEST_CODE", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        requestCode = sharedPreferences.getInt("REQUEST_CODE", 0);
        if (requestCode > (Integer.MAX_VALUE - 1000)) {
            requestCode = 0;
        }

    }

    private void initFirebase() {
            clearLists();
            rootRef = FirebaseDatabase.getInstance().getReference();
            userRef = rootRef.child(getString(R.string.db_user));
            userDataRef = userRef.child(firebaseUser.getUid());
            alarmRef = userDataRef.child(getString(R.string.db_alarm));
            alarmRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    AlarmDB tempItem = dataSnapshot.getValue(AlarmDB.class);
                    alarmsList.add(tempItem);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    final AlarmDB tempItem = dataSnapshot.getValue(AlarmDB.class);

                    Predicate condition = new Predicate() {
                        public boolean evaluate(Object sample) {
                            return ((AlarmDB)sample).getAlarmId().equals(tempItem.getAlarmId());
                        }
                    };
                    List<AlarmDB> evaluateResult = (List<AlarmDB>) CollectionUtils.select( alarmsList, condition );
                    AlarmDB oldItem = evaluateResult.get(0);

                    alarmsList.remove(oldItem);
                    alarmsList.add(tempItem);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    final AlarmDB tempItem = dataSnapshot.getValue(AlarmDB.class);

                    AddAlarm.cancelAlarm(tempItem);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            notesRef = userDataRef.child(getString(R.string.db_notes));
            notesRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    NotesDB tempItem = dataSnapshot.getValue(NotesDB.class);
                    notesList.add(tempItem);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    final NotesDB tempItem = dataSnapshot.getValue(NotesDB.class);

                    Predicate condition = new Predicate() {
                        public boolean evaluate(Object sample) {
                            return ((NotesDB)sample).getNoteId().equals(tempItem.getNoteId());
                        }
                    };
                    List<NotesDB> evaluateResult = (List<NotesDB>) CollectionUtils.select( notesList, condition );
                    NotesDB result = evaluateResult.get(0);

                    notesList.remove(result);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            todoListRef = userDataRef.child(getString(R.string.db_todo_list));
            todoListRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    TodoListDB tempList = dataSnapshot.getValue(TodoListDB.class);
                    if (tempList.getTodoListId() != null)
                        todoListList.add(tempList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    final TodoListDB tempList = dataSnapshot.getValue(TodoListDB.class);

                    Predicate condition = new Predicate() {
                        public boolean evaluate(Object sample) {
                            return ((TodoListDB) sample).getTodoListId().equals(tempList.getTodoListId());
                        }
                    };
                    List<TodoListDB> evaluateResult = (List<TodoListDB>) CollectionUtils.select(todoListList, condition);

                    if (evaluateResult.size() > 0) {
                        TodoListDB result = evaluateResult.get(0);
                    todoListList.remove(result);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            todoItemRef = todoListRef.child(getString(R.string.db_todo_items));
            todoItemRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    TodoItemDB tempItem = dataSnapshot.getValue(TodoItemDB.class);
                    todoItemsList.add(tempItem);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    TodoItemDB tempItem = dataSnapshot.getValue(TodoItemDB.class);
//                    int index = todoItemsList.indexOf(tempItem);
//                    todoItemsList.set(index, tempItem);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    final TodoItemDB tempItem = dataSnapshot.getValue(TodoItemDB.class);

                    Predicate condition = new Predicate() {
                        public boolean evaluate(Object sample) {
                            return ((TodoItemDB)sample).getItemId().equals(tempItem.getItemId());
                        }
                    };
                    List<TodoItemDB> evaluateResult = (List<TodoItemDB>) CollectionUtils.select( todoItemsList, condition );
                    if (evaluateResult.size() > 0) {
                        TodoItemDB result = evaluateResult.get(0);
                        todoItemsList.remove(result);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            reminderRef = userDataRef.child(getString(R.string.db_reminder));
            reminderRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ReminderDB tempItem = dataSnapshot.getValue(ReminderDB.class);
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

            Toast.makeText(getApplication(), "Application linked to server", Toast.LENGTH_SHORT).show();
    }

    private void clearLists() {
        notesList.clear();
        alarmsList.clear();
        reminderList.clear();
        todoListList.clear();
        todoItemsList.clear();
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

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                // Firebase Initialization
                initFirebase();
            } else {
                Toast.makeText(getApplication(), "Sign in failed!", Toast.LENGTH_SHORT).show();
                authenticate();
            }
        }
    }

    public static int getRequestCode() {
        int temp = requestCode++;
        editor.putInt("REQUEST_CODE", requestCode).commit();
        return temp;
    }

    public static String getCurrentUid() {
        return firebaseUser.getUid();
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
