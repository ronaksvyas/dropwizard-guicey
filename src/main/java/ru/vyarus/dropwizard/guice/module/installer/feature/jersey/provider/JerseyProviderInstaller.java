package ru.vyarus.dropwizard.guice.module.installer.feature.jersey.provider;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.Injector;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.AbstractJerseyInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.JerseyManaged;
import ru.vyarus.dropwizard.guice.module.installer.install.binding.BindingInstaller;
import ru.vyarus.dropwizard.guice.module.installer.order.Order;
import ru.vyarus.dropwizard.guice.module.installer.util.FeatureUtils;
import ru.vyarus.java.generics.resolver.GenericsResolver;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.ext.*;
import java.util.Set;
import java.util.function.Supplier;

import static ru.vyarus.dropwizard.guice.module.installer.util.FeatureUtils.is;
import static ru.vyarus.dropwizard.guice.module.installer.util.JerseyBinding.*;

/**
 * Jersey provider installer.
 * Looks for classes annotated with {@code @javax.ws.rs.ext.Provider} and register bindings in HK context.
 * <p>
 * If provider is annotated with {@code HK2Managed} it's instance will be created by HK2, not guice.
 * This is important when extensions directly depends on HK beans (no way to wrap with {@code Provider}
 * or if it's eager extension, which instantiated by HK immediately (when hk-guice contexts not linked yet).
 * <p>
 * In some cases {@code @LazyBinding} could be an alternative to {@code HK2Managed}
 * <p>
 * Force singleton scope for extensions, but not for beans having explicit scope annotation.
 * See {@link ru.vyarus.dropwizard.guice.module.installer.InstallersOptions#ForceSingletonForJerseyExtensions}.
 * {@link ru.vyarus.dropwizard.guice.module.support.scope.Prototype} annotation may be used on guice beans
 * to declare bean in prototype scope (prevent forced singleton).
 *
 * @author Vyacheslav Rusakov
 * @see JerseyManaged
 * @see ru.vyarus.dropwizard.guice.module.installer.feature.jersey.GuiceManaged
 * @see ru.vyarus.dropwizard.guice.module.installer.install.binding.LazyBinding
 * @since 10.10.2014
 */
@Order(30)
public class JerseyProviderInstaller extends AbstractJerseyInstaller<Object> implements
        BindingInstaller {

    private static final Set<Class<?>> EXTENSION_TYPES = ImmutableSet.<Class<?>>of(
            ExceptionMapper.class,
            ParamConverterProvider.class,
            ContextResolver.class,
            MessageBodyReader.class,
            MessageBodyWriter.class,
            ReaderInterceptor.class,
            WriterInterceptor.class,
            ContainerRequestFilter.class,
            ContainerResponseFilter.class,
            DynamicFeature.class,
            ValueParamProvider.class,
            InjectionResolver.class,
            ApplicationEventListener.class
    );

    private final ProviderReporter reporter = new ProviderReporter();

    @Override
    public boolean matches(final Class<?> type) {
        return FeatureUtils.hasAnnotation(type, Provider.class);
    }

    @Override
    public <T> void install(final Binder binder, final Class<? extends T> type, final boolean lazyMarker) {
        final boolean hkManaged = isJerseyExtension(type);
        final boolean lazy = isLazy(type, lazyMarker);
        // register in guice only if not managed by hk and just in time (lazy) binding not requested
        if (!hkManaged && !lazy) {
            bindInGuice(binder, type);
        }
        reporter.provider(type, hkManaged, lazy);
    }

    @Override
    public void install(final AbstractBinder binder, final Injector injector, final Class<Object> type) {
        final boolean hkExtension = isJerseyExtension(type);
        final boolean forceSingleton = isForceSingleton(type, hkExtension);
        // since jersey 2.26 internal hk Factory class replaced by java 8 Supplier
        if (is(type, Supplier.class)) {
            // register factory directly (without wrapping)
            bindFactory(binder, injector, type, hkExtension, forceSingleton);

        } else {
            // support multiple extension interfaces on one type
            final Set<Class<?>> extensions = Sets.intersection(EXTENSION_TYPES,
                    GenericsResolver.resolve(type).getGenericsInfo().getComposingTypes());
            if (!extensions.isEmpty()) {
                for (Class<?> ext : extensions) {
                    bindSpecificComponent(binder, injector, type, ext, hkExtension, forceSingleton);
                }
            } else {
                // no known extension found
                bindComponent(binder, injector, type, hkExtension, forceSingleton);
            }
        }
    }

    @Override
    public void report() {
        reporter.report();
    }
}
