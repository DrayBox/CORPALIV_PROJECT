package cl.duoc.corpaliv.domain.validation

import cl.duoc.corpaliv.data.model.TipoColaboracion
import java.time.LocalDate
import java.time.Period

// Reglas de validación de los formularios. Son funciones puras: no dependen
// de Android ni de la UI, por eso se prueban con JUnit (ValidatorsTest).
// Convención: devuelven null si el valor es válido, o el mensaje de error.
object Validators {

    const val EDAD_MINIMA = 18
    const val EDAD_MAXIMA = 110
    const val NOMBRE_MAX = 60
    const val CLAVE_MIN = 8
    const val MENSAJE_MIN = 10
    const val MENSAJE_MAX = 500
    const val MONTO_MIN = 1_000
    const val MONTO_MAX = 10_000_000
    const val DIAS_CONTACTO_MAX = 90L

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    // Letras (con tildes y ñ), espacios, apóstrofes y guiones
    private val NOMBRE_REGEX = Regex("^[\\p{L} '-]+$")

    fun nombre(valor: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> "Ingresa tu nombre"
            limpio.length < 3 -> "El nombre debe tener al menos 3 letras"
            limpio.length > NOMBRE_MAX -> "Máximo $NOMBRE_MAX caracteres"
            !NOMBRE_REGEX.matches(limpio) -> "Usa solo letras y espacios"
            else -> null
        }
    }

    fun email(valor: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> "Ingresa tu correo"
            !EMAIL_REGEX.matches(limpio) -> "Correo no válido (ej: nombre@dominio.cl)"
            else -> null
        }
    }

    // La edad llega como texto desde el campo; se valida que sea un Int en rango
    fun edad(valor: String): String? {
        val numero = valor.trim().toIntOrNull()
        return when {
            valor.isBlank() -> "Ingresa tu edad"
            numero == null -> "La edad debe ser un número entero"
            numero < EDAD_MINIMA -> "Debes tener al menos $EDAD_MINIMA años"
            numero > EDAD_MAXIMA -> "Ingresa una edad válida"
            else -> null
        }
    }

    // hoy se recibe como parámetro para poder probar con una fecha fija
    fun fechaNacimiento(fecha: LocalDate?, hoy: LocalDate = LocalDate.now()): String? = when {
        fecha == null -> "Selecciona tu fecha de nacimiento"
        fecha.isAfter(hoy) -> "La fecha no puede ser futura"
        Period.between(fecha, hoy).years < EDAD_MINIMA -> "Debes tener al menos $EDAD_MINIMA años"
        else -> null
    }

    // Cruza los dos campos: la edad escrita debe calzar con la fecha elegida
    fun edadCoincideConFecha(edad: String, fecha: LocalDate?, hoy: LocalDate = LocalDate.now()): String? {
        val numero = edad.trim().toIntOrNull() ?: return null
        if (fecha == null) return null
        return if (Period.between(fecha, hoy).years != numero) {
            "La edad no coincide con la fecha de nacimiento"
        } else {
            null
        }
    }

    fun clave(valor: String): String? = when {
        valor.isEmpty() -> "Ingresa una clave"
        valor.length < CLAVE_MIN -> "Mínimo $CLAVE_MIN caracteres"
        valor.none { it.isLetter() } -> "Debe incluir al menos una letra"
        valor.none { it.isDigit() } -> "Debe incluir al menos un número"
        valor.any { it.isWhitespace() } -> "No puede tener espacios"
        else -> null
    }

    fun confirmacionClave(clave: String, confirmacion: String): String? = when {
        confirmacion.isEmpty() -> "Repite la clave"
        clave != confirmacion -> "Las claves no coinciden"
        else -> null
    }

    fun terminos(aceptados: Boolean): String? =
        if (aceptados) null else "Debes aceptar los términos para continuar"

    // --- Formulario de interés ---

    fun tipoColaboracion(tipo: TipoColaboracion?): String? =
        if (tipo == null) "Elige cómo quieres colaborar" else null

    // Monto en pesos chilenos, como Int. Puede ser obligatorio u opcional según el tipo.
    fun monto(valor: String, requerido: Boolean): String? {
        if (valor.isBlank()) return if (requerido) "Ingresa un monto" else null
        val numero = valor.trim().toIntOrNull()
        return when {
            numero == null -> "El monto debe ser un número entero"
            numero < MONTO_MIN -> "El monto mínimo es $1.000"
            numero > MONTO_MAX -> "El monto máximo es $10.000.000"
            else -> null
        }
    }

    fun fechaContacto(fecha: LocalDate?, hoy: LocalDate = LocalDate.now()): String? = when {
        fecha == null -> "Elige una fecha para contactarte"
        fecha.isBefore(hoy) -> "La fecha no puede ser pasada"
        fecha.isAfter(hoy.plusDays(DIAS_CONTACTO_MAX)) -> "Elige una fecha dentro de los próximos $DIAS_CONTACTO_MAX días"
        else -> null
    }

    fun mensaje(valor: String): String? {
        val largo = valor.trim().length
        return when {
            largo == 0 -> "Escribe un mensaje"
            largo < MENSAJE_MIN -> "Cuéntanos un poco más (mínimo $MENSAJE_MIN caracteres)"
            largo > MENSAJE_MAX -> "Máximo $MENSAJE_MAX caracteres"
            else -> null
        }
    }

    // En el login solo se exige que los campos no vengan vacíos:
    // no se revela qué reglas tiene la clave
    fun requerido(valor: String, campo: String): String? =
        if (valor.isBlank()) "Ingresa tu $campo" else null
}
