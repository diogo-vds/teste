package br.com.bancoamazonia.gatelayer.translate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface PositionalField {
    int length();

    int precision() default 0;

    String description() default "";

    String transactionFieldName() default "";

    ConditionalObject[] conditionalObjects() default {};//TODO desenvolver isso
}
