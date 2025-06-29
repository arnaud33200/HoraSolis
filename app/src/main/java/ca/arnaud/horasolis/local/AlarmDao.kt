package ca.arnaud.horasolis.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.arnaud.horasolis.domain.usecase.AlarmRinging
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAlarmRinging(entity: AlarmRingingEntity)

    @Query("DELETE FROM alarm_ringing")
    suspend fun clearAlarmRinging()

    @Query("SELECT * FROM alarm_ringing WHERE id = 1")
    fun observeRinging(): Flow<AlarmRinging?>
}
