package peegee.fullorganizer.room_db.reminder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class RemindersDB {

    @PrimaryKey (autoGenerate = true)
    private int reminderId;

    @ColumnInfo(name = "reminder_title")
    private String reminderTitle;

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public String getReminderTitle() {
        return reminderTitle;
    }

    public void setReminderTitle(String reminderTitle) {
        this.reminderTitle = reminderTitle;
    }
}
