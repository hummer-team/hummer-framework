package com.hummer.first.restfull.plugin.bean;

import com.hummer.common.exceptions.SysException;
import com.hummer.core.PropertiesContainer;
import com.hummer.first.restfull.plugin.invoke.RemoteServiceInvokeWrapper;
import com.hummer.first.restfull.plugin.invoke.RemoteServiceInvokeWrapperImpl;
import com.hummer.first.restfull.plugin.annotation.HummerRestApiClient;
import com.hummer.first.restfull.plugin.annotation.HummerRestApiClientBootScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import static com.hummer.common.SysConstant.APPLICATION_BOOT_CLASS;

/**
 * register dynamic bean factory
 *
 * @author lee
 */
@Slf4j
public class RegisterDynamicProxyBean implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware {
    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;
    private Environment environment;
    private BeanFactory beanFactory;
    private final RemoteServiceInvokeWrapper invokeWrapper = RemoteServiceInvokeWrapperImpl.INSTANCE;

    /**
     * Callback that supplies the bean {@link ClassLoader class loader} to
     * a bean instance.
     * <p>Invoked <i>after</i> the population of normal bean properties but
     * <i>before</i> an initialization callback such as
     * {@link InitializingBean InitializingBean's}
     * {@link InitializingBean#afterPropertiesSet()}
     * method or a custom init-method.
     *
     * @param classLoader the owning class loader
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Callback that supplies the owning factory to a bean instance.
     * <p>Invoked after the population of normal bean properties
     * but before an initialization callback such as
     * {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
     *
     * @param beanFactory owning BeanFactory (never {@code null}).
     *                    The bean can immediately call methods on the factory.
     * @throws BeansException in case of initialization errors
     * @see BeanInitializationException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Set the {@code Environment} that this component runs in.
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Set the ResourceLoader that this object runs in.
     * <p>This might be a ResourcePatternResolver, which can be checked
     * through {@code instanceof ResourcePatternResolver}. See also the
     * {@code ResourcePatternUtils.getResourcePatternResolver} method.
     * <p>Invoked after population of normal bean properties but before an init callback
     * like InitializingBean's {@code afterPropertiesSet} or a custom init-method.
     * Invoked before ApplicationContextAware's {@code setApplicationContext}.
     *
     * @param resourceLoader the ResourceLoader object to be used by this object
     * @see ResourcePatternResolver
     * @see ResourcePatternUtils#getResourcePatternResolver
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Register bean definitions as necessary based on the given annotation metadata of
     * the importing {@code @Configuration} class.
     * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
     * registered here, due to lifecycle constraints related to {@code @Configuration}
     * class processing.
     * <p>The default implementation is empty.
     *
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry               current bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Class<?> bootClass = PropertiesContainer.valueOf(APPLICATION_BOOT_CLASS, Class.class);
        HummerRestApiClientBootScan restBootScan = bootClass.getAnnotation(HummerRestApiClientBootScan.class);
        if (restBootScan == null) {
            return;
        }

        ClassPathScanningCandidateComponentProvider classScanner = getClassScannerProvider();
        classScanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(HummerRestApiClient.class);
        classScanner.addIncludeFilter(annotationTypeFilter);

        //scan package
        Set<BeanDefinition> beanDefinitionSet = classScanner.findCandidateComponents(restBootScan.scanBasePackages());
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                registerBeans(((AnnotatedBeanDefinition) beanDefinition));
            }
        }
    }

    private void registerBeans(AnnotatedBeanDefinition annotatedBeanDefinition) {
        String className = annotatedBeanDefinition.getBeanClassName();
        ((DefaultListableBeanFactory) this.beanFactory).registerSingleton(className, createProxy(annotatedBeanDefinition));
    }

    private Object createProxy(AnnotatedBeanDefinition annotatedBeanDefinition) {
        AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
        try {
            Class<?> target = Class.forName(annotationMetadata.getClassName());
            InvocationHandler invocationHandler = createInvocationHandler();
            return Proxy.newProxyInstance(HummerRestApiClient.class.getClassLoader(), new Class[]{target}, invocationHandler);
        } catch (ClassNotFoundException e) {
            throw new SysException(50000, String.format("create proxy %s failed", annotationMetadata.getClassName()), e);
        }
    }

    private InvocationHandler createInvocationHandler() {
         return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                return invokeWrapper.invoke(proxy, method, args);
            }
        };
    }

    private ClassPathScanningCandidateComponentProvider getClassScannerProvider() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (!beanDefinition.getMetadata().isInterface()) {
                    return false;
                }
                try {
                    Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), classLoader);
                    return !target.isAnnotation();
                } catch (Exception ex) {
                    throw new SysException(50000, String.format("instance %s exception"
                            , beanDefinition.getMetadata().getClassName()), ex);
                }
            }
        };
    }
}
