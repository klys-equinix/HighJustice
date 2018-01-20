package com.algorytmy;

import com.algorytmy.GUI.Utility.CustomSplash;
import com.algorytmy.GUI.View.DataWindowView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class JudgeApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launchApp(JudgeApplication.class, DataWindowView.class, new CustomSplash(), args);
        //SpringApplication.run(JudgeApplication.class, args);
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Judge-");
        executor.initialize();
        return executor;
    }
}
