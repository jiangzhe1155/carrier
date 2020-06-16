package org.cn.jiangzhe.admin.aspect;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 *
 * @Date 2019/3/29
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ServiceException.class})
    public R serviceExceptionHandler(Exception e) {
        return R.failed(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public R exceptionHandler(Exception e) {
        e.printStackTrace();
        return R.failed("系统内部异常");
    }

}
