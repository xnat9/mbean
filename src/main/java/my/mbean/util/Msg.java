package my.mbean.util;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * common util for Msg.
 * @author hubert
 */
public class Msg {
    /**
     * match string like "{0.name}".
     */
//    private static final Pattern paramPattern = Pattern.compile("\\{(([0-9]+).([\\w].+))\\}");
    private static final Pattern paramPattern = Pattern.compile("\\{(([0-9]+).([\\w]+))\\}");



    /**
     * format String like "my name: {0.name}" use pArgs.
     * @param pFormat
     *            format String.
     * @param pArgs
     *            arguments
     * @return formatted String.
     */
    public static String format(String pFormat, Object... pArgs) {
        if (Utils.isEmpty(pArgs)) {
            return pFormat;
        }
        Matcher matcher = paramPattern.matcher(pFormat);
        // search string like "{0.name}" in pFormat
        if (!matcher.find()) {
            return MessageFormat.format(pFormat, pArgs);
        }
        StringBuilder sb = new StringBuilder(pFormat);
        matcher = paramPattern.matcher(sb);
        List<Object> args = new ArrayList<>(pArgs.length + 2);
        // "{0.name}" regex resolve:
        // group(1) => {0.name}
        // group(2) => 0
        // group(3) => name
        int count = 0;
        while (matcher.find()) {
            Object indexMappedObject = null;
            try {
                int index = Utils.toInt(matcher.group(2), -1);
                if (index < 0) {
                    continue;
                }
                String propExpression = matcher.group(3);
                indexMappedObject = getProperty(propExpression, pArgs[index]);
            } catch (Exception e) {
                // ignore. set it's mapped value is null.
                System.out.println(e.getMessage());
            }
            args.add(indexMappedObject);
            // change "0.name" to "0".
            sb = new StringBuilder(sb.toString().replaceFirst(matcher.group(1), String.valueOf(count)));
//            sb = sb.replaceFirst(matcher.group(1), String.valueOf(count));
            matcher = paramPattern.matcher(sb);
            count++;
        }
        return MessageFormat.format(sb.toString(), args.toArray());
    }



    protected static Object getProperty(String pPropEl, Object pInst) {
        try {
//            BeanWrapper beanWrapper = new BeanWrapperImpl(pInst);
//            return beanWrapper.getPropertyValue(pPropEl);
            return PropertyUtil.getProperty(pInst, pPropEl);
        } catch (Exception e) {
        }

        return null;
    }
}
