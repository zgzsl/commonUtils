package com.cntracechain.commonUtils.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName HttpClientResult
 * @Description 封装httpClient响应结果
 * @Author chenxw
 * @Date 2019/5/14 13:41
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpClientResult {

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;

    public HttpClientResult(int code) {
        this.code = code;
    }
}
