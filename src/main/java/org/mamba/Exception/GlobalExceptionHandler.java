package org.mamba.Exception;

import org.mamba.entity.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result hanleException(Exception ex){
        ex.printStackTrace();
        return Result.error(StringUtils.hasLength(ex.getMessage())? ex.getMessage() : "Failed!");
    }
}
