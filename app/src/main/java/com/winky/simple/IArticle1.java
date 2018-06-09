package com.winky.simple;

import com.winky.annotation.http.Get;
import com.winky.annotation.http.Parameter;

import okhttp3.Call;

public interface IArticle1 {

    /**
     * cccccccccccccccccc
     * @param key
     * @return
     */
    @Get("wx/article/category/query")
    Call queryArticle(@Parameter("key") String key);

}
