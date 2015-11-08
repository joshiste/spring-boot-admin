package de.codecentric.boot.admin.cli.command;

import java.util.Map;

import de.codecentric.boot.admin.cli.command.options.GetRequestOptionHandler;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class MetricsCommand extends OptionParsingCommand {

	public MetricsCommand() {
		super("metrics", "Query the metrics for given instances", new MetricsOptionHandler());
	}

	@Override
	public String getUsageHelp() {
		return "[options] <instance-id> [<instance-id>...]";
	}

	private static class MetricsOptionHandler extends GetRequestOptionHandler {
		private OptionSpec<String> patternOption;

		public MetricsOptionHandler() {
			super("/applications/{instanceId}/metrics/{pattern}");
		}

		@Override
		protected void doOptions() {
			super.doOptions();
			patternOption = option("pattern",
					"Pattern to filter the results with when querying the metrics")
							.withRequiredArg().defaultsTo("");
		}

		@Override
		protected void getUrlVariables(Map<String, Object> urlVariables, OptionSet options) {
			urlVariables.put("pattern", patternOption.value(options));
		}
	};
}
