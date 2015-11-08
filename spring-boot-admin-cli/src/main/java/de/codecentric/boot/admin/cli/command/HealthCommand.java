package de.codecentric.boot.admin.cli.command;

import de.codecentric.boot.admin.cli.command.options.GetRequestOptionHandler;

public class HealthCommand extends OptionParsingCommand {

	public HealthCommand() {
		super("health", "Get health-information the given instances",
				new GetRequestOptionHandler("/applications/{instanceId}/health"));
	}

	@Override
	public String getUsageHelp() {
		return "[options] <instance-id> [<instance-id>...]";
	}
}
