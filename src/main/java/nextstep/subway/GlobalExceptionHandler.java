package nextstep.subway;

import nextstep.subway.common.ErrorResponse;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NoLineExistException;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.exception.NotSameUpAndDownStationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoLineExistException.class)
    public ResponseEntity<String> lineException(Exception exception) {
        return ErrorResponse.badRequest(exception.getMessage());
    }

    @ExceptionHandler({NoStationException.class, NotSameUpAndDownStationException.class})
    public ResponseEntity<String> stationException(Exception exception) {
        return ErrorResponse.badRequest(exception.getMessage());
    }

    @ExceptionHandler(CannotDeleteSectionException.class)
    public ResponseEntity<String> sectionException(Exception exception) {
        return ErrorResponse.badRequest(exception.getMessage());
    }
}
