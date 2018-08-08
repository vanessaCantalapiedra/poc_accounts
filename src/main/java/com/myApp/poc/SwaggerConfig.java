package com.myApp.poc;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("!test")
public class SwaggerConfig {

	@Value("${service.context}")
	private String serviceContext;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).paths(PathSelectors.any())
				.build().genericModelSubstitutes(Optional.class).produces(Sets.newHashSet("application/json"))
				.consumes(Sets.newHashSet("application/json")).protocols(Sets.newHashSet("https")).apiInfo(apiInfo())
				.useDefaultResponseMessages(false);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Accounts Service")
				.description("Data source about accounts and provider of transactions").version(getVersion()).build();
	}

	private String getVersion() {
		try {
			String[] allVersionSegments = getClass().getPackage().getImplementationVersion().split("\\.");
			String[] majorMinorersionSegments = Arrays.copyOfRange(allVersionSegments, 0, 2);
			return String.join(".", majorMinorersionSegments);
		} catch (Exception e) {
			return null;
		}
	}
}
