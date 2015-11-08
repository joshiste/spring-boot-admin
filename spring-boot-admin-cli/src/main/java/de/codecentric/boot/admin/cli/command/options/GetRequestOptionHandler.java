package de.codecentric.boot.admin.cli.command.options;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.cli.command.CommandException;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.web.client.RestTemplate;

import joptsimple.OptionSet;

public class GetRequestOptionHandler extends PrintingOptionHandler {
	private final String url;

	public GetRequestOptionHandler(String url) {
		this.url = url;
	}

	@Override
	protected ExitStatus run(final OptionSet options) throws Exception {
		if (options.nonOptionArguments().isEmpty()) {
			throw new CommandException("At least one instance-id must be specified!");
		}

		@SuppressWarnings("unchecked")
		List<String> instanceIds = (List<String>) options.nonOptionArguments();
		final RestTemplate restTemplate = createRestTemplate(options);
		return forEach(instanceIds, options, new CommandFunction<String>() {
			@Override
			public ExitStatus apply(String instanceId, OptionSet options) {
				Map<String, Object> urlVariables = new HashMap<String, Object>();
				urlVariables.put("instanceId", instanceId);
				getUrlVariables(urlVariables, options);
				Object o = restTemplate.getForObject(url, Object.class, urlVariables);
				print(o, options);
				System.out.println();
				return ExitStatus.OK;
			}

		});
	}

	protected void getUrlVariables(Map<String, Object> urlVariables, OptionSet options) {
	}
}