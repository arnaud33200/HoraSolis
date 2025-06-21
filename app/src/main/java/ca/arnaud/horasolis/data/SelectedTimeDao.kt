package ca.arnaud.horasolis.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SelectedTimeDao {

    @Insert
    suspend fun insert(time: SelectedTimeEntity)

    @Query("SELECT * FROM selected_times")
    suspend fun getAll(): List<SelectedTimeEntity>

    @Delete
    suspend fun delete(time: SelectedTimeEntity)

    @Query("DELETE FROM selected_times")
    suspend fun deleteAll()
}
