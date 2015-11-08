package de.codecentric.boot.admin.cli.command;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.boot.test.OutputCapture;

public class VersionCommandTest {
	private VersionCommand command = new VersionCommand();

	@Rule
	public OutputCapture output = new OutputCapture();

	@Test
	public void run() {
		output.expect(containsString("Spring Boot Admin CLI v"));
		ExitStatus rc = command.run();
		assertThat(rc, is(ExitStatus.OK));
	}
}
