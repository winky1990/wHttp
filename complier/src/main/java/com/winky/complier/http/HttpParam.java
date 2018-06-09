package com.winky.complier.http;

import com.squareup.javapoet.TypeName;

public class HttpParam {

    /**
     * Request attribute
     */
    private String attr;
    /**
     * Method parameter name
     */
    private String name;
    /**
     * Parameter type
     */
    private TypeName typeName;

    public HttpParam(String attr, TypeName typeName, String name) {
        this.attr = attr;
        this.typeName = typeName;
        this.name = name;
    }

    public String getAttr() {
        return attr;
    }

    public String getName() {
        return name;
    }

    public TypeName getTypeName() {
        return typeName;
    }
}
