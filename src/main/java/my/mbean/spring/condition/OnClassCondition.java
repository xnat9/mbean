package my.mbean.spring.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xnat on 17/5/14.
 */
public class OnClassCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        List<String> onClasses = getCandidates(metadata, ConditionalOnClass.class);
        for (String cls : onClasses) {
            boolean present = ClassUtils.isPresent(cls, context.getClassLoader());
            if (!present) return false;
        }
        return true;
    }

    private List<String> getCandidates(AnnotatedTypeMetadata metadata,
                                       Class<?> annotationType) {
        MultiValueMap<String, Object> attributes = metadata
                .getAllAnnotationAttributes(annotationType.getName(), true);
        List<String> candidates = new ArrayList<String>();
        if (attributes == null) {
            return Collections.emptyList();
        }
        addAll(candidates, attributes.get("value"));
        addAll(candidates, attributes.get("name"));
        return candidates;
    }

    private void addAll(List<String> list, List<Object> itemsToAdd) {
        if (itemsToAdd != null) {
            for (Object item : itemsToAdd) {
                Collections.addAll(list, (String[]) item);
            }
        }
    }
}
