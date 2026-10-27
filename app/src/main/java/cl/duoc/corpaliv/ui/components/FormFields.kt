package cl.duoc.corpaliv.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Campos de formulario con retroalimentación visual por campo:
// - Con error: borde rojo, ícono de error y el mensaje debajo.
// - Válido (con contenido): ícono de check en violeta.

@Composable
fun CorpalivTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    minLines: Int = 1,
    supportingHint: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = { FieldStatusIcon(error = error, hasValue = value.isNotBlank()) },
        isError = error != null,
        supportingText = (error ?: supportingHint)?.let { texto -> { Text(texto) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        singleLine = singleLine,
        minLines = minLines,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    supportingHint: String? = null
) {
    // rememberSaveable: la preferencia de mostrar/ocultar sobrevive a la rotación
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = if (error != null) Icons.Filled.Error else Icons.Outlined.Lock,
                contentDescription = if (error != null) "Error" else null,
                // Material pinta el ícono inicial en gris aunque haya error: se fuerza el rojo
                tint = if (error != null) MaterialTheme.colorScheme.error else LocalContentColor.current
            )
        },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (visible) "Ocultar clave" else "Mostrar clave"
                )
            }
        },
        isError = error != null,
        supportingText = (error ?: supportingHint)?.let { texto -> { Text(texto) } },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.fillMaxWidth()
    )
}

private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

// Campo de fecha de solo lectura: al tocarlo abre el DatePicker de Material 3.
// soloPasadas / soloFuturas limitan los días que se pueden elegir.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    value: LocalDate?,
    onValueChange: (LocalDate) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    soloPasadas: Boolean = false,
    soloFuturas: Boolean = false,
    supportingHint: String? = null
) {
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }

    // Un TextField de solo lectura no recibe clicks: se escucha cuando se suelta el toque
    LaunchedEffect(interaction) {
        interaction.interactions.collect {
            if (it is PressInteraction.Release) mostrarDialogo = true
        }
    }

    OutlinedTextField(
        value = value?.format(FORMATO_FECHA) ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("dd/mm/aaaa") },
        leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
        trailingIcon = { FieldStatusIcon(error = error, hasValue = value != null) },
        isError = error != null,
        supportingText = (error ?: supportingHint)?.let { texto -> { Text(texto) } },
        interactionSource = interaction,
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.fillMaxWidth()
    )

    if (mostrarDialogo) {
        // El DatePicker trabaja en milisegundos UTC
        val hoyUtc = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val state = rememberDatePickerState(
            initialSelectedDateMillis = value?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = when {
                    soloPasadas -> utcTimeMillis <= hoyUtc
                    soloFuturas -> utcTimeMillis >= hoyUtc
                    else -> true
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onValueChange(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    mostrarDialogo = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

// Fila con checkbox: toda la fila es tocable (mejor accesibilidad)
@Composable
fun CheckboxField(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
        ) {
            Checkbox(checked = checked, onCheckedChange = null)
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
        }
        FieldErrorText(error)
    }
}

@Composable
fun SwitchField(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = null)
    }
}

// Mensaje de error con ícono para controles que no son TextField.
// Aparece y desaparece animado.
@Composable
fun FieldErrorText(error: String?, modifier: Modifier = Modifier) {
    var ultimo by remember { mutableStateOf(error) }
    if (error != null) ultimo = error
    AparecerVertical(visible = error != null) {
        FieldErrorRow(ultimo.orEmpty(), modifier)
    }
}

@Composable
private fun FieldErrorRow(error: String, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.padding(start = 12.dp, top = 2.dp)
    ) {
        Icon(Icons.Filled.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
        Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun FieldStatusIcon(error: String?, hasValue: Boolean) {
    when {
        error != null -> Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
        hasValue -> Icon(Icons.Filled.CheckCircle, contentDescription = "Válido", tint = MaterialTheme.colorScheme.primary)
    }
}
