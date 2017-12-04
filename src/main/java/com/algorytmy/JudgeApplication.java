package com.algorytmy;

import com.algorytmy.GUI.Utility.CustomSplash;
import com.algorytmy.GUI.View.DataWindowView;
import com.algorytmy.GUI.View.MapWindowView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class JudgeApplication extends AbstractJavaFxApplicationSupport {

	public static void main(String[] args) {
		launchApp(JudgeApplication.class, DataWindowView.class, new CustomSplash(), args);
		//SpringApplication.run(JudgeApplication.class, args);
	}
}
