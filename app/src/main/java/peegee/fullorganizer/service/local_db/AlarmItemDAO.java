package peegee.fullorganizer.service.local_db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AlarmItemDAO {

    @Query("SELECT * FROM alarm_items")
    List<AlarmItemDB> getAll();

    @Query("SELECT * FROM alarm_items WHERE id = :alarmId")
    AlarmItemDB getAlarmById(String alarmId);

    @Insert
    void insert (AlarmItemDB alarmItemDB);

    @Update
    void update (AlarmItemDB alarmItemDB);

    @Delete
    void delete (AlarmItemDB alarmItemDB);
}
