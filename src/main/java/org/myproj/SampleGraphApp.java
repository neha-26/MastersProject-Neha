package org.myproj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Michael Hunger
 * @author Mark Angrish
 */
@SpringBootApplication
public class SampleGraphApp {

    public static void main(String[] args) {
        SpringApplication.run(SampleGraphApp.class, args);
    }
}
