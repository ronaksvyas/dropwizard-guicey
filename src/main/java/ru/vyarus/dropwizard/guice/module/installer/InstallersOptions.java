package ru.vyarus.dropwizard.guice.module.installer;

import ru.vyarus.dropwizard.guice.module.context.option.Option;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.JerseyManaged;

/**
 * Bundled installers options. Applies for both {@link CoreInstallersBundle} and {@link WebInstallersBundle}
 * installers.
 */
public enum InstallersOptions implements Option {

    /**
     * Affects {@link ru.vyarus.dropwizard.guice.module.installer.feature.web.WebServletInstaller}.
     * During servlet registration, url patterns may clash with already installed servlets. By default, only warning
     * will be printed in log. Set option to {@code true} to throw exception on registration if clash detected.
     * <p>
     * Note: clash resolution depends on servlets registration order. Moreover, clash may appear on some 3rd party
     * servlet registration (not managed by installer) and so be not affected by this option.
     */
    DenyServletRegistrationWithClash(Boolean.class, false),

    /**
     * Affects {@link ru.vyarus.dropwizard.guice.module.installer.feature.web.listener.WebListenerInstaller}.
     * By default, dropwizard does not have configured sessions support (to be stateless), so session listeners
     * can't be installed.
     * Because session listeners may be defined as part of 3rd party bundles and most likely will complement bundle
     * functionality (aka optional part), listener installer will only log warning about not installed listeners.
     * Set option to {@code true} to throw error instead (when session listeners can't be installed because of no
     * sessions support enabled).
     */
    DenySessionListenersWithoutSession(Boolean.class, false),
    /**
     * By default, HK2 related extensions like resources or other jersey specific extensions are managed by
     * guice (guice-managed instance is registered in HK2). This makes some HK2-specific features not possible
     * (like context injection with @Context annotation).
     * {@link JerseyManaged} annotation could switch
     * annotated beans to be managed by HK2. But in some cases, it is more convenient to always use HK2 and this
     * option is supposed to be used exactly for such cases.
     * <p>
     * When false value set, all beans, managed by jersey installers
     * ({@link ru.vyarus.dropwizard.guice.module.installer.install.JerseyInstaller}) should register beans for
     * HK2 management. {@link JerseyManaged} become
     * useless in this case, instead {@link ru.vyarus.dropwizard.guice.module.installer.feature.jersey.GuiceManaged}
     * annotation could be used.
     * <p>
     * Note that even managed by HK2 beans will be singletons. Still this will not block you from using
     * {@code @Context} annotation injections, because HK2 will proxy such injections and properly handle
     * multi-threaded access (implicit provider).
     * <p>
     * NOTE: guice aop is not applicable for beans managed by HK2 (because guice aop use class proxies and not
     * instance proxies).
     * <p>
     * Startup will fail if HK2 bridge is not enabled
     * (see {@link ru.vyarus.dropwizard.guice.GuiceyOptions#UseHkBridge}) because without it you can't inject
     * any guice beans into HK2 managed instances (and if you don't need to then you don't need guice support at all).
     */
    JerseyExtensionsManagedByGuice(Boolean.class, true),
    /**
     * Force singleton scope for jersey extensions (including resources). It is highly recommended using singletons
     * to avoid redundant objects creation. Enabled by default.
     * <p>
     * Note that forced singleton is not applied to beans with explicit scoping annotation set.
     * {@link ru.vyarus.dropwizard.guice.module.support.scope.Prototype} annotation may be used to force
     * default (prototype) scope instead of singleton.
     * <p>
     * When switched off, extension scope will be driven only by scope annotation. Note that by default
     * guice and HK2 use prototype scope (for example, for resources it means new instance for each request).
     */
    ForceSingletonForJerseyExtensions(Boolean.class, true);

    private Class<?> type;
    private Object value;

    <T> InstallersOptions(final Class<T> type, final T value) {
        this.type = type;
        this.value = value;
    }


    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getDefaultValue() {
        return value;
    }
}
