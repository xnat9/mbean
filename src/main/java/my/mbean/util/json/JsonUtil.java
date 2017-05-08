package my.mbean.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ClassUtils;

import java.io.OutputStream;

/**
 * Created by xnat on 17/5/7.
 */
public abstract class JsonUtil {
    public abstract String toJson(Object pObject);

    public abstract <T> T fromJson(String pJsonStr, Class<T> exceptType);

    public abstract void write(Object pObject, OutputStream pOutputStream);

    public static JsonUtil get(ClassLoader pClassLoader) {
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", pClassLoader)) {
            return new JacksonJsonUtil();
        } else if (ClassUtils.isPresent("com.google.gson.Gson", pClassLoader)) {
            return new GsonJsonUtil();
        }
        return null;
    }

    public static JsonUtil get() {
        return get(ClassUtils.getDefaultClassLoader());
    }
}
