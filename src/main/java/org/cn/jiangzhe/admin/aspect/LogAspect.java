package org.cn.jiangzhe.admin.aspect;

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
        log = ObjectUtil.defaultIfNull(log, LogAspect.log);

        Object[] args = joinPoint.getArgs();
        MapBuilder<String, Object> builder = MapUtil.builder();
        for (Object arg : args) {
            if (ObjectUtil.isBasicType(arg) || arg instanceof String) {
                builder.put(arg.getClass().getSimpleName(), arg.toString());
            } else if (BeanUtil.isBean(arg.getClass())) {
                builder.put(arg.getClass().getName(), arg);
            }
        }
        log.info("日志打印\t【捕获对象类】:{}\t【方法名】:{}\t【参数】:{}",
                joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(),
                mapper.writeValueAsString(builder.build()));
        Object res = joinPoint.proceed();
        log.info("日志打印\t【返回对象】:{}", mapper.writeValueAsString(res));
        return res;
    }
}
