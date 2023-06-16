package de.schmiereck.hexWave;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * see: https://blog.jetbrains.com/idea/2019/11/tutorial-reactive-spring-boot-a-javafx-spring-boot-application/
 */
@SpringBootApplication
public class HexWaveMain {

    public static void main(String[] args) {
        //org.springframework.boot.SpringApplication.run(HexWaveApplication.class, args);
        javafx.application.Application.launch(HexWaveApplication.class, args);
    }
}
