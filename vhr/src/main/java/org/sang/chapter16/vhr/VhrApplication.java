package org.sang.chapter16.vhr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@MapperScan("org.sang.chapter16.vhr.mapper")
//@EnableWebSecurity
//@EnableCaching
public class VhrApplication {

	public static void main(String[] args) {
		SpringApplication.run(VhrApplication.class, args);
	}

}
