package de.codecentric.boot.admin.cli.command.options;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.springframework.boot.cli.command.CommandException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public abstract class PrintingOptionHandler extends RestOptionHandler {
	private OptionSpec<Boolean> prettyOption;
	private OptionSpec<String> formatOption;

	@Override
	protected void options() {
		prettyOption = option("pretty", "Pretty print the response").withOptionalArg()
				.ofType(Boolean.class).defaultsTo(false);
		formatOption = option(asList("format", "f"), "Pretty print the response").withRequiredArg();
		super.options();
	}

	public void print(Object o, OptionSet options) {
		if (options.has(formatOption)) {
			format(o, options);
		} else {
			prettyPrint(o, options);
		}
	}

	private void format(Object o, OptionSet options) {
		Template template = Mustache.compiler().escapeHTML(false).defaultValue("")
				.compile(formatOption.value(options));
		PrintWriter out = new PrintWriter(System.out);
		if (o instanceof Collection) {
			for (Object i : (Collection<?>) o) {
				template.execute(i, out);
			}
		} else {
			template.execute(o, out);
		}
		out.flush();
	}

	private void prettyPrint(Object o, OptionSet options) {
		boolean prettyPrint = options.has(prettyOption)
				&& (!options.hasArgument(prettyOption) || prettyOption.value(options));
		ObjectWriter writer = Jackson2ObjectMapperBuilder.json().build().writer()
				.withoutFeatures(Feature.AUTO_CLOSE_TARGET);
		if (prettyPrint) {
			writer = writer.withDefaultPrettyPrinter();
		}
		try {
			writer.writeValue(System.out, o);
		} catch (IOException ex) {
			throw new CommandException(ex);
		}
	}
}
