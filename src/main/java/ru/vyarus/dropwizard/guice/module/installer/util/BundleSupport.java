package ru.vyarus.dropwizard.guice.module.installer.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.dropwizard.setup.Bootstrap;
import ru.vyarus.dropwizard.guice.module.context.ConfigurationContext;
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBootstrap;
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBundle;
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyEnvironment;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to work with registered {@link io.dropwizard.ConfiguredBundle} objects within dropwizard
 * {@link Bootstrap} object.
 *
 * @author Vyacheslav Rusakov
 * @since 01.08.2015
 */
public final class BundleSupport {

    private BundleSupport() {
    }

    /**
     * Process initialization for initially registered and all transitive bundles.
     * <ul>
     * <li>Executing initial bundles initialization (registered in {@link ru.vyarus.dropwizard.guice.GuiceBundle}
     * and by bundle lookup)</li>
     * <li>During execution bundles may register other bundles (through {@link GuiceyBootstrap})</li>
     * <li>Execute registered bundles and repeat from previous step until no new bundles registered</li>
     * </ul>
     * Bundles duplicates are checked by type: only one bundle instance may be registered.
     *
     * @param context bundles context
     */
    public static void initBundles(final ConfigurationContext context) {
        final List<GuiceyBundle> bundles = context.getEnabledBundles();
        final List<Class<? extends GuiceyBundle>> installedBundles = Lists.newArrayList();
        final GuiceyBootstrap guiceyBootstrap = new GuiceyBootstrap(context, bundles);

        // iterating while no new bundles registered
        while (!bundles.isEmpty()) {
            final List<GuiceyBundle> processingBundles = Lists.newArrayList(removeDuplicates(bundles));
            bundles.clear();
            for (GuiceyBundle bundle : removeTypes(processingBundles, installedBundles)) {

                final Class<? extends GuiceyBundle> bundleType = bundle.getClass();
                Preconditions.checkState(!installedBundles.contains(bundleType),
                        "State error: duplicate bundle '%s' registration", bundleType.getName());

                // disabled bundles are not processed (so nothing will be registered from it)
                // important to check here because transitive bundles may appear to be disabled
                if (context.isBundleEnabled(bundleType)) {
                    context.setScope(bundleType);
                    bundle.initialize(guiceyBootstrap);
                    context.closeScope();
                }

                installedBundles.add(bundleType);
            }
        }
        context.lifecycle().bundlesInitialized(context.getEnabledBundles(), context.getDisabledBundles());
    }

    /**
     * Run all enabled bundles.
     *
     * @param context bundles context
     */
    public static void runBundles(final ConfigurationContext context) {
        final GuiceyEnvironment env = new GuiceyEnvironment(context);
        for (GuiceyBundle bundle : context.getEnabledBundles()) {
            bundle.run(env);
        }
        context.lifecycle().bundlesStarted(context.getEnabledBundles());
    }

    /**
     * Remove duplicates in list by rule: only one instance of type must be present in list.
     *
     * @param list bundles list
     * @param <T>  required bundle type
     * @return list cleared from duplicates
     */
    public static <T> List<T> removeDuplicates(final List<T> list) {
        final List<Class> registered = Lists.newArrayList();
        final Iterator it = list.iterator();
        while (it.hasNext()) {
            final Class type = it.next().getClass();
            if (registered.contains(type)) {
                it.remove();
            } else {
                registered.add(type);
            }
        }
        return list;
    }

    /**
     * Filter list from objects of type present in filter list.
     *
     * @param list   list to filter
     * @param filter types to filter
     * @param <T>    required type
     * @return filtered list
     */
    public static <T> List<T> removeTypes(final List<T> list, final List<Class<? extends T>> filter) {
        final Iterator it = list.iterator();
        while (it.hasNext()) {
            final Class type = it.next().getClass();
            if (filter.contains(type)) {
                it.remove();
            }
        }
        return list;
    }

    /**
     * @param bootstrap dropwizard bootstrap instance
     * @param type      required bundle type (or marker interface)
     * @param <T>       required bundle type
     * @return list of bundles of specified type
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> findBundles(final Bootstrap bootstrap, final Class<T> type) {
        final List bundles = Lists.newArrayList(resolveBundles(bootstrap, "configuredBundles"));
        bundles.removeIf(o -> !type.isAssignableFrom(o.getClass()));
        return bundles;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> resolveBundles(final Bootstrap bootstrap, final String field) {
        try {
            final Field declaredField = Bootstrap.class.getDeclaredField(field);
            declaredField.setAccessible(true);
            final List<T> res = (List<T>) declaredField.get(bootstrap);
            declaredField.setAccessible(false);
            // in case of mock bootstrap (tests)
            return MoreObjects.firstNonNull(res, Collections.<T>emptyList());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to resolve bootstrap field " + field, e);
        }
    }
}
