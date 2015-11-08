package de.codecentric.boot.admin.cli.command;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.codecentric.boot.admin.cli.command.options.OptionHandler;

/**
 * Copy from the spring boot
 */
public class OptionParsingCommandTests {

	@Test
	public void optionHelp() {
		OptionHandler handler = new OptionHandler();
		handler.option("bar", "Bar");
		OptionParsingCommand command = new OptionParsingCommand("foo", "Foo", handler) {
		};
		assertThat(command.getHelp(), containsString("--bar"));
	}

}