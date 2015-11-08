package de.codecentric.boot.admin.cli.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.springframework.boot.cli.command.status.ExitStatus;

import de.codecentric.boot.admin.cli.command.options.PrintingOptionHandler;
import jline.TerminalFactory;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class ListCommand extends OptionParsingCommand {

	public ListCommand() {
		super("ls", "List all registered spring boot applications", new ListOptionHandler());
	}

	private static class ListOptionHandler extends PrintingOptionHandler {
		private OptionSpec<Boolean> quietOption;

		@Override
		protected void doOptions() {
			quietOption = option(Arrays.asList("quiet", "q"), "List only instance ids")
					.withOptionalArg().ofType(Boolean.class).defaultsTo(false);
		}

		private boolean useColors() {
			return TerminalFactory.get().isAnsiSupported();
		}

		private String colored(String s, Color c) {
			return useColors() ? Ansi.ansi().fg(c).a(s).reset().toString() : s;
		}

		@Override
		@SuppressWarnings("unchecked")
		protected ExitStatus run(OptionSet options) throws Exception {
			List<Map<String, Object>> applications = createRestTemplate(options)
					.getForObject("/applications", List.class);
			if (options.has(quietOption)
					&& (!options.hasArgument(quietOption) || quietOption.value(options))) {
				for (Map<String, Object> application : applications) {
					System.out.println(application.get("id"));
				}
			} else if (options.has("pretty") || options.has("format")) {
				print(applications, options);
				System.out.println();
			} else {
				System.out.printf("%-12s   %-30s   %-10s%n", "INSTANCE-ID", "NAME", "STATUS");
				for (Map<String, Object> application : applications) {
					String status = ((Map<String, String>) application.get("statusInfo"))
							.get("status");
					String coloredstatus = colored(status,
							"UP".equals(status) ? Color.GREEN : Color.RED);
					System.out.printf("%-12s   %-30s   %-10s%n", application.get("id"),
							application.get("name"), coloredstatus);
				}
			}
			return ExitStatus.OK;
		}
	};
}
