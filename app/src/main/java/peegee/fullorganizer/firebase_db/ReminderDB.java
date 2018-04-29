package peegee.fullorganizer.firebase_db;

import java.util.Date;

public class ReminderDB {

    private String uid;
    private String reminderId;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }
}
