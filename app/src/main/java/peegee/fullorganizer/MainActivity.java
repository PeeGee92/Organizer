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

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.alarm.AlarmActivity;
import peegee.fullorganizer.notes.NotesActivity;
import peegee.fullorganizer.reminder.ReminderActivity;
import peegee.fullorganizer.room_db.AppDatabase;
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

    public static AppDatabase db;
    public static final Object DBLOCK = new Object(); // Database Lock
    public static final Object FBLOCK = new Object(); // Firebase Lock
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;
    public static DatabaseReference rootRef;
    public static DatabaseReference userReference;
    public static DatabaseReference dbReference;
    public boolean newUser;
    public static final String DB_PRIMARY_KEY = "user_id";

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
                        .setNegativeButton("Cancel", null).show();
            }
        });

        // Authentication
        if (firebaseUser == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            authenticate();
        }

        // Firebase Initialization
        initFirebase();

    }

    // FireBase
    private void initFirebase() {
        synchronized (FBLOCK) {
            // TODO Check if user exists
            rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot val : dataSnapshot.getChildren()){
                        if(val.child(DB_PRIMARY_KEY).getValue(String.class).contains(firebaseUser.getUid())) {
                            newUser = false;
                            // Database
                            synchronized (DBLOCK) {
                                db = val.child(DB_PRIMARY_KEY).child("database").getValue(AppDatabase.class);
                            }
                            break;
                        }
                        if(newUser != false){
                            newUser = true;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Do Nothing
                }
            });

            if(newUser) {
                // Database
                synchronized (DBLOCK) {
                    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "organizer_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
                userReference = rootRef.child(DB_PRIMARY_KEY);
                dbReference = userReference.child("database");

                rootRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        userReference.setValue(firebaseUser.getUid());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // Do Nothing
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        // Do Nothing
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        // Do Nothing
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Do Nothing
                    }
                });

                userReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        dbReference.setValue(db);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // Do Nothing
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        // Do Nothing
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        // Do Nothing
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Do Nothing
                    }
                });
            }
        }
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

        boolean newUser = false; // TODO

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                loadDBAccordingToUser();
            } else {
                Toast.makeText(getApplication(), "Sign in failed!", Toast.LENGTH_SHORT).show();
                authenticate();
            }
        }
    }

    private void loadDBAccordingToUser() {
        // Database
        synchronized (DBLOCK) {
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "organizer_database")
                    .allowMainThreadQueries()
                    .build();
//            // Firebase
//            synchronized (FBLOCK) {
//                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                DatabaseReference rootRef = firebaseDatabase.getReference();
//
//                rootRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // TODO
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        // TODO
//                    }
//                });
//
//                rootRef.setValue(db);
//
//            }
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

//    private static class MyFireBase {
//
//        // Entity references
//        static DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        static DatabaseReference notesReference = rootRef.child("Notes");
//        static DatabaseReference todoReference = rootRef.child("Todo");
//        static DatabaseReference alarmReference = rootRef.child("Alarm");
//        static DatabaseReference ReminderReference = rootRef.child("Reminder");
//
//        // TEST
//        static DatabaseReference dbReference = rootRef.child("Database");
//
//
//        // TODO add value event listener
//        public void test() {
//
//            dbReference.child("user_id").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    dbReference.child("user_id").setValue(firebaseUser.getUid());
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            })
//
//            dbReference.setValue(db);
//            dbReference.child("database_id").setValue(100);
//            Query query = dbReference.orderByChild("database_id")
//                    .equalTo(100);
//
//        }
//    }
}
