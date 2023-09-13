package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootSecurityDemoApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
		logger.info("SpringBootSecurityDemoApplication is running...");
	}

}
