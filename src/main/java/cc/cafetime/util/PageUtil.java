package cc.cafetime.util;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;

/**
 * Created by steven on 2017/2/19.
 */
public class PageUtil {

    public static String readHtml(String name) throws IOException {
        URL url = PageUtil.class.getResource("/templates/" + name + ".html");
        BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

}
