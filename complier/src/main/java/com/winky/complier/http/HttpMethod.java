package com.winky.complier.http;

import com.squareup.javapoet.TypeName;
import com.winky.complier.Config;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

public class HttpMethod {

    private String methodName;
    private TypeName returnType;
    /**
     * Method's parameter caching
     */
    private List<HttpParam> paramListCache = new ArrayList<>(Config.ANNOTATION_INIT_COUNT);
    /**
     * The value of the method annotation, the interface name
     */
    private String relativeUrl;
    /**
     * GET POST
     */
    private String httpMethod;

    /**
     * Add properties
     *
     * @param httpParam
     */
    public void addParemeter(HttpParam httpParam) {
        paramListCache.add(httpParam);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setReturnType(TypeName returnType) {
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public TypeName getReturnType() {
        return returnType;
    }

    public List<HttpParam> getParamListCache() {
        return paramListCache;
    }
}
