package no.nav.syfo.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.NOT_FOUND)
internal class ResourceNotFoundException(message: String, httpStatus: HttpStatus, reason: String, loglevel: LogLevel) :
    AbstractApiError(
        message = message,
        httpStatus = httpStatus,
        reason = reason,
        loglevel = loglevel
    )
