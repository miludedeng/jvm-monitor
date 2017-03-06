package cc.cafetime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import java.io.IOException;

/**
 * Created by steven on 2017/2/9.
 */
@EnableAutoConfiguration
@Controller
@ComponentScan
public class App {

    public static final String RESPONSE_STATUS_SUCCESS = "success";
    public static final String RESPONSE_STATUS_FAILED = "failed";

    public static final String RESPONSE_STATUS_AUTHCODE_OVERTIME = "authcode_overtime";

    public static void main(String[] args) throws InterruptedException {
        BroadcastReceive.exec();
        SpringApplication.run(App.class, args);
    }
}
