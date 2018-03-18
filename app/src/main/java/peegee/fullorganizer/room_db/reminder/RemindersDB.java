package peegee.fullorganizer.room_db.reminder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class RemindersDB {

    public RemindersDB(String reminderTitle, String reminderLocation, String reminderDescription,
                       Date reminderDate,
                       boolean reminderAlarm, Date reminderAlarmDate,
                       int reminderAlarmValue, String reminderAlarmType) {
        this.reminderTitle = reminderTitle;
        this.reminderLocation = reminderLocation;
        this.reminderDescription = reminderDescription;
        this.reminderDate = reminderDate;
        this.reminderAlarm = reminderAlarm;
        this.reminderAlarmDate = reminderAlarmDate;
        this.reminderAlarmValue = reminderAlarmValue;
        this.reminderAlarmType = reminderAlarmType;
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

    @ColumnInfo(name = "reminder_alarm")
    private boolean reminderAlarm;

    @ColumnInfo(name = "reminder_alarm_date")
    private Date reminderAlarmDate;

    @ColumnInfo(name = "reminder_alarm_value")
    private int reminderAlarmValue;

    @ColumnInfo(name = "reminder_alarm_type")
    private String reminderAlarmType;

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

    public int getReminderAlarmValue() {
        return reminderAlarmValue;
    }

    public void setReminderAlarmValue(int reminderAlarmValue) {
        this.reminderAlarmValue = reminderAlarmValue;
    }

    public String getReminderAlarmType() {
        return reminderAlarmType;
    }

    public void setReminderAlarmType(String reminderAlarmType) {
        this.reminderAlarmType = reminderAlarmType;
    }
}
