package peegee.fullorganizer.service.local_db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * Room Database
 */
@Database(entities = {AlarmItemDB.class}, version = 10)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmItemDAO alarmItemDAO();
}
