package plat.plantcare.app.core.data.plant.considerations.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantConsiderationsDAO {
    @Query("SELECT * FROM plant_considerations WHERE plantId = :plantId")
    fun observePlantConsiderations(plantId: String): Flow<PlantConsiderationsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsertPlantConsiderations(plantConsiderations: PlantConsiderationsEntity)

    @Query("DELETE FROM plant_considerations WHERE plantId = :plantId")
    suspend fun deletePlantConsiderations(plantId: String)

    @Query("DELETE FROM plant_characteristics")
    suspend fun clear()
}