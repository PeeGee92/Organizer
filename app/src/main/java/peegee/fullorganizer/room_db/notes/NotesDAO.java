package peegee.fullorganizer.room_db.notes;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by GhalysOnly on 09/03/2018.
 */

@Dao
public interface NotesDAO {

    @Query("SELECT * FROM NotesDB")
    List<NotesDB> getAll();

    @Query("SELECT * FROM NotesDB WHERE noteID = :id")
    NotesDB getById(int id);

    @Insert
    void insertAll(NotesDB... notesDBs);

    @Update
    void update(NotesDB noteDB);

    @Delete
    void delete(NotesDB noteDB);

}
