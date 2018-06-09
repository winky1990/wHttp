package com.winky.simple;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.winky.expand.OkHttpUtils;
import com.winky.simple.impl.IArticleImpl;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void testHttp() {
        try {
            Class<?> clazz = IArticle.class;
            IArticle iArticle = null;
            ClassLoader classLoader = clazz.getClassLoader();
            String clazzPath = clazz.getPackage().toString() + ".impl." + clazz.getSimpleName() + "Impl";
//            com.winky.simple.impl.IArticleImpl
//            com.winky.simple.impl.IArticleImpl
            Class<?> realClazz;
            realClazz = ClassLoader.getSystemClassLoader().loadClass(clazzPath);
            if (realClazz != null) {
                iArticle = (IArticle) realClazz.newInstance();
            }
            System.out.println(String.valueOf(iArticle));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        IArticle iArticle=new IArticleImpl();
//        OkHttpUtils httpUtils = new OkHttpUtils.Builder().baseUrl("http://apicloud.mob.com/").build();
//        IArticle iArticle = httpUtils.create(IArticle.class);
//        Call call = iArticle.queryArticle("25a9b44caa1c5");
//        try {
//            Response response = call.execute();
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
