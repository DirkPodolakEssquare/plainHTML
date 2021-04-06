package de.essquare.driftbottle;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
//		LOG.info("=======================================================");
//		Map<String, String> getenv = System.getenv();
//		getenv.forEach((key, value) -> LOG.info("{} :: {}", key, value));
//		LOG.info("=======================================================");

		SpringApplication.run(Application.class, args);
	}

//	@Bean
//	public CommandLineRunner CommandLineRunnerBean() {
//		LOG.info("-------------------------------------------------------");
//
//		Map<String, String> getenv = System.getenv();
//		getenv.forEach((key, value) -> LOG.info("{} :: {}", key, value));
//
//		System.out.println("-------------------------------------------------------");
//
//		Properties properties = System.getProperties();
//		properties.forEach((o, o2) -> LOG.info("{} :: {}", o, o2));
//
//		return (args) -> {
//			LOG.info("In CommandLineRunnerImpl ");
//
//			for (String arg : args) {
//				LOG.info(arg);
//			}
//		};
//	}
}