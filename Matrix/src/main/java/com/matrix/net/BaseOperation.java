package com.matrix.net;

import com.matrix.db.entity.BaseEntity;
import org.apache.http.params.HttpParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class BaseOperation implements Serializable {
    private static final long serialVersionUID = -5529432321201760124L;

    private String url;
    private String tag = "BaseOperation";
    private Method method;
    private ArrayList<BaseEntity> requestEntities = new ArrayList<BaseEntity>();
    public ArrayList<BaseEntity> responseEntities = new ArrayList<BaseEntity>();

    private int responseStatusCode;
    private String responseError;
    private String responseString;

    public enum Method {
        GET, POST, PUT, PATCH, DELETE, POST_MULTIPART
    }

    protected HttpParams params;

    private String[] args;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ArrayList<BaseEntity> getEntities() {
        return requestEntities;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getResponseError() {
        return responseError;
    }

    public void setResponseError(String responseError) {
        this.responseError = responseError;
    }

    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    public void setResponseStatusCode(int responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public void setUrl(String url, String... args) {
        this.url = url;
        this.args = args;
    }

    public String getRequestUrl() {
        if (args != null) {
            return String.format(url, args);
        } else {
            return url;
        }
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<? extends BaseEntity> getResponseEntities() {
        return responseEntities;
    }

    @Override
    public String toString() {
        return "BaseOperation{" +
                "url='" + url + '\'' +
                ", tag='" + tag + '\'' +
                ", method=" + method +
                ", responseStatusCode=" + responseStatusCode +
                ", responseError='" + responseError + '\'' +
                ", responseString='" + responseString + '\'' +
                ", params=" + params +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
