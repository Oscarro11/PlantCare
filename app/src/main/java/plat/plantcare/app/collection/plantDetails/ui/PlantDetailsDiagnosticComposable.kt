package plat.plantcare.app.collection.plantDetails.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import plat.plantcare.app.R
import plat.plantcare.app.diagnosis.domain.Diagnostic
import plat.plantcare.app.core.ui.AccordionSection
import plat.plantcare.app.diagnosis.ui.DiagnosisResultContent

@Composable
fun DiagnosticSection(description: String, diagnostic: Diagnostic?, onCreate: () -> Unit, modifier: Modifier = Modifier) {
    AccordionSection(icon = R.drawable.monitor, title = "Diagnóstico", description = description,
        isEnabled = true, modifier = modifier) {
        if (diagnostic == null) Text("Todavía no hay un diagnóstico guardado para esta planta.")
        else DiagnosisResultContent(diagnostic)
        Button(onClick = onCreate, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text("Nuevo diagnóstico")
        }
    }
}
