package my.mbean.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.MediaType;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.web.servlet.View;

public class SpelView implements View {

    private final NonRecursivePropertyPlaceholderHelper helper;

    private final String                                template;

    private volatile Map<String, Expression>            expressions;



    public SpelView(String template) {
        this.helper = new NonRecursivePropertyPlaceholderHelper("${", "}");
        this.template = template;
    }



    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }



    @Override
    public void render(Map<String, ?> model, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        if (response.getContentType() == null) {
            response.setContentType(getContentType());
        }
        Map<String, Object> map = new HashMap<String, Object>(model);
//        map.put("path", request.getContextPath());
        PlaceholderResolver resolver = new ExpressionResolver(getExpressions(), map);
        String result = this.helper.replacePlaceholders(this.template, resolver);
        response.getWriter().append(result);
    }



    private Map<String, Expression> getExpressions() {
        if (this.expressions == null) {
            synchronized (this) {
                ExpressionCollector expressionCollector = new ExpressionCollector();
                this.helper.replacePlaceholders(this.template, expressionCollector);
                this.expressions = expressionCollector.getExpressions();
            }
        }
        return this.expressions;
    }

    /**
     * {@link PlaceholderResolver} to collect placeholder expressions.
     */
    private static class ExpressionCollector implements PlaceholderResolver {
        private final SpelExpressionParser    parser      = new SpelExpressionParser();
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
        private final EvaluationContext       context;



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
