package peegee.fullorganizer.firebase_db;

import java.util.Date;

public class ReminderDB {

    public String reminderTitle;
    public String reminderLocation;
    public String reminderDescription;
    public Date reminderDate;
    public boolean reminderAlarm;
    public Date reminderAlarmDate;
    public int reminderAlarmValue;
    public String reminderAlarmType;

    // Required for FireBase
    public ReminderDB() {
    }

    public ReminderDB(String reminderTitle, String reminderLocation, String reminderDescription, Date reminderDate, boolean reminderAlarm, Date reminderAlarmDate, int reminderAlarmValue, String reminderAlarmType) {
        this.reminderTitle = reminderTitle;
        this.reminderLocation = reminderLocation;
        this.reminderDescription = reminderDescription;
        this.reminderDate = reminderDate;
        this.reminderAlarm = reminderAlarm;
        this.reminderAlarmDate = reminderAlarmDate;
        this.reminderAlarmValue = reminderAlarmValue;
        this.reminderAlarmType = reminderAlarmType;
    }
}
