package app;

import app.xacml.pdp.My_PDP;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MyApplication {
    public static void main(String[] args){
        SpringApplication.run(MyApplication.class, args);
    }
}