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

public class ListCommandTest {
	@Rule
	public WireMockRule wireMock = new WireMockRule(wireMockConfig().dynamicPort());

	@Rule
	public OutputCapture output = new OutputCapture();

	private ListCommand cmd = new ListCommand();

	@Test
	public void run() throws Exception {
		stubFor(get(urlMatching("/api/applications"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json")
						.withBodyFile("list/response.json").withStatus(HttpStatus.SC_OK)));

		ExitStatus rc = cmd.run("--url", "http://localhost:" + wireMock.port() + "/api");

		assertThat(rc, is(ExitStatus.OK));
		output.expect(is("INSTANCE-ID    NAME                             STATUS    \n"
				+ "167a80f4       test                             [31mOFFLINE[m\n"
				+ "b1fd46f8       spring-boot-admin-sample         [32mUP[m\n"));
	}

	@Test
	public void run_quiet() throws Exception {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json")
						.withBodyFile("list/response.json").withStatus(HttpStatus.SC_OK)));

		ExitStatus rc = cmd.run("-q", "--url", "http://localhost:" + wireMock.port() + "/api");

		assertThat(rc, is(ExitStatus.OK));
		output.expect(is("167a80f4\nb1fd46f8\n"));
	}

	@Test
	public void run_format() throws Exception {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json")
						.withBodyFile("list/response.json").withStatus(HttpStatus.SC_OK)));

		ExitStatus rc = cmd.run("--f", "{{id}} {{statusInfo.status}}\n", "--url",
				"http://localhost:" + wireMock.port() + "/api");

		assertThat(rc, is(ExitStatus.OK));
		output.expect(is("167a80f4 OFFLINE\nb1fd46f8 UP\n\n"));
	}
}
