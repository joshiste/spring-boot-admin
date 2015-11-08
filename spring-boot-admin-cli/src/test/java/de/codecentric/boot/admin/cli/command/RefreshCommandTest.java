package de.codecentric.boot.admin.cli.command;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class RefreshCommandTest {
	@Rule
	public WireMockRule wireMock = new WireMockRule(wireMockConfig().dynamicPort());

	private RefreshCommand cmd = new RefreshCommand();

	@Test
	public void run() throws Exception {
		stubFor(post(urlMatching("/api/applications/.*/refresh"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		ExitStatus rc = cmd.run("cafebabe", "1337fefe", "--url",
				"http://localhost:" + wireMock.port() + "/api");

		assertThat(rc, is(ExitStatus.OK));
		verify(postRequestedFor(urlEqualTo("/api/applications/cafebabe/refresh")));
		verify(postRequestedFor(urlEqualTo("/api/applications/1337fefe/refresh")));
	}

	@Test(expected = CommandException.class)
	public void run_error() throws Exception {
		cmd.run();
	}
}
