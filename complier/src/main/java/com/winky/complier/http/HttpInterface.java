package com.winky.complier.http;

import com.winky.complier.Config;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpInterface {

    /**
     * Class name to be generated
     */
    private String clazzName;
    /**
     * The interface address that the generated class needs to be implemented
     */
    private String interfaceName;

    private String packageName;
    /**
     * Method caching
     */
    private Map<String, HttpMethod> methodMapCache = new ConcurrentHashMap<>(Config.ANNOTATION_INIT_COUNT);
    /**
     * Value of class annotation
     */
    private String baseUrl;

    /**
     * Add method
     *
     * @param method
     */
    public void addHttpMethod(String clazzName, HttpMethod method) {
        this.methodMapCache.put(clazzName, method);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClazzName() {
        return clazzName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Collection<HttpMethod> getMethodList() {
        return methodMapCache.values();
    }
}
