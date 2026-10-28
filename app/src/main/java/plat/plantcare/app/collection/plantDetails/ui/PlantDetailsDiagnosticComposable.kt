package plat.plantcare.app.collection.plantDetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import plat.plantcare.app.R
import plat.plantcare.app.core.data.models.Diagnostic
import plat.plantcare.app.core.data.models.getRemainingDays
import plat.plantcare.app.core.ui.AccordionSection
import plat.plantcare.app.core.ui.darkenColor

@Composable
fun DiagnosticSection(
    description: String,
    diagnostic: Diagnostic?,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AccordionSection(
        icon = R.drawable.monitor,
        title = "Diagnóstico",
        description = description,
        isEnabled = diagnostic != null
    ) {
        DiagnosticContent(
            diagnostic = diagnostic,
            onCreate = onCreate,
            modifier = modifier
        )
    }
}

@Composable
private fun DiagnosticContent(
    diagnostic: Diagnostic?,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (diagnostic != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = darkenColor(MaterialTheme.colorScheme.background, 0.05f),
                        shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.check_circle),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(Modifier.width(5.dp))

                    Text(
                        text = "Estado del follaje",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )

                    //TODO: update
                    StatusPill("Excelente")
                }

                Text(diagnostic.foliageState,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                DiagnosisStat(
                    R.drawable.bug,
                    "Control plagas",
                    diagnostic.pestControl,
                    Modifier.weight(1f)
                )
                DiagnosisStat(
                    R.drawable.power_leaf,
                    "Análisis de vigor",
                    diagnostic.growthVigor,
                    Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row{
                    Icon(
                        painter = painterResource(R.drawable.star),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Último escaneo IA:", style = MaterialTheme.typography.labelSmall)
                }

                Text(
                    text = "Hace ${diagnostic.date.getRemainingDays()} días",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Button(
                onClick = onCreate,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    painterResource(id = R.drawable.camera),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(20.dp))

                Text("Escanear nueva foto con IA")
            }
        }
    }
}

@Composable
private fun DiagnosisStat(icon: Int, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StatusPill(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .background(darkenColor(MaterialTheme.colorScheme.primary), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}