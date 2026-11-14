package plat.plantcare.app.core.data.plant.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDAO {
    @Query("SELECT * FROM plants")
    fun observePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE ownerId = :uid")
    fun observeUserPlants(uid: String): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :id")
    fun observePlant(id: String): Flow<PlantEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsertPlant(plant: PlantEntity)

    @Query("UPDATE plants SET favorite = NOT favorite WHERE id = :id")
    suspend fun togglePlantFavorite(id: String)

    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deletePlant(id: String)

    @Query("DELETE FROM plants")
    suspend fun clear()
}