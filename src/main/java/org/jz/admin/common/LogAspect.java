package org.jz.admin.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
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

import javax.servlet.ServletResponse;

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

    @Pointcut("@within(org.jz.admin.common.CommonLog) || @annotation(org.jz.admin.common.CommonLog)")
    public void paramsLog() {

    }

    @Around("paramsLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = (Logger) ReflectUtil.getFieldValue(joinPoint.getTarget(), "log");
        log = ObjectUtil.defaultIfNull(log, LogAspect.log);

        Object[] args = joinPoint.getArgs();
        MapBuilder<String, Object> builder = MapUtil.builder();
        for (Object arg : args) {
            if (arg == null || arg instanceof ServletResponse) {
                continue;
            }
            if (ObjectUtil.isBasicType(arg) || arg instanceof String) {
                builder.put(arg.getClass().getSimpleName(), arg.toString());
            } else if (BeanUtil.isBean(arg.getClass())) {
                builder.put(arg.getClass().getName(), arg);
            }
        }
        log.info("日志打印\t【方法名】{}\t【捕获对象类】{}\t【参数】{}",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getName(),
                mapper.writeValueAsString(builder.build()));
        Object res = joinPoint.proceed();
        log.info("日志打印\t【方法名】{}【返回对象】{}", joinPoint.getSignature().getName(), mapper.writeValueAsString(res));
        return res;
    }
}
