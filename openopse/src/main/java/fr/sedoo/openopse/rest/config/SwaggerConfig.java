package fr.sedoo.openopse.rest.config;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	@Profile("!" + Profiles.PRODUCTION_PROFILE)
	public Docket testApi(ServletContext servletContext) {
		return new Docket(DocumentationType.SWAGGER_2).pathProvider(new RelativePathProvider(servletContext) {
			@Override
			public String getApplicationBasePath() {
				return "/";
			}
		}).select().apis(RequestHandlerSelectors.basePackage("fr.sedoo")).paths(PathSelectors.any()).build();
	}

	@Bean
	@Profile(Profiles.PRODUCTION_PROFILE)
	public Docket prodApi(ServletContext servletContext) {
		return new Docket(DocumentationType.SWAGGER_2).pathMapping("/opseprod")
				.pathProvider(new RelativePathProvider(servletContext) {
					@Override
					public String getApplicationBasePath() {
						return "/";
					}
				}).select().apis(RequestHandlerSelectors.basePackage("fr.sedoo")).paths(PathSelectors.any()).build();
	}
}
