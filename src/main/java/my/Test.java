package my;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import java.util.concurrent.TimeUnit;

/**
 * Created by xnat on 17/5/6.
 */
public class Test {
    public static void main(String[] args) {
        String pattern = "/static/**";
        AntPathMatcher pathMatcher = new AntPathMatcher();
//        UrlPathHelper urlPathHelper = new UrlPathHelper();
        boolean flag = pathMatcher.match(pattern, "/static/rescoure/js/vue.js");
        String pathWithinHandlerMapping = pathMatcher.extractPathWithinPattern(pattern, "/static/rescoure/js/vue.js");
        System.out.println(flag);
        System.out.println(pathWithinHandlerMapping);
        System.out.println(TimeUnit.MINUTES.toSeconds(1));
    }
}
