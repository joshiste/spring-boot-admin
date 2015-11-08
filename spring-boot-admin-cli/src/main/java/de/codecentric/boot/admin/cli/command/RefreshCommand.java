package de.codecentric.boot.admin.cli.command;

import static java.util.Collections.singletonMap;

import java.util.List;

import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.web.client.RestTemplate;

import de.codecentric.boot.admin.cli.command.options.RestOptionHandler;
import joptsimple.OptionSet;

public class RefreshCommand extends OptionParsingCommand {

	public RefreshCommand() {
		super("refresh", "Refresh the environment of specified instances",
				new RefreshOptionHandler());
	}

	@Override
	public String getUsageHelp() {
		return "[options] <instance-id> [<instance-id>...]";
	}

	private static class RefreshOptionHandler extends RestOptionHandler {
		@Override
		protected ExitStatus run(final OptionSet options) throws Exception {
			@SuppressWarnings("unchecked")
			List<String> instanceIds = (List<String>) options.nonOptionArguments();
			if (instanceIds.isEmpty()) {
				throw new CommandException("At least one instance-id must be specified!");
			}

			final RestTemplate restTemplate = createRestTemplate(options);
			return forEach(instanceIds, options, new CommandFunction<String>() {
				@Override
				public ExitStatus apply(String instanceId, OptionSet options) {
					restTemplate.postForEntity("/applications/{instanceId}/refresh", null,
							Void.class, singletonMap("instanceId", instanceId));
					return ExitStatus.OK;
				}
			});
		}
	};
}
