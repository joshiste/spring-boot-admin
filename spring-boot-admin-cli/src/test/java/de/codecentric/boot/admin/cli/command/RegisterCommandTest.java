package de.codecentric.boot.admin.cli.command;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.boot.test.OutputCapture;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class RegisterCommandTest {
	@Rule
	public WireMockRule wireMock = new WireMockRule(wireMockConfig().dynamicPort());

	@Rule
	public OutputCapture output = new OutputCapture();

	private RegisterCommand cmd = new RegisterCommand();

	@Test
	public void run() throws Exception {
		stubFor(post(urlEqualTo("/api/applications"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json")
						.withBodyFile("register/response.json").withStatus(HttpStatus.SC_OK)));

		ExitStatus rc = cmd.run("testname", "--health-url", "/health", "--service-url",
				"http://service", "--url", "http://localhost:" + wireMock.port() + "/api");

		assertThat(rc, is(ExitStatus.OK));
		output.expect(equalTo("cafebabe\n"));
		verify(postRequestedFor(urlEqualTo("/api/applications")).withRequestBody(equalToJson(
				"{\"name\":\"testname\", \"managementUrl\":\"http://service\", \"healthUrl\":\"http://service/health\", \"serviceUrl\":\"http://service\"}")));
	}

	@Test(expected = CommandException.class)
	public void run_error_name() throws Exception {
		cmd.run();
	}

	@Test(expected = CommandException.class)
	public void run_error_missingurl() throws Exception {
		cmd.run("testname");
	}
}
