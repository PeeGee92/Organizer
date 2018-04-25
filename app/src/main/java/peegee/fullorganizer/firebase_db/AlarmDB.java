package peegee.fullorganizer.firebase_db;

import java.util.Date;

public class AlarmDB {

    public String alarmId;
    public boolean alarmRepeated;
    public int alarmSnooze;
    public Date alarmDate;
    public boolean alarmOn;

    // Required for FireBase
    public AlarmDB() {
    }

    public AlarmDB(boolean alarmRepeated, int alarmSnooze, Date alarmDate, boolean alarmOn) {
        this.alarmRepeated = alarmRepeated;
        this.alarmSnooze = alarmSnooze;
        this.alarmDate = alarmDate;
        this.alarmOn = alarmOn;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }
}
