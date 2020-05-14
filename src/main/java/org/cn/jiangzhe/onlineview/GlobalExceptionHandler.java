package org.cn.jiangzhe.onlineview;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 *
 * @Date 2019/3/29 18:05
 * Version      1.0
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ServiceException.class})
    public R serviceExceptionHandler(ServiceException e) {
        log.error(e.getMessage());
        return R.failed(e.getMessage());
    }


    @ExceptionHandler({Exception.class})
    public R exceptionHandler(Exception exception) {
        log.error(exception.getMessage());
        return R.failed("内部异常");
    }

}
