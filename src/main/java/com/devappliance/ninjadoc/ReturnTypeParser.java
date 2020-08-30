
package com.devappliance.ninjadoc;

import com.devappliance.ninjadoc.api.annotations.DocumentReturnType;
import ninja.Result;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * The interface Return type parser.
 *
 * @author bnasslahsen
 */
public interface ReturnTypeParser {

    /**
     * This is a copy of GenericTypeResolver.resolveType which is not available on spring 4.
     * This also keeps compatibility with spring-boot 1 applications.
     * Resolve the given generic type against the given context class,
     * substituting type variables as far as possible.
     *
     * @param genericType  the (potentially) generic type
     * @param contextClass a context class for the target type, for example a class in which the target type appears in a method signature (can be {@code null})
     * @return the resolved type (possibly the given generic type as-is)
     * @since 5.0
     */
    static Type resolveType(Type genericType, @Nullable Class<?> contextClass) {
        if (contextClass != null) {
            if (genericType instanceof TypeVariable) {
                ResolvableType resolvedTypeVariable = resolveVariable(
                        (TypeVariable<?>) genericType, ResolvableType.forClass(contextClass));
                if (resolvedTypeVariable != ResolvableType.NONE) {
                    Class<?> resolved = resolvedTypeVariable.resolve();
                    if (resolved != null) {
                        return resolved;
                    }
                }
            } else if (genericType instanceof ParameterizedType) {
                ResolvableType resolvedType = ResolvableType.forType(genericType);
                if (resolvedType.hasUnresolvableGenerics()) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    Class<?>[] generics = new Class<?>[parameterizedType.getActualTypeArguments().length];
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    ResolvableType contextType = ResolvableType.forClass(contextClass);
                    findTypeForGenerics(generics, typeArguments, contextType);
                    Class<?> rawClass = resolvedType.getRawClass();
                    if (rawClass != null) {
                        return ResolvableType.forClassWithGenerics(rawClass, generics).getType();
                    }
                }
            }
        }
        return genericType;
    }

    /**
     * Find type for generics.
     *
     * @param generics      the generics
     * @param typeArguments the type arguments
     * @param contextType   the context type
     */
    static void findTypeForGenerics(Class<?>[] generics, Type[] typeArguments, ResolvableType contextType) {
        for (int i = 0; i < typeArguments.length; i++) {
            Type typeArgument = typeArguments[i];
            if (typeArgument instanceof TypeVariable) {
                ResolvableType resolvedTypeArgument = resolveVariable(
                        (TypeVariable<?>) typeArgument, contextType);
                if (resolvedTypeArgument != ResolvableType.NONE) {
                    generics[i] = resolvedTypeArgument.resolve();
                } else {
                    generics[i] = ResolvableType.forType(typeArgument).resolve();
                }
            } else {
                generics[i] = ResolvableType.forType(typeArgument).resolve();
            }
        }
    }

    /**
     * Resolve variable resolvable type.
     *
     * @param typeVariable the type variable
     * @param contextType  the context type
     * @return the resolvable type
     */
    static ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
        ResolvableType resolvedType;
        if (contextType.hasGenerics()) {
            resolvedType = ResolvableType.forType(typeVariable, contextType);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }

        ResolvableType superType = contextType.getSuperType();
        if (superType != ResolvableType.NONE) {
            resolvedType = resolveVariable(typeVariable, superType);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        for (ResolvableType ifc : contextType.getInterfaces()) {
            resolvedType = resolveVariable(typeVariable, ifc);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        return ResolvableType.NONE;
    }

    /**
     * Gets type.
     *
     * @param methodParameter the method parameter
     * @return the type
     */
    static Type getType(MethodParameter methodParameter) {
        if (methodParameter.getGenericParameterType() instanceof ParameterizedType)
            return ReturnTypeParser.resolveType(methodParameter.getGenericParameterType(), methodParameter.getContainingClass());
        return methodParameter.getParameterType();
    }

    /**
     * Gets return type.
     *
     * @param methodParameter the method parameter
     * @return the return type
     */
    default Type getReturnType(MethodParameter methodParameter) {
        DocumentReturnType documentReturnType = methodParameter.getMethodAnnotation(DocumentReturnType.class);
        if (documentReturnType != null) {
            Class<?> type = documentReturnType.type();
            Class<?>[] parameters = documentReturnType.genericTypes();
            ResolvableType t = ResolvableType.forClassWithGenerics(type, parameters);
            return t.getType();
        }
        if (Result.class.isAssignableFrom(methodParameter.getParameterType())) {
            return Object.class;
        }
        if (methodParameter.getGenericParameterType() instanceof ParameterizedType)
            return ReturnTypeParser.resolveType(methodParameter.getGenericParameterType(), methodParameter.getContainingClass());
        return methodParameter.getParameterType();
    }

}

