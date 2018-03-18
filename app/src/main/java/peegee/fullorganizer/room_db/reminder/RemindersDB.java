package peegee.fullorganizer.room_db.reminder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class RemindersDB {

    public RemindersDB(String reminderTitle, String reminderLocation, String reminderDescription,
                       Date reminderDate, Date reminderTime,
                       boolean reminderAlarm, Date reminderAlarmDate) {
        this.reminderTitle = reminderTitle;
        this.reminderLocation = reminderLocation;
        this.reminderDescription = reminderDescription;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.reminderAlarm = reminderAlarm;
        this.reminderAlarmDate = reminderAlarmDate;
    }

    @PrimaryKey (autoGenerate = true)
    private int reminderId;

    @ColumnInfo(name = "reminder_title")
    private String reminderTitle;

    @ColumnInfo(name = "reminder_location")
    private String reminderLocation;

    @ColumnInfo(name = "reminder_description")
    private String reminderDescription;

    @ColumnInfo(name = "reminder_date")
    private Date reminderDate;

    @ColumnInfo(name = "reminder_time")
    private Date reminderTime;

    @ColumnInfo(name = "reminder_alarm")
    private boolean reminderAlarm;

    @ColumnInfo(name = "reminder_alarm_date")
    private Date reminderAlarmDate;

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

    public String getReminderLocation() {
        return reminderLocation;
    }

    public void setReminderLocation(String reminderLocation) {
        this.reminderLocation = reminderLocation;
    }

    public String getReminderDescription() {
        return reminderDescription;
    }

    public void setReminderDescription(String reminderDescription) {
        this.reminderDescription = reminderDescription;
    }

    public Date getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isReminderAlarm() {
        return reminderAlarm;
    }

    public void setReminderAlarm(boolean reminderAlarm) {
        this.reminderAlarm = reminderAlarm;
    }

    public Date getReminderAlarmDate() {
        return reminderAlarmDate;
    }

    public void setReminderAlarmDate(Date reminderAlarmDate) {
        this.reminderAlarmDate = reminderAlarmDate;
    }
}
