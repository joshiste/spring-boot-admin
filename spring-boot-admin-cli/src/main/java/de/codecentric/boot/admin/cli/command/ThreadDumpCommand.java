package de.codecentric.boot.admin.cli.command;

import de.codecentric.boot.admin.cli.command.options.GetRequestOptionHandler;

public class ThreadDumpCommand extends OptionParsingCommand {

	public ThreadDumpCommand() {
		super("dump", "Dump all threads of the given instances",
				new GetRequestOptionHandler("/applications/{instanceId}/dump"));
	}

	@Override
	public String getUsageHelp() {
		return "[options] <instance-id> [<instance-id>...]";
	}
}
