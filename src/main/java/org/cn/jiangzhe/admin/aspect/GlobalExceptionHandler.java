package org.cn.jiangzhe.admin.aspect;

import cn.hutool.core.util.ArrayUtil;
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
    public R serviceExceptionHandler(ServiceException e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (ArrayUtil.isNotEmpty(stackTrace)) {
            StackTraceElement stackTraceElement = stackTrace[0];
            log.error("服务异常\t【类名】:{}\t【方法名】:{}\t【行号】:{}\t【消息】:{}",
                    stackTraceElement.getClassName(), stackTraceElement.getMethodName(),
                    stackTraceElement.getLineNumber(), e.getMessage());
        }
        return R.failed(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public R exceptionHandler(Exception e) {
        e.printStackTrace();
        return R.failed("系统内部异常");
    }

}
