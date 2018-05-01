package peegee.fullorganizer.firebase_db;

import java.util.Date;

public class AlarmDB {

    private String uid;
    private String alarmId;
    private int alarmRequestCode;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public int getAlarmRequestCode() {
        return alarmRequestCode;
    }

    public void setAlarmRequestCode(int alarmRequestCode) {
        this.alarmRequestCode = alarmRequestCode;
    }
}
