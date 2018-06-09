package com.winky.simple;

import com.winky.expand.OkHttpUtils;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testHttp() {
        try {
            OkHttpUtils httpUtils = new OkHttpUtils.Builder().baseUrl("http://apicloud.mob.com/").build();

            IArticle iArticle = httpUtils.create(IArticle.class);
            Call call = iArticle.queryArticle("25a9b44caa1c5");

            Response response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}