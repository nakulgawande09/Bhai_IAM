package com.auth.oidcprovider.util;

import java.lang.annotation.*;

/**
 * Annotation to inject the current authenticated user into a method parameter.
 * Used with {@link CurrentUserArgumentResolver} to resolve the current user from the security context.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
} 