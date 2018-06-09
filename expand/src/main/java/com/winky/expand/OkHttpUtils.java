package com.winky.expand;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class OkHttpUtils {

    private static Call.Factory callFactory;
    private static HttpUrl httpUrl;

    public OkHttpUtils(HttpUrl httpUrl, Call.Factory callFactory) {
        this.httpUrl = httpUrl;
        this.callFactory = callFactory;
    }

    public static Call.Factory getCallFactory() {
        return callFactory;
    }

    public static HttpUrl getHttpUrl() {
        return httpUrl;
    }

    public <T> T create(Class<T> clazz) {
        String clazzPath = clazz.getPackage().getName() + ".impl." + clazz.getSimpleName() + "Impl";
        try {
            return (T) clazz.getClassLoader().loadClass(clazzPath).newInstance();
//            return (T) Class.forName(clazzPath).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Builder {
        private Call.Factory callFactory;
        private HttpUrl httpUrl;

        public Builder baseUrl(String url) {
            this.httpUrl = HttpUrl.parse(url);
            return this;
        }

        public Builder callFactory(OkHttpClient okHttpClient) {
            this.callFactory = okHttpClient;
            return this;
        }

        public OkHttpUtils build() {
            if (this.httpUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }
            if (this.callFactory == null) {
                this.callFactory = new OkHttpClient();
            }
            return new OkHttpUtils(httpUrl, callFactory);
        }
    }
}
