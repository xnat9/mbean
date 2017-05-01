package my.mbean.util;

import java.util.HashMap;
import java.util.Map;

/**
 * used for common method return info.
 * @author hubert
 */
public class Response {
    private final Map<String, Object> data      = new HashMap<>(3);
    public static final String        SUCCESS   = "success";
    public static final String        ERROR_MSG = "errorMsg";
    public static final String        RESULT    = "result";



    public Response() {
        setSuccess(Boolean.TRUE);
    }



    /**
     * set default result success.
     * @param pSuccess
     */
    public Response(Boolean pSuccess) {
        setSuccess(pSuccess);
    }



    public static Response of(String pAttrName, Object pAttrValue) {
        return new Response().addObject(pAttrName, pAttrValue);
    }



    /**
     * setErrorMsg
     * @param pErrorMsg
     */
    public void setErrorMsg(String pErrorMsg) {
        data.put(ERROR_MSG, pErrorMsg);
    }



    /**
     * @return errorMsg
     */
    public String getErrorMsg() {
        return (String) data.get(ERROR_MSG);
    }



    /**
     * setSuccess
     * @param pSuccess
     */
    public void setSuccess(Boolean pSuccess) {
        data.put(SUCCESS, pSuccess);
    }



    /**
     * @return success.
     */
    public boolean isSuccess() {
        return (boolean) data.get(SUCCESS);
    }



    /**
     * add attribute into result object.
     * @param pAttrName
     *            attribute name.
     * @param pAttrValue
     *            attribute value.
     * @return Response
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Response addObject(String pAttrName, Object pAttrValue) {
        Object result = getResult();
        if (result == null) {
            result = new HashMap<>(7);
            setResult(result);
            ((Map) result).put(pAttrName, pAttrValue);
        } else if (result instanceof Map) {
            ((Map) result).put(pAttrName, pAttrValue);
        } else {
            throw new IllegalArgumentException("already exist result object, but is not like map structure.");
        }
        return this;
    }



    public Object getResult() {
        return data.get(RESULT);
    }



    public void setResult(Object pResult) {
        data.put(RESULT, pResult);
    }
}
