package ru.vyarus.dropwizard.guice.module.context.info.impl;

import io.dropwizard.cli.EnvironmentCommand;
import ru.vyarus.dropwizard.guice.module.context.ConfigItem;
import ru.vyarus.dropwizard.guice.module.context.ConfigScope;
import ru.vyarus.dropwizard.guice.module.context.info.CommandItemInfo;

/**
 * Command item info implementation.
 *
 * @author Vyacheslav Rusakov
 * @since 27.07.2016
 */
public class CommandItemInfoImpl extends InstanceItemInfoImpl implements CommandItemInfo {

    public CommandItemInfoImpl(final Class<?> type) {
        super(ConfigItem.Command, type);
    }

    @Override
    public boolean isFromScan() {
        return getRegisteredBy().contains(ConfigScope.ClasspathScan.getType());
    }

    @Override
    public boolean isEnvironmentCommand() {
        return EnvironmentCommand.class.isAssignableFrom(getType());
    }
}
