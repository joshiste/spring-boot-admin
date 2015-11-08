package de.codecentric.boot.admin.cli.command.options;

import java.util.Arrays;

import org.jolokia.client.BasicAuthenticator;
import org.jolokia.client.J4pClient;
import org.jolokia.client.J4pClientBuilder;
import org.jolokia.client.J4pClientBuilderFactory;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

import de.codecentric.boot.admin.cli.web.BasicAuthHttpRequestInterceptor;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;

public abstract class RestOptionHandler extends OptionHandler {
	private OptionSpec<String> urlOption;
	private OptionSpec<String> userOption;
	private OptionSpec<String> passwordOption;

	@Override
	protected void options() {
		urlOption = option("url", "Specifiy the spring boot admin server to query")
				.withRequiredArg().defaultsTo("http://localhost:8080/api/");

		OptionSpecBuilder user = option("user",
				"Specifiy the basic-auth user to query the admin server");

		OptionSpecBuilder password = option("password",
				"Specifiy the basic-auth password to query the admin server");

		userOption = user.requiredIf(password).withRequiredArg();
		passwordOption = password.requiredIf(user).withRequiredArg();

		doOptions();
	}

	protected void doOptions() {
	}

	protected final <T> ExitStatus forEach(Iterable<T> iterable, OptionSet options,
			CommandFunction<T> function) {
		ExitStatus maxExitStatus = ExitStatus.OK;
		for (T i : iterable) {
			try {
				ExitStatus exitStatus = function.apply(i, options);
				if (exitStatus != null && maxExitStatus.getCode() < exitStatus.getCode()) {
					maxExitStatus = exitStatus;
				}
			} catch (Exception ex) {
				System.err.println(i + ": " + ex.getMessage());
				maxExitStatus = ExitStatus.ERROR;
			}
		}
		return maxExitStatus;
	}

	protected interface CommandFunction<I> {
		ExitStatus apply(I input, OptionSet options) throws Exception;
	}

	protected final RestTemplate createRestTemplate(OptionSet options) {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter(
				Jackson2ObjectMapperBuilder.json().build()));

		DefaultUriTemplateHandler uriHandler = new DefaultUriTemplateHandler();
		uriHandler.setBaseUrl(urlOption.value(options));
		template.setUriTemplateHandler(uriHandler);

		if (options.has(userOption)) {
			template.setInterceptors(Arrays.<ClientHttpRequestInterceptor> asList(
					new BasicAuthHttpRequestInterceptor(userOption.value(options),
							passwordOption.value(options))));
		}
		return template;
	}

	protected final J4pClient createJ4pClient(String instanceId, OptionSet options) {
		J4pClientBuilder builder = J4pClientBuilderFactory
				.url(urlOption.value(options) + "/applications/" + instanceId + "/jolokia");
		if (options.has(userOption)) {
			builder.user(userOption.value(options)).password(passwordOption.value(options))
					.authenticator(new BasicAuthenticator().preemptive());
		}
		return builder.build();
	}

}
