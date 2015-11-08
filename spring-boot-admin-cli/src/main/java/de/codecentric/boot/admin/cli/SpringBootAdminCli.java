package de.codecentric.boot.admin.cli;

import org.springframework.boot.cli.command.CommandRunner;
import org.springframework.boot.cli.command.core.HelpCommand;
import org.springframework.boot.cli.command.core.HintCommand;
import org.springframework.boot.loader.tools.LogbackInitializer;

import de.codecentric.boot.admin.cli.command.EnvironmentCommand;
import de.codecentric.boot.admin.cli.command.HealthCommand;
import de.codecentric.boot.admin.cli.command.InfoCommand;
import de.codecentric.boot.admin.cli.command.ListCommand;
import de.codecentric.boot.admin.cli.command.LogfileCommand;
import de.codecentric.boot.admin.cli.command.LoggerCommand;
import de.codecentric.boot.admin.cli.command.MetricsCommand;
import de.codecentric.boot.admin.cli.command.RefreshCommand;
import de.codecentric.boot.admin.cli.command.RegisterCommand;
import de.codecentric.boot.admin.cli.command.ThreadDumpCommand;
import de.codecentric.boot.admin.cli.command.TraceCommand;
import de.codecentric.boot.admin.cli.command.UnregisterCommand;
import de.codecentric.boot.admin.cli.command.VersionCommand;

public final class SpringBootAdminCli {

	private SpringBootAdminCli() {
	}

	public static void main(String... args) {
		System.setProperty("java.awt.headless", Boolean.toString(true));
		LogbackInitializer.initialize();

		CommandRunner runner = new CommandRunner("bootadm");
		runner.addCommand(new HelpCommand(runner));
		runner.addCommand(new HintCommand(runner));
		runner.addCommand(new VersionCommand());
		runner.setOptionCommands(HelpCommand.class, VersionCommand.class);
		runner.setHiddenCommands(HintCommand.class);

		runner.addCommand(new ListCommand());
		runner.addCommand(new EnvironmentCommand());
		runner.addCommand(new RefreshCommand());
		runner.addCommand(new RegisterCommand());
		runner.addCommand(new UnregisterCommand());
		runner.addCommand(new MetricsCommand());
		runner.addCommand(new ThreadDumpCommand());
		runner.addCommand(new InfoCommand());
		runner.addCommand(new LogfileCommand());
		runner.addCommand(new HealthCommand());
		runner.addCommand(new LoggerCommand());
		runner.addCommand(new TraceCommand());

		int exitCode = runner.runAndHandleErrors(args);
		if (exitCode != 0) {
			System.exit(exitCode);
		}
	}

}
