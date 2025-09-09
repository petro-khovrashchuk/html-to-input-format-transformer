package com.petrokhovrashchuk.petprojects.di.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;

@Qualifier
@Retention(RUNTIME)
public @interface JsoupParser {}
