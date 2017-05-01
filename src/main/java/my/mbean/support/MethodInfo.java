package my.mbean.support;

import java.util.List;

public class MethodInfo {
    /**
     * method name.
     */
    private String       name;
    /**
     * method return type.
     */
    private String       returnType;
    /**
     * method declaringClass.
     */
    private String       declaringClass;
    /**
     * method url.
     */
    private String       url;
    /**
     * is or not callable.
     */
    private boolean      callable;
    /**
     * if method exist some annotation need to show.
     */
    private List<String> annotationMarks;



    /**
     * @return the name
     */
    public String getName() {
        return name;
    }



    /**
     * @return the returnType
     */
    public String getReturnType() {
        return returnType;
    }



    /**
     * @return the declaringClass
     */
    public String getDeclaringClass() {
        return declaringClass;
    }



    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }



    /**
     * @return the callable
     */
    public boolean isCallable() {
        return callable;
    }



    /**
     * @param pName
     *            the name to set
     */
    public void setName(String pName) {
        name = pName;
    }



    /**
     * @param pReturnType
     *            the returnType to set
     */
    public void setReturnType(String pReturnType) {
        returnType = pReturnType;
    }



    /**
     * @param pDeclaringClass
     *            the declaringClass to set
     */
    public void setDeclaringClass(String pDeclaringClass) {
        declaringClass = pDeclaringClass;
    }



    /**
     * @param pUrl
     *            the url to set
     */
    public void setUrl(String pUrl) {
        url = pUrl;
    }



    /**
     * @param pCallable
     *            the callable to set
     */
    public void setCallable(boolean pCallable) {
        callable = pCallable;
    }



    /**
     * @return the annotationMarks
     */
    public List<String> getAnnotationMarks() {
        return annotationMarks;
    }



    /**
     * @param pAnnotationMarks
     *            the annotationMarks to set
     */
    public void setAnnotationMarks(List<String> pAnnotationMarks) {
        annotationMarks = pAnnotationMarks;
    }
}
