package com.myApp.poc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "env:test", "spring.profiles.active: test" })
public abstract class AbstractIntegratedTest {

	@Autowired
	protected WebApplicationContext context;

	protected MockMvc mockMvc;

	/*
	 * Run before each test
	 */

	@Before
	public void setUp() {
	
		// standard spring MVC mock setup

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();

		doSetup();
	}

	public abstract void doSetup();

}