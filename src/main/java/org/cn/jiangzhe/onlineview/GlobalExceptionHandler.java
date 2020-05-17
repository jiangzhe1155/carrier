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

    @ExceptionHandler({ServiceException.class,Exception.class})
    public R serviceExceptionHandler(Exception e) {
        e.printStackTrace();
        return R.failed(e.getMessage());
    }

}
