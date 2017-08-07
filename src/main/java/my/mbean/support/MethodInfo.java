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


    public String getName() {
        return name;
    }

    public MethodInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getReturnType() {
        return returnType;
    }

    public MethodInfo setReturnType(String returnType) {
        this.returnType = returnType;
        return this;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public MethodInfo setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MethodInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public boolean isCallable() {
        return callable;
    }

    public MethodInfo setCallable(boolean callable) {
        this.callable = callable;
        return this;
    }

    public List<String> getAnnotationMarks() {
        return annotationMarks;
    }

    public MethodInfo setAnnotationMarks(List<String> annotationMarks) {
        this.annotationMarks = annotationMarks;
        return this;
    }
}
