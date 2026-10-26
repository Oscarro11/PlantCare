package plat.plantcare.app.collection.allPlants.data
import androidx.annotation.DrawableRes
import plat.plantcare.app.R
import plat.plantcare.app.core.data.models.Task

enum class CollectionLayout(
    @DrawableRes val icon: Int
) {
    GRID(
        icon = R.drawable.grid_view
    ),
    LIST(
        icon = R.drawable.list_view
    )
}

//Auxiliary class for UI
//TODO: change image from int to url, using coil
data class CollectionCardInfo(
    val id: String,
    val name: String,
    val image: Int,
    val location: String,
    val tasks: List<Task> = emptyList()
)

data class CollectionState(
    //TODO: this could be changed to tasks of the day (number)
    val pendingWatering: Int = 0,
    val todayClimate: String = "",
    val healthPercentage: Float = 0f,
    val searchQuery: String = "",
    val selectedLayout: CollectionLayout = CollectionLayout.GRID,
    val plantList: List<CollectionCardInfo> = emptyList(),
)