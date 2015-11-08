package de.codecentric.boot.admin.cli.command;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.boot.test.OutputCapture;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class TraceCommandTest {
	@Rule
	public WireMockRule wireMock = new WireMockRule(wireMockConfig().dynamicPort());

	@Rule
	public OutputCapture output = new OutputCapture();

	private TraceCommand cmd = new TraceCommand();

	@Test
	public void run() throws Exception {
		stubFor(get(urlMatching("/api/applications/.*/trace")).willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody("[{\"name\":\"foo\",\"value\":1000}]").withStatus(HttpStatus.SC_OK)));

		ExitStatus rc = cmd.run("cafebabe", "--url",
				"http://localhost:" + wireMock.port() + "/api");

		assertThat(rc, is(ExitStatus.OK));
		output.expect(is("[{\"name\":\"foo\",\"value\":1000}]\n"));
	}
}
