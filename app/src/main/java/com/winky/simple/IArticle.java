package com.winky.simple;

import com.winky.annotation.http.BaseUrl;
import com.winky.annotation.http.Get;
import com.winky.annotation.http.Parameter;
import com.winky.annotation.http.Post;

import okhttp3.Call;

@BaseUrl("http://apicloud.mob.com/")
public interface IArticle {

    /**
     * aaaaaaaaaaaaaaaaaaaaaa
     * @param key
     * @return
     */
    @Get("wx/article/category/query")
    Call queryArticle(@Parameter("key") String key);

    /**
     * bbbbbbbbbbbbbbbbb
     * @param key
     * @return
     */
    @Post("wx/article/category/query")
    Call queryArticle1(@Parameter("key") String key);
}
