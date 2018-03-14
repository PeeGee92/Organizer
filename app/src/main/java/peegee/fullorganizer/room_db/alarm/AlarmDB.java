package peegee.fullorganizer.room_db.alarm;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.widget.TimePicker;

@Entity
public class AlarmDB {

    public AlarmDB(int alarmHour, int alarmMin, int alarmSnooze, boolean alarmRepeated, boolean alarmOn) {
        this.alarmRepeated = alarmRepeated;
        this.alarmSnooze = alarmSnooze;
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
}
