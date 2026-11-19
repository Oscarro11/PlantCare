package plat.plantcare.app.core.data.plant.characteristics.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantCharacteristicsDAO {
    @Query("SELECT * FROM plant_characteristics WHERE plantId = :plantId")
    fun observePlantCharacteristics(plantId: String): Flow<PlantCharacteristicsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsertPlantCharacteristics(plantCharacteristics: PlantCharacteristicsEntity)

    @Query("DELETE FROM plant_characteristics WHERE plantId = :plantId")
    suspend fun deletePlantCharacteristics(plantId: String)

    @Query("DELETE FROM plant_characteristics")
    suspend fun clear()
}