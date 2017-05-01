package my.mbean.view;

import java.util.Set;

import org.springframework.util.PropertyPlaceholderHelper;

public class NonRecursivePropertyPlaceholderHelper extends PropertyPlaceholderHelper {
    NonRecursivePropertyPlaceholderHelper(String placeholderPrefix,
        String placeholderSuffix) {
        super(placeholderPrefix, placeholderSuffix);
    }



    @Override
    protected String parseStringValue(String strVal,
        PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {
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
}
