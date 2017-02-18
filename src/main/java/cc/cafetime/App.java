package cc.cafetime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

/**
 * Created by steven on 2017/2/9.
 */
@EnableAutoConfiguration
@Controller
@ComponentScan
public class App {

    public static String RESPONSE_STATUS_SUCCESS="success";
    public static String RESPONSE_STATUS_FAILED="failed";


    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(App.class, args);
    }
}
