package com.store.example;

import com.store.example.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class ExampleOfWebStoreBackendApiRestApplication  implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(ExampleOfWebStoreBackendApiRestApplication.class);

	@Autowired
	private UserServiceImp userService;

	public static void main(String[] args) {
		SpringApplication.run(ExampleOfWebStoreBackendApiRestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//List<Authorities> authorities = new ArrayList<>();

		//logger.info("Esta corriendo");

		//Roles.ROLE_USER.getAuthorities().stream().forEach(item->logger.info(item.name()));

		//logger.info("Role----->"+Roles.ROLE_USER.name().substring(5));

		//userService.register("adrian","chacon","adrian.chacon22@hotmail.com","$chancho2012$");


	}
}
