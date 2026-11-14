package plat.plantcare.app.core.data.plant.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import plat.plantcare.app.core.data.plant.domain.Plant
import java.time.LocalDate

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    val location: String,
    val birthDate: String?,
    val ownerId: String,
    val referenceId: String?,
    val favorite: Boolean
)

fun PlantEntity.toDomain(): Plant =
    Plant(id, name, imageUrl, location,
        birthDate = birthDate?.let { LocalDate.parse(it) },
        ownerId, referenceId, favorite)

fun Plant.toEntity(): PlantEntity =
    PlantEntity(id, name, imageUrl, location,
        birthDate = birthDate?.toString(),
        ownerId, referenceId, favorite)