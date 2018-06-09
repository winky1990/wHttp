package com.winky.annotation.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface HttpType {

    public static final String GET = "GET";
    public static final String POST = "POST";
}
