package ru.vyarus.dropwizard.guice.module.context.info.impl;

import com.google.common.collect.Sets;
import ru.vyarus.dropwizard.guice.module.context.ConfigItem;
import ru.vyarus.dropwizard.guice.module.context.ConfigScope;
import ru.vyarus.dropwizard.guice.module.context.info.ExtensionItemInfo;
import ru.vyarus.dropwizard.guice.module.installer.FeatureInstaller;

import java.util.Set;

/**
 * Extension item info implementation.
 *
 * @author Vyacheslav Rusakov
 * @since 06.07.2016
 */
public class ExtensionItemInfoImpl extends ClassItemInfoImpl implements ExtensionItemInfo {

    private Class<? extends FeatureInstaller> installedBy;
    private boolean lazy;
    private boolean jerseyManaged;
    private final Set<Class<?>> disabledBy = Sets.newLinkedHashSet();
    // little hack used to preserve installer reference during initialization
    private FeatureInstaller installer;

    public ExtensionItemInfoImpl(final Class<?> type) {
        super(ConfigItem.Extension, type);
    }

    @Override
    public Set<Class<?>> getDisabledBy() {
        return disabledBy;
    }

    @Override
    public boolean isEnabled() {
        return disabledBy.isEmpty();
    }

    @Override
    public Class<? extends FeatureInstaller> getInstalledBy() {
        return installedBy;
    }

    @Override
    public boolean isFromScan() {
        return getRegisteredBy().contains(ConfigScope.ClasspathScan.getType());
    }

    @Override
    public boolean isLazy() {
        return lazy;
    }

    @Override
    public boolean isJerseyManaged() {
        return jerseyManaged;
    }

    public void setLazy(final boolean lazy) {
        this.lazy = lazy;
    }

    public void setJerseyManaged(final boolean jerseyManaged) {
        this.jerseyManaged = jerseyManaged;
    }

    public FeatureInstaller getInstaller() {
        return installer;
    }

    public void setInstaller(FeatureInstaller installer) {
        this.installer = installer;
        this.installedBy = installer.getClass();
    }
}
