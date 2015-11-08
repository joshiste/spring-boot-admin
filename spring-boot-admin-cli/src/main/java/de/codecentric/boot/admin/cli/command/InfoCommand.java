package de.codecentric.boot.admin.cli.command;

import de.codecentric.boot.admin.cli.command.options.GetRequestOptionHandler;

public class InfoCommand extends OptionParsingCommand {

	public InfoCommand() {
		super("info", "Query /info-endpoint of the given instances",
				new GetRequestOptionHandler("/applications/{instanceId}/info"));
	}

	@Override
	public String getUsageHelp() {
		return "[options] <instance-id> [<instance-id>...]";
	}
}
