package peegee.fullorganizer.service.local_db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {AlarmItemDB.class}, version = 1)
@TypeConverters({DateTypeConvertor.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmItemDAO alarmItemDAO();
}
