package cc.cafetime.util;

import java.util.Formatter;

/**
 * Created by liujing on 2017/2/11.
 */
public class CommonUtil {

    public static String getEntryPointClass(String name) {
        if (name.indexOf(' ') > 0) {
            name = name.substring(0, name.indexOf(' '));
        }
        return name;
    }

    public static String toHHMM(long millis) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter
                .format("%2d:%2dm", millis / 1000 / 3600,
                        (millis / 1000 / 60) % 60);
        return sb.toString();
    }

    public static String toMB(long bytes) {
        if (bytes < 0) {
            return "n/a";
        }
        if (bytes > 1024 * 1024 * 1024) {
            return "" + (bytes / 1024 / 1024 / 1024) + "G";
        } else if (bytes > 1024 * 1024) {
            return "" + (bytes / 1024 / 1024) + "M";
        } else if (bytes > 1024) {
            return "" + (bytes / 1024) + "K";
        } else {
            return "" + bytes + "B";
        }

    }
}
