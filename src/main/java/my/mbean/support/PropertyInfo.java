package my.mbean.support;

import java.util.List;
import java.util.Objects;

/**
 * common property info.
 *
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
     * property has some tag, e.g: final.
     */
    private List<String>   tags;
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


    public String getName() {
        return name;
    }

    public PropertyInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public PropertyInfo setType(String type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PropertyInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public PropertyInfo setValue(Object value) {
        this.value = value;
        return this;
    }

    public List<String> getAnnotationMarks() {
        return annotationMarks;
    }

    public PropertyInfo setAnnotationMarks(List<String> annotationMarks) {
        this.annotationMarks = annotationMarks;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public PropertyInfo setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public PropertySource getSource() {
        return source;
    }

    public PropertyInfo setSource(PropertySource source) {
        this.source = source;
        return this;
    }
}
