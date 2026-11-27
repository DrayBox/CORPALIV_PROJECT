package cl.duoc.corpaliv.contenido.controller

import cl.duoc.corpaliv.contenido.dto.ErrorResponse
import cl.duoc.corpaliv.contenido.service.NoEncontradoException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
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
    fun noEncontrado(e: NoEncontradoException) = ErrorResponse(e.message)

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun camposInvalidos(e: HandlerMethodValidationException) = ErrorResponse(
        "Revisa los datos",
        e.beanResults.flatMap { it.fieldErrors }.associate { it.field to it.defaultMessage.orEmpty() }
    )

    // @Valid en el cuerpo de la petición
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun cuerpoInvalido(e: MethodArgumentNotValidException) = ErrorResponse(
        "Revisa los datos",
        e.bindingResult.fieldErrors.associate { it.field to it.defaultMessage.orEmpty() }
    )

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun parametroInvalido(e: MethodArgumentTypeMismatchException) = ErrorResponse("El valor de ${e.name} no es válido")

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun jsonInvalido(e: HttpMessageNotReadableException) = ErrorResponse("El formato de los datos no es válido")
}
