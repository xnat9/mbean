package my.mbean.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * Created by xnat on 17/5/7.
 */
public class GsonJsonUtil extends JsonUtil {

    @Override
    public String toJson(Object pObject) {
        return getGson().toJson(pObject);
    }

    @Override
    public <T> T fromJson(String pJsonStr, Class<T> exceptType) {
        return getGson().fromJson(pJsonStr, exceptType);
    }

    @Override
    public void write(Object pObject, OutputStream pOutputStream) {
        OutputStreamWriter writer = new OutputStreamWriter(pOutputStream, Charset.forName("UTF-8"));
        getGson().toJson(pObject, writer);
    }

    protected Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }
}
