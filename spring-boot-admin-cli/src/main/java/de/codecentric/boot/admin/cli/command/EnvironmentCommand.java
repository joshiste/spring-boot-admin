package de.codecentric.boot.admin.cli.command;

import static java.util.Collections.singletonMap;

import java.util.List;
import java.util.Map;

import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.codecentric.boot.admin.cli.command.options.GetRequestOptionHandler;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class EnvironmentCommand extends OptionParsingCommand {

	public EnvironmentCommand() {
		super("env", "Query and manipulate the environment of specified instances",
				new EnvironmentOptionHandler());
	}

	@Override
	public String getUsageHelp() {
		return "[options] [set <key>=<value> [<key>=<value>...] | reset] <instance-id> [<instance-id>...]";
	}

	private static class EnvironmentOptionHandler extends GetRequestOptionHandler {
		private OptionSpec<String> patternOption;
		private OptionSpec<?> autoRefreshOption;

		public EnvironmentOptionHandler() {
			super("/applications/{instanceId}/env/{pattern}");
		}

		@Override
		protected void doOptions() {
			super.doOptions();
			patternOption = option("pattern",
					"Pattern to filter the results with when querying the environment")
							.withRequiredArg().defaultsTo("");
			autoRefreshOption = option("auto-refresh",
					"Refresh the @RefreshScope after setting/restting the environment");
		}

		@Override
		protected ExitStatus run(OptionSet options) throws Exception {
			if (!options.nonOptionArguments().isEmpty()) {
				if ("set".equals(options.nonOptionArguments().get(0))) {
					return set(options);
				}
				if ("reset".equals(options.nonOptionArguments().get(0))) {
					return reset(options);
				}
			}
			return super.run(options);
		}

		private ExitStatus set(final OptionSet options) {
			@SuppressWarnings("unchecked")
			List<String> args = (List<String>) options.nonOptionArguments();
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
			int i = 0;
			for (String arg : args.subList(1, args.size())) {
				int pos = arg.indexOf('=');
				if (pos > 0) {
					uriBuilder.queryParam(arg.substring(0, pos),
							arg.substring(pos + 1, arg.length()));
				} else {
					break;
				}
				i++;
			}
			final String query = uriBuilder.build().getQuery();
			List<String> instanceIds = args.subList(i + 1, args.size());
			if (query == null || instanceIds.isEmpty()) {
				throw new CommandException(
						"At least one key-value pair and at least one instance-id must be specified!");
			}

			final RestTemplate restTemplate = createRestTemplate(options);
			return forEach(instanceIds, options, new CommandFunction<String>() {
				@Override
				public ExitStatus apply(String instanceId, OptionSet options) {
					restTemplate.postForEntity("/applications{instanceId}/env?" + query, null,
							Void.class, singletonMap("instanceId", instanceId));
					if (options.has(autoRefreshOption)) {
						restTemplate.postForEntity("/applications{instanceId}/refresh", null,
								Void.class, singletonMap("instanceId", instanceId));
					}
					return ExitStatus.OK;
				}
			});
		}

		private ExitStatus reset(final OptionSet options) {
			@SuppressWarnings("unchecked")
			List<String> args = (List<String>) options.nonOptionArguments();
			if (args.size() < 2) {
				throw new CommandException("At least one instance-id must be specified!");
			}

			List<String> instanceIds = args.subList(1, args.size());
			final RestTemplate restTemplate = createRestTemplate(options);
			return forEach(instanceIds, options, new CommandFunction<String>() {
				@Override
				public ExitStatus apply(String instanceId, OptionSet options) {
					restTemplate.postForObject("/applications/{instanceId}/env/reset", null,
							Void.class, singletonMap("instanceId", instanceId));
					if (options.has(autoRefreshOption)) {
						restTemplate.postForEntity("/applications/{instanceId}/refresh", null,
								Void.class, singletonMap("instanceId", instanceId));
					}
					return ExitStatus.OK;
				}
			});
		}

		@Override
		protected void getUrlVariables(Map<String, Object> urlVariables, OptionSet options) {
			urlVariables.put("pattern", patternOption.value(options));
		}
	};
}
