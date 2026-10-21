package cl.duoc.corpaliv.ui.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Formatos chilenos para mostrar datos en pantalla
private val LOCALE_CL: Locale = Locale.forLanguageTag("es-CL")
private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE_CL)

// 15000 -> "$15.000"
fun formatoMonto(monto: Int): String =
    "$" + NumberFormat.getIntegerInstance(LOCALE_CL).format(monto)

fun formatoFecha(fecha: LocalDate): String = fecha.format(FORMATO_FECHA)

fun formatoFecha(epochMillis: Long): String =
    formatoFecha(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate())
