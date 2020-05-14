package org.cn.jiangzhe.onlineview;

/**
 *  定义service层异常，用于给Controller层捕获
 *  以便在全局异常处理中返回错误信息
 * */
public class ServiceException extends RuntimeException{
    public ServiceException(String msg, Throwable throwable){
        super(msg, throwable);
    }

    public ServiceException(String msg){
        super(msg);
    }
}
