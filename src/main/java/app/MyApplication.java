package app;

import app.xacml.pdp.My_PDP;

import java.io.IOException;

//@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) throws IOException {
//        My_PDP p = new My_PDP();
//        p.nihao();
        //SpringApplication.run(MyApplication.class, args);
        My_PDP p = new My_PDP();
        p.hello();
    }
}