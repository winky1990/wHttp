package com.winky.complier.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.winky.annotation.http.BaseUrl;
import com.winky.annotation.http.Get;
import com.winky.annotation.http.HttpType;
import com.winky.annotation.http.Parameter;
import com.winky.annotation.http.Post;
import com.winky.complier.Config;
import com.winky.complier.http.HttpInterface;
import com.winky.complier.http.HttpMethod;
import com.winky.complier.http.HttpParam;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class HttpProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(BaseUrl.class.getCanonicalName());
        set.add(Get.class.getCanonicalName());
        set.add(Post.class.getCanonicalName());
        set.add(Parameter.class.getCanonicalName());
        return set;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    private final Map<String, HttpInterface> httpCache = new ConcurrentHashMap<>();
    private Elements elementUtils;

    private final ClassName httpUrlType = ClassName.get("okhttp3", "HttpUrl");
    private final ClassName httpUtilType = ClassName.get("com.winky.expand", "OkHttpUtils");
    private final ClassName formBodyType = ClassName.get("okhttp3", "FormBody");
    private final ClassName requestType = ClassName.get("okhttp3", "Request");

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        httpCache.clear();
        parseInterface(roundEnv);
        parseMethod(roundEnv);

//        Deque<JavaFile> specDeque = new ArrayDeque<>();
        for (String key : httpCache.keySet()) {
            if (key == null || key.length() == 0) {
                continue;
            }
            HttpInterface httpInterface = httpCache.get(key);
            TypeSpec.Builder classSpec = TypeSpec.classBuilder(httpInterface.getClazzName());
            classSpec.addSuperinterface(ClassName.get(elementUtils.getTypeElement(httpInterface.getInterfaceName())));
            classSpec.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            classSpec.addJavadoc(Config.JAVA_DOC);
            if (httpInterface.getBaseUrl() != null) {
                FieldSpec fieldSpec = FieldSpec.builder(httpUrlType, "httpUrl")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .initializer("HttpUrl.parse($S)", httpInterface.getBaseUrl())
                        .build();
                classSpec.addField(fieldSpec);
            }
            for (HttpMethod httpMethod : httpInterface.getMethodList()) {
                classSpec.addMethod(doMethod(httpInterface, httpMethod));
            }
            JavaFile javaFile = JavaFile.builder(httpInterface.getPackageName() + ".impl", classSpec.build()).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    private void parseInterface(RoundEnvironment roundEnv) {
        Set<TypeElement> typeElements = ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(BaseUrl.class));
        for (TypeElement typeElement : typeElements) {
            HttpInterface httpInterface = create(typeElement);
            httpInterface.setBaseUrl(typeElement.getAnnotation(BaseUrl.class).value());
        }
    }

    private HttpInterface create(TypeElement element) {
        HttpInterface httpInterface = new HttpInterface();
        String clazzName = element.getSimpleName().toString();
        httpInterface.setClazzName(clazzName + "Impl");
        String name = element.getQualifiedName().toString();
        httpInterface.setInterfaceName(name);
        httpInterface.setPackageName(name.substring(0, name.lastIndexOf(".")));
        httpCache.put(httpInterface.getInterfaceName(), httpInterface);
        return httpInterface;
    }

    private void parseMethod(RoundEnvironment roundEnv) {
        parseMethodByAnnotation(roundEnv, Get.class);
        parseMethodByAnnotation(roundEnv, Post.class);
    }

    private void parseAnnotationValue(HttpMethod httpMethod, Annotation annotation) {
        if (annotation instanceof Get) {
            httpMethod.setRelativeUrl(((Get) annotation).value());
            httpMethod.setHttpMethod(HttpType.GET);
        } else if (annotation instanceof Post) {
            httpMethod.setRelativeUrl(((Post) annotation).value());
            httpMethod.setHttpMethod(HttpType.POST);
        }
    }

    private void parseMethodByAnnotation(RoundEnvironment roundEnv, Class<? extends Annotation> annotation) {
        Set<ExecutableElement> getElements = ElementFilter.methodsIn(roundEnv.getElementsAnnotatedWith(annotation));
        for (ExecutableElement element : getElements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            //Finding corresponding class objects based on the class name of the method
            String clazzName = typeElement.asType().toString();
            HttpInterface httpInterface = httpCache.get(clazzName);
            if (httpInterface == null) {
                httpInterface = create(typeElement);
            }
            String methodName = element.getSimpleName().toString();
            HttpMethod httpMethod = new HttpMethod();
            httpMethod.setMethodName(methodName);
            httpMethod.setReturnType(TypeName.get(element.getReturnType()));
            parseAnnotationValue(httpMethod, element.getAnnotation(annotation));
            List<? extends VariableElement> variableElements = element.getParameters();
            if (variableElements != null) {
                parseParameter(httpMethod, variableElements);
            }
            httpInterface.addHttpMethod(methodName, httpMethod);
        }
    }

    private void parseParameter(HttpMethod httpMethod, List<? extends VariableElement> elements) {
        for (Element element : elements) {
            httpMethod.addParemeter(new HttpParam(element.getAnnotation(Parameter.class).value(), TypeName.get(element.asType()), element.getSimpleName().toString()));
        }
    }

    private MethodSpec doMethod(HttpInterface httpInterface, HttpMethod httpMethod) {
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(httpMethod.getMethodName());
        methodSpec.addModifiers(Modifier.PUBLIC);
        methodSpec.returns(httpMethod.getReturnType());
        methodSpec.addAnnotation(Override.class);
        for (HttpParam httpParam : httpMethod.getParamListCache()) {
            methodSpec.addParameter(httpParam.getTypeName(), httpParam.getName());
        }
        if (httpInterface.getBaseUrl() != null) {
            methodSpec.addStatement("$T.Builder builder = httpUrl.newBuilder($S)", httpUrlType, httpMethod.getRelativeUrl());
        } else {
            methodSpec.addStatement("$T.Builder builder = $T.getHttpUrl().newBuilder($S)", httpUrlType, httpUtilType, httpMethod.getRelativeUrl());
        }
        methodSpec.addStatement("$T formBody = null", formBodyType);
        switch (httpMethod.getHttpMethod()) {
            case HttpType.GET:
                for (HttpParam httpParam : httpMethod.getParamListCache()) {
                    methodSpec.addStatement("builder.addQueryParameter($S, " + httpParam.getName() + ")", httpParam.getAttr());
                }
                break;
            case HttpType.POST:
                methodSpec.addStatement("FormBody.Builder formBodyBuilder = new FormBody.Builder()");
                for (HttpParam httpParam : httpMethod.getParamListCache()) {
                    methodSpec.addStatement("formBodyBuilder.add($S, " + httpParam.getName() + ")", httpParam.getAttr());
                }
                methodSpec.addStatement("formBody = formBodyBuilder.build()");
                break;
        }
        methodSpec.addStatement("$T request = new Request.Builder().url(builder.build()).method($S, formBody).build()", requestType, httpMethod.getHttpMethod());
        methodSpec.addStatement("return $T.getCallFactory().newCall(request)", httpUtilType);
        return methodSpec.build();
    }

}
