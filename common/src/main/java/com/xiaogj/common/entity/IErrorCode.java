package com.xiaogj.common.entity;

/**
 * @ClassName: IErrorCode
 * @Description: 封装API的错误码
 * @Author: liupeng
 * @Date: 2020/5/7 19:03
 **/
public interface IErrorCode {
    long getCode();

    String getMessage();
}
