package de.codecentric.boot.admin.cli.command;

import static java.util.Collections.singletonMap;

import java.util.List;

import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.web.client.RestTemplate;

import de.codecentric.boot.admin.cli.command.options.RestOptionHandler;
import joptsimple.OptionSet;

public class LogfileCommand extends OptionParsingCommand {

	public LogfileCommand() {
		super("logfile", "Get the logfile the given instances", new LogfileOptionHandler());
	}

	@Override
	public String getUsageHelp() {
		return "[options] <instance-id> [<instance-id>...]";
	}

	private static class LogfileOptionHandler extends RestOptionHandler {
		@Override
		protected ExitStatus run(OptionSet options) throws Exception {
			if (options.nonOptionArguments().isEmpty()) {
				throw new CommandException("At least one instance-id must be specified!");
			}
			@SuppressWarnings("unchecked")
			List<String> instanceIds = (List<String>) options.nonOptionArguments();
			final RestTemplate restTemplate = createRestTemplate(options);
			return forEach(instanceIds, options, new CommandFunction<String>() {
				@Override
				public ExitStatus apply(String instanceId, OptionSet options) {
					String o = restTemplate.getForObject("/applications/{instanceId}/logfile",
							String.class, singletonMap("instanceId", instanceId));
					System.out.println(o);
					return ExitStatus.OK;
				}
			});
		}
	}
}
