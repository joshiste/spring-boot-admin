package de.codecentric.boot.admin.cli.command;

import java.util.Collection;

import org.springframework.boot.cli.command.AbstractCommand;
import org.springframework.boot.cli.command.options.OptionHelp;
import org.springframework.boot.cli.command.status.ExitStatus;

import de.codecentric.boot.admin.cli.command.options.OptionHandler;

/**
 * Copy from {@link org.springframework.boot.cli.command.OptionParsingCommand}.
 */
public abstract class OptionParsingCommand extends AbstractCommand {

	private final OptionHandler handler;

	protected OptionParsingCommand(String name, String description, OptionHandler handler) {
		super(name, description);
		this.handler = handler;
	}

	@Override
	public String getHelp() {
		return this.handler.getHelp();
	}

	@Override
	public Collection<OptionHelp> getOptionsHelp() {
		return this.handler.getOptionsHelp();
	}

	@Override
	public final ExitStatus run(String... args) throws Exception {
		return this.handler.run(args);
	}

	protected OptionHandler getHandler() {
		return this.handler;
	}

}