package com.cntracechain.commonUtils.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @ClassName HttpClientUtils
 * @Description httpClient工具类
 * @Author chenxw
 * @Date 2019/5/14 12:01
 * @Version 1.0
 **/
public class HttpClientUtils {

    // 编码格式。发送编码格式统一用UTF-8
    private static final String ENCODING = "UTF-8";

    // 设置连接超时时间，单位毫秒。
    private static final int CONNECT_TIMEOUT = 6000;

    // 请求获取数据的超时时间(即响应时间)，单位毫秒。
    private static final int SOCKET_TIMEOUT = 6000;

    //统一配置请求属性
    private static RequestConfig requestConfig;

    static {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
    }

    /**
     * 封装请求头
     * @param params
     * @param httpMethod
     */
    private static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 封装请求参数
     * @param params
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    private static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod)
            throws UnsupportedEncodingException {
        // 封装请求参数
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            // 设置到请求的http对象中
            httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
        }
    }

    /**
     * 获得响应结果
     * @param httpResponse
     * @param httpClient
     * @param httpMethod
     * @return
     * @throws Exception
     */
    private static HttpClientResult getHttpClientResult(CloseableHttpResponse httpResponse,
                                                       CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        // 执行请求
        httpResponse = httpClient.execute(httpMethod);

        // 获取返回结果
        if (httpResponse != null && httpResponse.getStatusLine() != null) {
            String content = "";
            if (httpResponse.getEntity() != null) {
                content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
            }
            return new HttpClientResult(httpResponse.getStatusLine().getStatusCode(), content);
        }
        return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * 释放资源
     * @param httpResponse
     * @param httpClient
     * @throws IOException
     */
    private static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) throws IOException {
        // 释放资源
        if (httpResponse != null) {
            httpResponse.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }

    /**
     * 发送get请求；不带请求头和请求参数
     * @param url   请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult getRequest(String url) throws Exception {
        return getRequest(url, null, null);
    }

    /**
     * 发送get请求；带请求参数
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpClientResult getRequest(String url, Map<String, String> params) throws Exception {
        return getRequest(url, null, params);
    }

    /**
     * 发送get请求；带请求头和请求参数
     * @param url   请求地址
     * @param headers   请求头集合
     * @param params    请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult getRequest(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建访问的地址
        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }
        // 创建http对象
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setConfig(requestConfig);
        // 设置请求头
        packageHeader(headers, httpGet);
        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;
        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpGet);
        } finally {
            // 释放资源
            release(httpResponse, httpClient);
        }
    }

    /**
     * 发送post请求；不带请求头和请求参数
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpClientResult postRequest(String url) throws Exception {
        return postRequest(url,null,null);
    }

    /**
     * 发送post请求；带请求参数
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpClientResult postRequest(String url, Map<String, String> params) throws Exception {
        return postRequest(url, null, params);
    }

    /**
     * 发送post请求；带请求头和请求参数
     * @param url
     * @param headers
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpClientResult postRequest(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        packageHeader(headers, httpPost);

        // 封装请求参数
        packageParam(params, httpPost);

        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;

        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        } finally {
            // 释放资源
            release(httpResponse, httpClient);
        }
    }

    /**
     * 发送put请求；带请求参数
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpClientResult putRequest(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(requestConfig);

        packageParam(params, httpPut);

        CloseableHttpResponse httpResponse = null;

        try {
            return getHttpClientResult(httpResponse, httpClient, httpPut);
        } finally {
            release(httpResponse, httpClient);
        }
    }

    /**
     * 发送put请求；不带请求参数
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpClientResult putRequest(String url) throws Exception {
        return putRequest(url,null);
    }

    /**
     * 发送delete请求；不带请求参数
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpClientResult deleteRequest(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(requestConfig);

        CloseableHttpResponse httpResponse = null;
        try {
            return getHttpClientResult(httpResponse, httpClient, httpDelete);
        } finally {
            release(httpResponse, httpClient);
        }
    }

    /**
     * 发送delete请求；带请求参数
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpClientResult doDelete(String url, Map<String, String> params) throws Exception {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        params.put("_method", "delete");
        return postRequest(url, params);
    }

    /**
     * FORM表单提交（带文件）
     * @param url
     * @param headerParams
     * @param file
     * @param fileName
     * @param fileField
     * @param formFields
     * @return
     * @throws Exception
     */
    public static HttpClientResult postMultipartRequest(String url, Map<String,String> headerParams, byte [] file, String fileName, String fileField, Map<String,String> formFields) throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody(fileField,file, ContentType.APPLICATION_OCTET_STREAM,fileName);
        packageHeader(headerParams,post);
        if (formFields != null) {
            for (Map.Entry<String,String> formField : formFields.entrySet()) {
                StringBody stringBody = new StringBody(formField.getValue(), ContentType.create("text/plain", "utf-8"));
                multipartEntityBuilder.addPart(formField.getKey(),stringBody);
            }
        }
        HttpEntity httpEntity = multipartEntityBuilder.build();
        post.setEntity(httpEntity);
        CloseableHttpResponse response = null;
        try {
            return getHttpClientResult(response,httpClient,post);
        } finally {
            release(response, httpClient);
        }
    }

    /**
     * post提交json数据
     * @param url
     * @param headers
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public static HttpClientResult postJsonRequest(String url, Map<String,String> headers, String jsonStr) throws Exception {
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        headers.put("Content-Type", "application/json;charset=UTF-8");
        packageHeader(headers, httpPost);

        StringEntity requestEntity = new StringEntity(jsonStr,"utf-8");
        requestEntity.setContentType("text/json");
        httpPost.setEntity(requestEntity);

        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;

        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        } finally {
            // 释放资源
            release(httpResponse, httpClient);
        }
    }
}
