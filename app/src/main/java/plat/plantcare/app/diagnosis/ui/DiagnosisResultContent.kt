package plat.plantcare.app.diagnosis.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import plat.plantcare.app.diagnosis.domain.Diagnostic
import plat.plantcare.app.diagnosis.domain.getHealthStatus
import plat.plantcare.app.diagnosis.ui.label

@Composable
fun DiagnosisResultContent(diagnostic: Diagnostic, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Evaluación visual · ${diagnostic.getHealthStatus().label()}", style = MaterialTheme.typography.titleLarge)
        Text("Fecha: ${diagnostic.date}", style = MaterialTheme.typography.bodySmall)
        if (diagnostic.summary.isNotBlank()) Text(diagnostic.summary)
        listOf(
            "Follaje" to diagnostic.foliageState,
            "Vigor y crecimiento" to diagnostic.growthVigor,
            "Señales de plagas" to diagnostic.pestControl,
            "Señales de enfermedad" to diagnostic.diseaseSigns,
            "Flores o frutos visibles" to when (diagnostic.hasFlowersOrFruits) { true -> "Sí"; false -> "No se observan"; null -> "No evaluable" }
        ).forEach { (title, value) -> if (value.isNotBlank()) {
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } }
        } }
        listOf("Posibles causas" to diagnostic.possibleCauses, "Cuidados recomendados" to diagnostic.recommendations,
            "Limitaciones del análisis" to diagnostic.limitations).forEach { (title, values) ->
            if (values.isNotEmpty()) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                values.forEach { Text("• $it") }
            }
        }
        Text("Orientación generada con IA a partir de una foto. No confirma una enfermedad ni sustituye la evaluación presencial.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
