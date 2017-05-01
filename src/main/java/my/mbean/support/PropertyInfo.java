package my.mbean.support;

import java.util.List;
import java.util.Objects;

/**
 * common property info.
 * @author hubert
 */
public class PropertyInfo {
    /**
     * property name.
     */
    private String         name;
    /**
     * property type.
     */
    private String         type;
    /**
     * property url;
     */
    private String         url;
    /**
     * property value info.
     */
    private Object         value;
    /**
     * if property exist some annotation need to show.
     */
    private List<String>   annotationMarks;
    /**
     * property source.
     */
    private PropertySource source;



    @Override
    public boolean equals(Object pObj) {
        if (pObj == null)
            return false;
        if (pObj == this)
            return true;
        if (pObj instanceof PropertyInfo) {
            PropertyInfo other = (PropertyInfo) pObj;
            if (Objects.equals(other.name, this.name) && Objects.equals(other.type, this.type))
                return true;
        } else
            return false;
        return super.equals(pObj);
    }



    /**
     * @return the name
     */
    public String getName() {
        return name;
    }



    /**
     * @return the type
     */
    public String getType() {
        return type;
    }



    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }



    /**
     * @param pName
     *            the name to set
     */
    public void setName(String pName) {
        name = pName;
    }



    /**
     * @param pType
     *            the type to set
     */
    public void setType(String pType) {
        type = pType;
    }



    /**
     * @param pUrl
     *            the url to set
     */
    public void setUrl(String pUrl) {
        url = pUrl;
    }



    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }



    /**
     * @param pValue
     *            the value to set
     */
    public void setValue(Object pValue) {
        value = pValue;
    }



    /**
     * @return the source
     */
    public PropertySource getSource() {
        return source;
    }



    /**
     * @param pSource
     *            the source to set
     */
    public void setSource(PropertySource pSource) {
        source = pSource;
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
