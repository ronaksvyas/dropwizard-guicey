package ru.vyarus.dropwizard.guice.module.lifecycle.event.configuration;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import ru.vyarus.dropwizard.guice.module.context.option.Options;
import ru.vyarus.dropwizard.guice.module.lifecycle.GuiceyLifecycle;
import ru.vyarus.dropwizard.guice.module.lifecycle.event.ConfigurationPhaseEvent;

import java.util.List;

/**
 * Called if commands search is enabled ({@link ru.vyarus.dropwizard.guice.GuiceBundle.Builder#searchCommands()})
 * and at least one command found. Not called otherwise.
 *
 * @author Vyacheslav Rusakov
 * @since 15.06.2019
 */
public class CommandsResolvedEvent extends ConfigurationPhaseEvent {

    private final List<Command> commands;

    public CommandsResolvedEvent(final Options options, final Bootstrap bootstrap, final List<Command> installed) {
        super(GuiceyLifecycle.CommandsResolved, options, bootstrap);
        commands = installed;
    }

    /**
     * @return list of registered commands
     */
    public List<Command> getCommands() {
        return commands;
    }
}
