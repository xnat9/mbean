package my;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xnat on 17/5/6.
 */
public class Test {
    public static void main(String[] args) {
        Pattern paramPattern = Pattern.compile("\\{(([0-9]+).([\\w]+))\\}");
        String s = "trace request: uri: {0.requestURI}";
        Matcher matcher = paramPattern.matcher(s);
        if (matcher.find()) {
            System.out.println("group0: "+ matcher.group(0));
            System.out.println("group1: "+ matcher.group(1));
            System.out.println("group2: "+ matcher.group(2));
            System.out.println("group3: "+ matcher.group(3));
        }
    }
}
