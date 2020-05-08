package com.xiaogj.common.exception;


import com.xiaogj.common.entity.IErrorCode;

 /**
 * @ClassName: Asserts
 * @Description: 断言处理类，用于抛出各种API异常
 * @author: liupeng
 * @date: 2020/5/7 18:14
 **/
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }
}
