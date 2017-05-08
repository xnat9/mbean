package my.mbean.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by xnat on 17/5/7.
 */
public class JacksonJsonUtil extends JsonUtil {
    @Override
    public String toJson(Object pObject) {
        try {
            return getObjectMapper().writeValueAsString(pObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T fromJson(String pJsonStr, Class<T> exceptType) {
        try {
            return getObjectMapper().readValue(pJsonStr, exceptType);
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return null;
    }

    @Override
    public void write(Object pObject, OutputStream pOutputStream) {
        try {
            getObjectMapper().writeValue(pOutputStream, pObject);
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
