package org.jz.admin.common;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.sun.xml.internal.ws.developer.Serialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
@AllArgsConstructor
public class Response<T> {


    private long code;
    private T data;
    private String msg;

    public static <T> Response<T> ok(T data) {
        return new Response<T>(0, data, "成功");
    }

    public static <T> Response<T> ok() {
        return ok(null);
    }

    public static <T> Response<T> failed() {
        return new Response<T>(-1, null, "失败");
    }

    public static <T> Response<T> failed(String msg) {
        return new Response<>(-1, null, msg);
    }

    public static <T> Response<T> failed(long code, String msg) {
        return new Response<>(code, null, msg);
    }

}
