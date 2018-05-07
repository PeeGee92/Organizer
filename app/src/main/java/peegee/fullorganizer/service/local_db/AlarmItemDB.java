package peegee.fullorganizer.service.local_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity (tableName = "alarm_items")
public class AlarmItemDB {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo (name = "request_code")
    private int requestCode;

    @ColumnInfo (name = "alarm_time")
    private Date alarmTime;

    @ColumnInfo (name = "reninder")
    private boolean reminder;

    @NonNull
    public String getId() {
        return id;
    }

    public AlarmItemDB(@NonNull String id, int requestCode, Date alarmTime, boolean reminder) {
        this.id = id;
        this.requestCode = requestCode;
        this.alarmTime = alarmTime;
        this.reminder = reminder;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }
}
