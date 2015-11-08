package de.codecentric.boot.admin.cli.command;

import de.codecentric.boot.admin.cli.command.options.GetRequestOptionHandler;

public class TraceCommand extends OptionParsingCommand {

	public TraceCommand() {
		super("trace", "View all traces of the given instances",
				new GetRequestOptionHandler("/applications/{instanceId}/trace"));
	}

	@Override
	public String getUsageHelp() {
		return "[options] <instance-id> [<instance-id>...]";
	}
}
