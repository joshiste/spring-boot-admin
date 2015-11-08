package de.codecentric.boot.admin.cli.command;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;

import de.codecentric.boot.admin.cli.command.options.RestOptionHandler;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class RegisterCommand extends OptionParsingCommand {

	public RegisterCommand() {
		super("add", "Register (spring boot) applications manually", new RegisterOptionHandler());
	}

	@Override
	public String getUsageHelp() {
		return "<name> [options]";
	}

	private static class RegisterOptionHandler extends RestOptionHandler {
		private OptionSpec<String> healthUrlOption;
		private OptionSpec<String> managementUrlOption;
		private OptionSpec<String> serviceUrlOption;

		@Override
		protected void doOptions() {
			healthUrlOption = option("health-url",
					"health-endpoint of the application you wish to register defaults to '<mgmt-url>/health'. "
							+ " If starting with '/' it will be appended to the mgmt-url")
									.withRequiredArg().defaultsTo("/health");
			managementUrlOption = option("mgmt-url",
					"managment-endpoint of the application you wish to register defaults to '<service-url>'. "
							+ "If starting with '/' it will be appended to the svc-url. "
							+ "If not specified and no <service-url> is given, no acutator informations will be available.")
									.withRequiredArg().defaultsTo("/");
			serviceUrlOption = option("service-url",
					"service-endpoint of the application you wish to register defaults.")
							.withRequiredArg();
		}

		@Override
		@SuppressWarnings("unchecked")
		protected ExitStatus run(OptionSet options) throws Exception {
			Map<String, Object> response = createRestTemplate(options)
					.postForObject("/applications", createApplication(options), Map.class);
			System.out.println(response.get("id"));
			return ExitStatus.OK;
		}

		private Map<String, Object> createApplication(OptionSet options) {
			if (options.nonOptionArguments().isEmpty()) {
				throw new CommandException("The name must be specified!");
			}

			if (!options.has(healthUrlOption) && !options.has(managementUrlOption)
					&& !options.has(serviceUrlOption)) {
				throw new CommandException(
						"At least one of health-, managment- or service-url must be specified!");
			}

			String serviceUrl = null;
			String managementUrl = null;
			String healthUrl = null;
			if (options.hasArgument(serviceUrlOption)) {
				serviceUrl = serviceUrlOption.value(options);
			}

			if (options.hasArgument(managementUrlOption)) {
				managementUrl = managementUrlOption.value(options);
				if (managementUrl.startsWith("/") && serviceUrl != null) {
					managementUrl = serviceUrl + managementUrl;
				}
			} else if (serviceUrl != null) {
				managementUrl = serviceUrl;
			}

			if (options.hasArgument(healthUrlOption)) {
				healthUrl = healthUrlOption.value(options);
				if (healthUrl.startsWith("/") && managementUrl != null) {
					healthUrl = managementUrl + healthUrl;
				}
			} else if (managementUrl != null) {
				healthUrl = managementUrl + "/health";
			}

			Map<String, Object> application = new HashMap<String, Object>();
			application.put("name", options.nonOptionArguments().get(0));
			application.put("healthUrl", healthUrl);
			if (managementUrl != null) {
				application.put("managementUrl", managementUrl);
			}
			if (serviceUrl != null) {
				application.put("serviceUrl", serviceUrl);
			}
			return application;
		}
	};
}
