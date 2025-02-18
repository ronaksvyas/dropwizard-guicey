package ru.vyarus.dropwizard.guice.module.lifecycle;

import ru.vyarus.dropwizard.guice.module.lifecycle.event.GuiceyLifecycleEvent;
import ru.vyarus.dropwizard.guice.module.lifecycle.event.configuration.*;
import ru.vyarus.dropwizard.guice.module.lifecycle.event.jersey.JerseyConfigurationEvent;
import ru.vyarus.dropwizard.guice.module.lifecycle.event.jersey.JerseyExtensionsInstalledByEvent;
import ru.vyarus.dropwizard.guice.module.lifecycle.event.jersey.JerseyExtensionsInstalledEvent;
import ru.vyarus.dropwizard.guice.module.lifecycle.event.run.*;

/**
 * Adapter for {@link GuiceyLifecycleListener}. Useful when you need to listen for multiple events: extend adapter
 * and override just required methods.
 *
 * @author Vyacheslav Rusakov
 * @since 18.04.2018
 */
public class GuiceyLifecycleAdapter implements GuiceyLifecycleListener {

    @Override
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MissingSwitchDefault",
            "checkstyle:JavaNCSS",
            "PMD.NcssCount", "PMD.CyclomaticComplexity", "PMD.SwitchStmtsShouldHaveDefault"})
    public void onEvent(final GuiceyLifecycleEvent event) {
        switch (event.getType()) {
            case ConfigurationHooksProcessed:
                configurationHooksProcessed((ConfigurationHooksProcessedEvent) event);
                break;
            case BundlesFromLookupResolved:
                lookupBundlesResolved((BundlesFromLookupResolvedEvent) event);
                break;
            case BundlesResolved:
                bundlesResolved((BundlesResolvedEvent) event);
                break;
            case BundlesInitialized:
                bundlesInitialized((BundlesInitializedEvent) event);
                break;
            case CommandsResolved:
                commandsResolved((CommandsResolvedEvent) event);
                break;
            case InstallersResolved:
                installersResolved((InstallersResolvedEvent) event);
                break;
            case ExtensionsResolved:
                extensionsResolved((ExtensionsResolvedEvent) event);
                break;
            case Initialized:
                initialized((InitializedEvent) event);
                break;
            case BeforeRun:
                beforeRun((BeforeRunEvent) event);
                break;
            case BundlesStarted:
                bundlesStarted((BundlesStartedEvent) event);
                break;
            case InjectorCreation:
                injectorCreation((InjectorCreationEvent) event);
                break;
            case ExtensionsInstalledBy:
                extensionsInstalledBy((ExtensionsInstalledByEvent) event);
                break;
            case ExtensionsInstalled:
                extensionsInstalled((ExtensionsInstalledEvent) event);
                break;
            case ApplicationRun:
                applicationRun((ApplicationRunEvent) event);
                break;
            case JerseyConfiguration:
                jerseyConfiguration((JerseyConfigurationEvent) event);
                break;
            case JerseyExtensionsInstalledBy:
                jerseyExtensionsInstalledBy((JerseyExtensionsInstalledByEvent) event);
                break;
            case JerseyExtensionsInstalled:
                jerseyExtensionsInstalled((JerseyExtensionsInstalledEvent) event);
                break;
        }
    }

    protected void configurationHooksProcessed(final ConfigurationHooksProcessedEvent event) {
        // empty
    }

    protected void lookupBundlesResolved(final BundlesFromLookupResolvedEvent event) {
        // empty
    }

    protected void bundlesResolved(final BundlesResolvedEvent event) {
        // empty
    }

    protected void bundlesInitialized(final BundlesInitializedEvent event) {
        // empty
    }

    protected void commandsResolved(final CommandsResolvedEvent event) {
        // empty
    }

    protected void installersResolved(final InstallersResolvedEvent event) {
        // empty
    }

    protected void extensionsResolved(final ExtensionsResolvedEvent event) {
        // empty
    }

    protected void initialized(final InitializedEvent event) {
        // empty
    }

    protected void beforeRun(final BeforeRunEvent event) {
        // empty
    }

    protected void bundlesStarted(final BundlesStartedEvent event) {
        // empty
    }

    protected void injectorCreation(final InjectorCreationEvent event) {
        // empty
    }

    protected void extensionsInstalledBy(final ExtensionsInstalledByEvent event) {
        // empty
    }

    protected void extensionsInstalled(final ExtensionsInstalledEvent event) {
        // empty
    }

    protected void applicationRun(final ApplicationRunEvent event) {
        // empty
    }

    protected void jerseyConfiguration(final JerseyConfigurationEvent event) {
        // empty
    }

    protected void jerseyExtensionsInstalledBy(final JerseyExtensionsInstalledByEvent event) {
        // empty
    }

    protected void jerseyExtensionsInstalled(final JerseyExtensionsInstalledEvent event) {
        // empty
    }
}
