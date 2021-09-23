package kr.or.uknowhat.api.framework.common;


import java.io.FileNotFoundException;
import java.util.zip.DataFormatException;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import kr.or.uknowhat.api.framework.exception.BizException;
import kr.or.uknowhat.api.ubusiness.util.ErrorCodeUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {


    @Autowired
    ErrorCodeUtil bizCodeUtil;
    
    // 400
    @ExceptionHandler({
            BizException.class,
    })
    public ResponseEntity<Object> bizException(BizException ex) {
    	log.debug("BizException {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bizCodeUtil.error(ex.getMessage(),ex.getBizErrorCode()));
    }
    
    // 400
    @ExceptionHandler({
            RuntimeException.class,
            NumberFormatException.class
    })
    public ResponseEntity<Object> badRequestException(Exception ex) {
        log.debug("badRequestException {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bizCodeUtil.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    // 422
    @ExceptionHandler({
            ClassCastException.class,
            NoSuchFieldException.class,
            DataIntegrityViolationException.class,
            DataAccessResourceFailureException.class,
            DataRetrievalFailureException.class,
            DataFormatException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<Object> entityException(Exception ex) {
        log.debug("entityException {}", ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(bizCodeUtil.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    // 422
    @ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> notReadableException(Exception ex) {
        log.debug("notReadableException {}", ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(bizCodeUtil.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    // 422
    @ExceptionHandler({
            InvalidDataAccessApiUsageException.class,
            DataAccessException.class
    })
    public ResponseEntity<Object> invalidDataAccessApiUsageException(Exception ex) {
        log.debug("invalidDataAccessApiUsageException {}", ex);
//        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("The given id must not be null!");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(bizCodeUtil.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }


    // 401
    @ExceptionHandler({
            AccessDeniedException.class
    })
    public ResponseEntity handleAccessDeniedException(Exception ex) {
        log.debug("handleAccessDeniedException {}", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(bizCodeUtil.error(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }


    // 404
    @ExceptionHandler({
            FileNotFoundException.class
    })
    public ResponseEntity notFoundException(Exception ex) {
        log.debug("notFoundException {}", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bizCodeUtil.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }


    // 500
    @ExceptionHandler({
            Exception.class,
            SQLGrammarException.class
    })
    public ResponseEntity<Object> handleAll(Exception ex) {
        log.debug("handleAll {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(bizCodeUtil.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bizCodeUtil.error(errorMessage));
    }

}
