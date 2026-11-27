package cl.duoc.corpaliv.solicitudes.controller

import cl.duoc.corpaliv.solicitudes.dto.ErrorResponse
import cl.duoc.corpaliv.solicitudes.service.DatoInvalidoException
import cl.duoc.corpaliv.solicitudes.service.SolicitudNoEncontradaException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

// Convierte las excepciones en respuestas JSON con un mensaje claro
@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noEncontrada(e: SolicitudNoEncontradaException) = ErrorResponse(e.message.orEmpty())

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun datoInvalido(e: DatoInvalidoException) = ErrorResponse("Revisa los datos", mapOf(e.campo to e.message))

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun camposInvalidos(e: HandlerMethodValidationException) = ErrorResponse(
        "Revisa los datos",
        e.beanResults.flatMap { it.fieldErrors }.associate { it.field to it.defaultMessage.orEmpty() }
    )

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun sinUsuario(e: MissingRequestHeaderException) = ErrorResponse("Falta el header ${e.headerName}")

    // Por ejemplo, un id que no es número
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun parametroInvalido(e: MethodArgumentTypeMismatchException) = ErrorResponse("El valor de ${e.name} no es válido")

    // JSON mal formado, fecha o tipo que no existe
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun jsonInvalido(e: HttpMessageNotReadableException) = ErrorResponse("El formato de los datos no es válido")
}
