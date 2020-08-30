package com.devappliance.ninjadoc.wrappers;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class HandlerMethod {

    private final Method method;
    private final Method bridgedMethod;
    private final MethodParameter[] parameters;
    private Object bean;
    @Nullable
    private Object beanFactory;
    private Class<?> beanType;
    @Nullable
    private HttpStatus responseStatus;

    @Nullable
    private String responseStatusReason;

    @Nullable
    private HandlerMethod resolvedFromHandlerMethod;

    @Nullable
    private volatile List<Annotation[][]> interfaceParameterAnnotations;

    private String description;

    public HandlerMethod(Object bean, Method method) {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(method, "Method is required");
        this.bean = bean;
        this.beanFactory = null;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
        this.description = initDescription(this.beanType, this.method);
    }

    /**
     * Create an instance from a bean instance, method name, and parameter types.
     *
     * @throws NoSuchMethodException when the method cannot be found
     */
    public HandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(methodName, "Method name is required");
        this.bean = bean;
        this.beanFactory = null;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = bean.getClass().getMethod(methodName, parameterTypes);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
        this.description = initDescription(this.beanType, this.method);
    }

    public HandlerMethod(Method method) {
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = initMethodParameters();
    }

    public static HandlerMethod from(Method method) {
        return new HandlerMethod(method);
    }

    private static String initDescription(Class<?> beanType, Method method) {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (Class<?> paramType : method.getParameterTypes()) {
            joiner.add(paramType.getSimpleName());
        }
        return beanType.getName() + "#" + method.getName() + joiner.toString();
    }

    public Class<?> getBeanType() {
        return this.method.getDeclaringClass();
    }

    public Method getMethod() {
        return this.method;
    }

    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            result[i] = new HandlerMethodParameter(i);
        }
        return result;
    }

    /**
     * Return a single annotation on the underlying method traversing its super methods
     * if no annotation can be found on the given method itself.
     * <p>Also supports <em>merged</em> composed annotations with attribute
     * overrides as of Spring Framework 4.2.2.
     *
     * @param annotationType the type of annotation to introspect the method for
     * @return the annotation, or {@code null} if none found
     * @see AnnotatedElementUtils#findMergedAnnotation
     */
    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
    }

    /**
     * Return whether the parameter is declared with the given annotation type.
     *
     * @param annotationType the annotation type to look for
     * @see AnnotatedElementUtils#hasAnnotation
     * @since 4.3
     */
    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
    }

    public MethodParameter getReturnType() {
        return new HandlerMethodParameter(-1);
    }

    public Object getBean() {
        return this.bean;
    }

    private void evaluateResponseStatus() {
        this.responseStatus = HttpStatus.OK;
        this.responseStatusReason = HttpStatus.OK.getReasonPhrase();
    }

    private List<Annotation[][]> getInterfaceParameterAnnotations() {
        List<Annotation[][]> parameterAnnotations = this.interfaceParameterAnnotations;
        if (parameterAnnotations == null) {
            parameterAnnotations = new ArrayList<>();
            for (Class<?> ifc : ClassUtils.getAllInterfacesForClassAsSet(this.method.getDeclaringClass())) {
                for (Method candidate : ifc.getMethods()) {
                    if (isOverrideFor(candidate)) {
                        parameterAnnotations.add(candidate.getParameterAnnotations());
                    }
                }
            }
            this.interfaceParameterAnnotations = parameterAnnotations;
        }
        return parameterAnnotations;
    }

    private boolean isOverrideFor(Method candidate) {
        if (!candidate.getName().equals(this.method.getName()) ||
                candidate.getParameterCount() != this.method.getParameterCount()) {
            return false;
        }
        Class<?>[] paramTypes = this.method.getParameterTypes();
        if (Arrays.equals(candidate.getParameterTypes(), paramTypes)) {
            return true;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] !=
                    ResolvableType.forMethodParameter(candidate, i, this.method.getDeclaringClass()).resolve()) {
                return false;
            }
        }
        return true;
    }

    protected class HandlerMethodParameter extends SynthesizingMethodParameter {

        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        protected HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }

        @Override
        public Class<?> getContainingClass() {
            return HandlerMethod.this.getBeanType();
        }

        @Override
        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.getMethodAnnotation(annotationType);
        }

        @Override
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.hasMethodAnnotation(annotationType);
        }

        @Override
        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                int index = getParameterIndex();
                if (index >= 0) {
                    for (Annotation[][] ifcAnns : getInterfaceParameterAnnotations()) {
                        if (index < ifcAnns.length) {
                            Annotation[] paramAnns = ifcAnns[index];
                            if (paramAnns.length > 0) {
                                List<Annotation> merged = new ArrayList<>(anns.length + paramAnns.length);
                                merged.addAll(Arrays.asList(anns));
                                for (Annotation paramAnn : paramAnns) {
                                    boolean existingType = false;
                                    for (Annotation ann : anns) {
                                        if (ann.annotationType() == paramAnn.annotationType()) {
                                            existingType = true;
                                            break;
                                        }
                                    }
                                    if (!existingType) {
                                        merged.add(adaptAnnotation(paramAnn));
                                    }
                                }
                                anns = merged.toArray(new Annotation[0]);
                            }
                        }
                    }
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }

        @Override
        public HandlerMethodParameter clone() {
            return new HandlerMethodParameter(this);
        }
    }
}
