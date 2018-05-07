package peegee.fullorganizer.service.local_db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateTypeConvertor {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }

}
