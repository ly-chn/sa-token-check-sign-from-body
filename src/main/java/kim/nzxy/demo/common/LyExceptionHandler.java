package kim.nzxy.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class LyExceptionHandler {
    @ExceptionHandler
    public String handleException(Exception e) {
        log.error("发生异常", e);
        return "异常: " + e.getMessage();
    }
}
