package my.mbean.view;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NonRecursivePropertyPlaceholderHelper extends PropertyPlaceholderHelper {
    public NonRecursivePropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
        super(placeholderPrefix, placeholderSuffix);
    }


    @Override
    protected String parseStringValue(String strVal, PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {
        return super.parseStringValue(strVal,
                new NonRecursivePlaceholderResolver(placeholderResolver),
                visitedPlaceholders);
    }

    private static class NonRecursivePlaceholderResolver implements PlaceholderResolver {

        private final PlaceholderResolver resolver;


        NonRecursivePlaceholderResolver(PlaceholderResolver resolver) {
            this.resolver = resolver;
        }


        @Override
        public String resolvePlaceholder(String placeholderName) {
            if (this.resolver instanceof NonRecursivePlaceholderResolver) {
                return null;
            }
            return this.resolver.resolvePlaceholder(placeholderName);
        }
    }

    /**
     * {@link PlaceholderResolver} to collect placeholder expressions.
     */
    public static class ExpressionCollector implements PlaceholderResolver {
        private final SpelExpressionParser parser      = new SpelExpressionParser();
        private final Map<String, Expression> expressions = new HashMap<String, Expression>();



        @Override
        public String resolvePlaceholder(String name) {
            this.expressions.put(name, this.parser.parseExpression(name));
            return null;
        }



        public Map<String, Expression> getExpressions() {
            return Collections.unmodifiableMap(this.expressions);
        }

    }

    /**
     * SpEL based {@link PlaceholderResolver}.
     */
    private static class ExpressionResolver implements PlaceholderResolver {
        private final Map<String, Expression> expressions;
        private final EvaluationContext context;



        ExpressionResolver(Map<String, Expression> expressions, Map<String, ?> map) {
            this.expressions = expressions;
            this.context = getContext(map);
        }



        private EvaluationContext getContext(Map<String, ?> map) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.addPropertyAccessor(new MapAccessor());
            context.setRootObject(map);
            return context;
        }



        @Override
        public String resolvePlaceholder(String placeholderName) {
            Expression expression = this.expressions.get(placeholderName);
            return escape(expression == null ? null : expression.getValue(this.context));
        }



        private String escape(Object value) {
//            return HtmlUtils.htmlEscape(value == null ? null : value.toString());
            return (value == null ? "" : value.toString());
        }

    }
}
