package peegee.fullorganizer.room_db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import peegee.fullorganizer.room_db.alarm.AlarmDAO;
import peegee.fullorganizer.room_db.alarm.AlarmDB;
import peegee.fullorganizer.room_db.notes.NotesDAO;
import peegee.fullorganizer.room_db.notes.NotesDB;
import peegee.fullorganizer.room_db.reminder.RemindersDAO;
import peegee.fullorganizer.room_db.reminder.RemindersDB;
import peegee.fullorganizer.room_db.todo.TodoDAO;
import peegee.fullorganizer.room_db.todo.TodoDB;
import peegee.fullorganizer.room_db.todo.TodoListDAO;
import peegee.fullorganizer.room_db.todo.TodoListDB;

@Database(entities = {TodoDB.class, NotesDB.class, TodoListDB.class, AlarmDB.class, RemindersDB.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TodoDAO todoDAO();
    public abstract TodoListDAO todoListDAO();
    public abstract NotesDAO notesDAO();
    public abstract AlarmDAO alarmDAO();
    public abstract RemindersDAO remindersDAO();
}
