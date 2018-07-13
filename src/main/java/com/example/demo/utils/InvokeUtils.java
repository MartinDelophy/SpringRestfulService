package com.example.demo.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class InvokeUtils {

    public static Object methodInvoke(Method[] Methods,String methodSuffix,Object obj,Map.Entry<String, Object> entry) throws InvocationTargetException, IllegalAccessException, UnsupportedEncodingException {
            for (Method method : Methods) {
                if (method.getName().equals("set" + methodSuffix)) {
                    Class<?>[] type = method.getParameterTypes();
                    for (Class cls : type) {
                        switch (cls.getName()) {
                            case "java.lang.String":
                                method.invoke(obj, String.valueOf(entry.getValue()));
                                break;
                            case "int":
                                method.invoke(obj, Integer.valueOf(String.valueOf(entry.getValue())));
                                break;
                            case "long":
                                method.invoke(obj, Long.valueOf(String.valueOf(entry.getValue())));
                                break;
                            case "[B":
                                method.invoke(obj,entry.getValue().toString().getBytes("UTF-8"));
                                break;
                            case "boolean":
                                method.invoke(obj, Boolean.valueOf(String.valueOf(entry.getValue())));
                                break;

                        }
                    }

                }
            }
        return  obj;
    }

}
