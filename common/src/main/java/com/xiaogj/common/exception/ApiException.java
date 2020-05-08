package com.xiaogj.common.exception;


import com.xiaogj.common.entity.IErrorCode;

/**
 * @ClassName: ApiException
 * @Description: 自定义API异常
 * @Author: liupeng
 * @Date: 2020/5/7 18:14
 **/
public class ApiException extends RuntimeException {
    private IErrorCode errorCode;

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
