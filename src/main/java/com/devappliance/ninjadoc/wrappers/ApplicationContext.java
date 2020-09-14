package com.devappliance.ninjadoc.wrappers;

import com.google.inject.Injector;
import ninja.Route;
import ninja.Router;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class ApplicationContext {
    private final Injector injector;
    private final Router router;


    public ApplicationContext(Injector injector) {
        this.injector = injector;
        this.router = this.injector.getInstance(Router.class);
    }

    public static ApplicationContext from(Injector injector) {
        return new ApplicationContext(injector);
    }

    /**
     * Find all beans which are annotated with the supplied {@link Annotation} type,
     * returning a Map of bean names with corresponding bean instances.
     * <p>Note that this method considers objects created by FactoryBeans, which means
     * that FactoryBeans will get initialized in order to determine their object type.
     *
     * @param annotationType the type of annotation to look for
     *                       (at class, interface or factory method level of the specified bean)
     * @return a Map with the matching beans, containing the bean names as
     * keys and the corresponding bean instances as values
     * @see #findAnnotationOnBean
     * @since 3.0
     */
    public Map<String, Object> getControllerBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        List<? extends Class<?>> classList = router.getRoutes().stream().map(Route::getControllerClass)
                .filter(aClass -> aClass.isAnnotationPresent(annotationType))
                .collect(Collectors.toList());
        Map<String, Object> mapping = new HashMap<>();
        return getControllerList(classList, mapping);
    }

    public Map<String, Object> getControllers() {
        List<? extends Class<?>> classList = router.getRoutes().stream().map(Route::getControllerClass)
                .collect(Collectors.toList());
        Map<String, Object> mapping = new HashMap<>();
        return getControllerList(classList, mapping);
    }

    private Map<String, Object> getControllerList(List<? extends Class<?>> classList, Map<String, Object> mapping) {
        classList.stream().map(this::getBean).forEach(aClass -> {
            if (!mapping.containsKey(aClass.getClass().getSimpleName())) {
                mapping.put(aClass.getClass().getSimpleName(), aClass);
            }
        });
        return mapping;
    }

    public <T> T getBean(Class<T> requiredType) {
        return injector.getInstance(requiredType);
    }

    /**
     * Gets an annotation from a bean
     *
     * @param beanName       the name of the bean
     * @param annotationType the type of the annotation
     * @param <A>            Generic type
     * @return the annotation or null if none was found
     */
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        Class<?> type = getType(beanName);
        return (type != null ? AnnotationUtils.findAnnotation(type, annotationType) : null);
    }

    public Class<?> getType(String beanName) {
        return router.getRoutes().stream().map(Route::getControllerClass)
                .filter(aClass -> aClass.getSimpleName().equals(beanName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No bean found"));
    }

    public List<Route> getRoutes() {
        return router.getRoutes();
    }
}
