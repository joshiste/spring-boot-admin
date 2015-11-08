package de.codecentric.boot.admin.cli.command;

import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;

import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;
import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.util.StringUtils;

import de.codecentric.boot.admin.cli.command.options.RestOptionHandler;
import joptsimple.OptionSet;

public class LoggerCommand extends OptionParsingCommand {
	private static final String LOGBACK_MBEAN = "ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator";

	public LoggerCommand() {
		super("logger", "Query and manipulate the loggers of specified instances (Logback only)",
				new LoggerOptionHandler());
	}

	@Override
	public String getUsageHelp() {
		return "[options] [set <logger> <level> | <logger> [<logger>] --] <instance-id> [<instance-id>...]";
	}

	private static class LoggerOptionHandler extends RestOptionHandler {
		@Override
		protected ExitStatus run(OptionSet options) throws Exception {
			if (!options.nonOptionArguments().isEmpty()
					&& "set".equals(options.nonOptionArguments().get(0))) {
				return set(options);
			}
			return query(options);
		}

		private ExitStatus set(OptionSet options) {
			@SuppressWarnings("unchecked")
			final List<String> args = (List<String>) options.nonOptionArguments();
			if (args.size() < 4) {
				throw new CommandException(
						"One loggername, a level and at least one instance-id must be specified!");
			}

			List<String> instanceIds = args.subList(3, args.size());
			return forEach(instanceIds, options, new CommandFunction<String>() {
				@Override
				public ExitStatus apply(String instanceId, OptionSet options) throws Exception {
					J4pExecRequest request = new J4pExecRequest(LOGBACK_MBEAN, "setLoggerLevel",
							args.get(1), args.get(2));
					createJ4pClient(instanceId, options).execute(request);
					return ExitStatus.OK;
				}
			});
		}

		@SuppressWarnings("unchecked")
		private ExitStatus query(OptionSet options) {
			List<String> args = (List<String>) options.nonOptionArguments();
			int beginInstances = findInstancesBegin(args);
			final List<String> loggers = args.subList(0, beginInstances);
			List<String> instanceIds = args.subList(beginInstances, args.size());

			return forEach(instanceIds, options, new CommandFunction<String>() {
				@Override
				public ExitStatus apply(String instanceId, OptionSet options) throws Exception {
					List<String> reqLoggers = !loggers.isEmpty() ? loggers
							: getAllLoggers(instanceId, options);

					List<J4pExecRequest> requests = new ArrayList<J4pExecRequest>(loggers.size());
					for (String logger : reqLoggers) {
						requests.add(new J4pExecRequest(LOGBACK_MBEAN, "getLoggerEffectiveLevel",
								logger));
					}

					for (J4pResponse<J4pExecRequest> response : createJ4pClient(instanceId, options)
							.execute(requests)) {
						if (!StringUtils.isEmpty(response.getValue())) {
							System.out.format("%s=%s%n",
									response.getRequest().getArguments().get(0),
									response.getValue());
						}
					}
					return ExitStatus.OK;
				}

				private List<String> getAllLoggers(String instanceId, OptionSet options)
						throws MalformedObjectNameException, J4pException {
					J4pReadRequest request = new J4pReadRequest(LOGBACK_MBEAN, "LoggerList");
					J4pResponse<J4pReadRequest> response = createJ4pClient(instanceId, options)
							.execute(request);
					return response.getValue();
				}
			});
		}

		private int findInstancesBegin(List<String> list) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).matches("^[0-9A-Fa-f]+$")) {
					return i;
				}
			}
			return 0;
		}
	};
}
