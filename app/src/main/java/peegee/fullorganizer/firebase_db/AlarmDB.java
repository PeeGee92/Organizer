package peegee.fullorganizer.firebase_db;

import java.util.Date;

public class AlarmDB {

    public int alarmId;
    public boolean alarmRepeated;
    public int alarmSnooze;
    public Date alarmDate;
    public boolean alarmOn;

    // Required for FireBase
    public AlarmDB() {
    }

    public AlarmDB(int alarmId, boolean alarmRepeated, int alarmSnooze, Date alarmDate, boolean alarmOn) {
        this.alarmId = alarmId;
        this.alarmRepeated = alarmRepeated;
        this.alarmSnooze = alarmSnooze;
        this.alarmDate = alarmDate;
        this.alarmOn = alarmOn;
    }
}
