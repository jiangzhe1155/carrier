package org.cn.jiangzhe.admin.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 江哲
 * @date 2020/06/14
 */
@Aspect
@Slf4j
@Component
public class LogAspect {

    @Autowired
    ObjectMapper mapper;

    @Pointcut("@within(org.cn.jiangzhe.admin.aspect.CommonLog) || @annotation(org.cn.jiangzhe.admin.aspect.CommonLog)")
    public void paramsLog() {

    }

    @Around("paramsLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = (Logger) ReflectUtil.getFieldValue(joinPoint.getTarget(), "log");
        if (log == null) {
            log = LogAspect.log;
        }

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg!=null && BeanUtil.isBean(arg.getClass())) {
                log.info("传入参数: {}", mapper.writeValueAsString(arg));
            }
        }
        Object res = joinPoint.proceed();
        log.info("返回结果: {}", mapper.writeValueAsString(res));
        return res;
    }
}
