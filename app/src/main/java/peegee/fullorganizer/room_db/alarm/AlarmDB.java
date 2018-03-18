package peegee.fullorganizer.room_db.alarm;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class AlarmDB {

    public AlarmDB(int alarmHour, int alarmMin, int alarmSnooze, boolean alarmRepeated, boolean alarmOn) {
        this.alarmRepeated = alarmRepeated;
        this.alarmSnooze = alarmSnooze;
        this.alarmHour = alarmHour;
        this.alarmMin = alarmMin;
        this.alarmOn = alarmOn;
    }

    public AlarmDB(int alarmDay, int alarmMonth, int alarmYear,
                   int alarmHour, int alarmMin,
                   int alarmSnooze,
                   boolean alarmRepeated,
                   boolean alarmOn) {
        this.alarmRepeated = alarmRepeated;
        this.alarmSnooze = alarmSnooze;
        this.alarmDay = alarmDay;
        this.alarmMonth = alarmMonth;
        this.alarmYear = alarmYear;
        this.alarmHour = alarmHour;
        this.alarmMin = alarmMin;
        this.alarmOn = alarmOn;
    }

    @PrimaryKey(autoGenerate = true)
    private int alarmId;

    @ColumnInfo(name = "alarm_repeated")
    private boolean alarmRepeated;

    @ColumnInfo(name = "alarm_snooze")
    private int alarmSnooze;

    @ColumnInfo(name = "alarm_day")
    private int alarmDay;

    @ColumnInfo(name = "alarm_month")
    private int alarmMonth;

    @ColumnInfo(name = "alarm_year")
    private int alarmYear;

    @ColumnInfo(name = "alarm_hour")
    private int alarmHour;

    @ColumnInfo(name = "alarm_min")
    private int alarmMin;

    @ColumnInfo(name = "alarm_on")
    private boolean alarmOn;

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public boolean isAlarmRepeated() {
        return alarmRepeated;
    }

    public void setAlarmRepeated(boolean alarmRepeated) {
        this.alarmRepeated = alarmRepeated;
    }

    public int getAlarmSnooze() {
        return alarmSnooze;
    }

    public void setAlarmSnooze(int alarmSnooze) {
        this.alarmSnooze = alarmSnooze;
    }

    public int getAlarmHour() {
        return alarmHour;
    }

    public void setAlarmHour(int alarmHour) {
        this.alarmHour = alarmHour;
    }

    public int getAlarmMin() {
        return alarmMin;
    }

    public void setAlarmMin(int alarmMin) {
        this.alarmMin = alarmMin;
    }

    public boolean isAlarmOn() {
        return alarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    public int getAlarmDay() {
        return alarmDay;
    }

    public void setAlarmDay(int alarmDay) {
        this.alarmDay = alarmDay;
    }

    public int getAlarmMonth() {
        return alarmMonth;
    }

    public void setAlarmMonth(int alarmMonth) {
        this.alarmMonth = alarmMonth;
    }

    public int getAlarmYear() {
        return alarmYear;
    }

    public void setAlarmYear(int alarmYear) {
        this.alarmYear = alarmYear;
    }
}
