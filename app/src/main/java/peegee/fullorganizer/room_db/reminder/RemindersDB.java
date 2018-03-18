package peegee.fullorganizer.room_db.reminder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class RemindersDB {

    public RemindersDB(String reminderTitle, String reminderLocation,
                       int reminderDay, int reminderMonth, int reminderYear,
                       int reminderHour, int reminderMin,
                       boolean reminderAlarm,
                       int reminderAlarmHour, int reminderAlarmMin,
                       int reminderAlarmDay, int reminderAlarmMonth, int reminderAlarmYear) {
        this.reminderTitle = reminderTitle;
        this.reminderLocation = reminderLocation;
        this.reminderHour = reminderHour;
        this.reminderMin = reminderMin;
        this.reminderDay = reminderDay;
        this.reminderMonth = reminderMonth;
        this.reminderYear = reminderYear;
        this.reminderAlarm = reminderAlarm;
        this.reminderAlarmHour = reminderAlarmHour;
        this.reminderAlarmMin = reminderAlarmMin;
        this.reminderAlarmDay = reminderAlarmDay;
        this.reminderAlarmMonth = reminderAlarmMonth;
        this.reminderAlarmYear = reminderAlarmYear;
    }

    @PrimaryKey (autoGenerate = true)
    private int reminderId;

    @ColumnInfo(name = "reminder_title")
    private String reminderTitle;

    @ColumnInfo(name = "reminder_location")
    private String reminderLocation;

    @ColumnInfo(name = "reminder_hour")
    private int reminderHour;

    @ColumnInfo(name = "reminder_min")
    private int reminderMin;

    @ColumnInfo(name = "reminder_day")
    private int reminderDay;

    @ColumnInfo(name = "reminder_month")
    private int reminderMonth;

    @ColumnInfo(name = "reminder_year")
    private int reminderYear;

    @ColumnInfo(name = "reminder_alarm")
    private boolean reminderAlarm;

    @ColumnInfo(name = "reminder_alarm_hour")
    private int reminderAlarmHour;

    @ColumnInfo(name = "reminder_alarm_min")
    private int reminderAlarmMin;

    @ColumnInfo(name = "reminder_alarm_day")
    private int reminderAlarmDay;

    @ColumnInfo(name = "reminder_alarm_month")
    private int reminderAlarmMonth;

    @ColumnInfo(name = "reminder_alarm_year")
    private int reminderAlarmYear;

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

    public int getReminderHour() {
        return reminderHour;
    }

    public void setReminderHour(int reminderHour) {
        this.reminderHour = reminderHour;
    }

    public int getReminderMin() {
        return reminderMin;
    }

    public void setReminderMin(int reminderMin) {
        this.reminderMin = reminderMin;
    }

    public int getReminderDay() {
        return reminderDay;
    }

    public void setReminderDay(int reminderDay) {
        this.reminderDay = reminderDay;
    }

    public int getReminderMonth() {
        return reminderMonth;
    }

    public void setReminderMonth(int reminderMonth) {
        this.reminderMonth = reminderMonth;
    }

    public int getReminderYear() {
        return reminderYear;
    }

    public void setReminderYear(int reminderYear) {
        this.reminderYear = reminderYear;
    }

    public boolean isReminderAlarm() {
        return reminderAlarm;
    }

    public void setReminderAlarm(boolean reminderAlarm) {
        this.reminderAlarm = reminderAlarm;
    }

    public int getReminderAlarmHour() {
        return reminderAlarmHour;
    }

    public void setReminderAlarmHour(int reminderAlarmHour) {
        this.reminderAlarmHour = reminderAlarmHour;
    }

    public int getReminderAlarmMin() {
        return reminderAlarmMin;
    }

    public void setReminderAlarmMin(int reminderAlarmMin) {
        this.reminderAlarmMin = reminderAlarmMin;
    }

    public int getReminderAlarmDay() {
        return reminderAlarmDay;
    }

    public void setReminderAlarmDay(int reminderAlarmDay) {
        this.reminderAlarmDay = reminderAlarmDay;
    }

    public int getReminderAlarmMonth() {
        return reminderAlarmMonth;
    }

    public void setReminderAlarmMonth(int reminderAlarmMonth) {
        this.reminderAlarmMonth = reminderAlarmMonth;
    }

    public int getReminderAlarmYear() {
        return reminderAlarmYear;
    }

    public void setReminderAlarmYear(int reminderAlarmYear) {
        this.reminderAlarmYear = reminderAlarmYear;
    }
}
