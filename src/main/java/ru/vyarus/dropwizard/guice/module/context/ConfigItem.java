package ru.vyarus.dropwizard.guice.module.context;

import ru.vyarus.dropwizard.guice.module.context.info.ItemInfo;
import ru.vyarus.dropwizard.guice.module.context.info.impl.*;

/**
 * Guicey configurable item types.
 *
 * @author Vyacheslav Rusakov
 * @since 06.07.2016
 */
public enum ConfigItem {
    /**
     * Installer.
     */
    Installer(false),
    /**
     * Extension (everything that is installed by installers (like resource, health check etc).
     */
    Extension(false),
    /**
     * {@link ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBundle}.
     * Note that guicey bundle installs other items and all of them are tracked too.
     */
    Bundle(true),
    /**
     * Guice module.
     * Note that only direct modules are tracked (if module registered by other guice module in it's configure
     * method it would not be tracked - it's pure guice staff).
     */
    Module(true),
    /**
     * Dropwizard command. Commands could be resolved with classpath scan and installed (by default disabled).
     */
    Command(true);

    private boolean instanceConfig;

    ConfigItem(final boolean instanceConfig) {
        this.instanceConfig = instanceConfig;
    }

    /**
     * @return true if instances used for configuration, false when configured with class
     */
    public boolean isInstanceConfig() {
        return instanceConfig;
    }

    /**
     * Creates info container for configuration item.
     *
     * @param type item class
     * @param <T>  type of required info container
     * @return info container instance
     */
    @SuppressWarnings("unchecked")
    public <T extends ItemInfoImpl> T newContainer(final Class<?> type) {
        final ItemInfo res;
        switch (this) {
            case Installer:
                res = new InstallerItemInfoImpl(type);
                break;
            case Extension:
                res = new ExtensionItemInfoImpl(type);
                break;
            case Bundle:
                res = new BundleItemInfoImpl(type);
                break;
            case Command:
                res = new CommandItemInfoImpl(type);
                break;
            case Module:
                res = new ModuleItemInfoImpl(type);
                break;
            default:
                res = new ItemInfoImpl(this, type);
                break;
        }
        return (T) res;
    }
}
