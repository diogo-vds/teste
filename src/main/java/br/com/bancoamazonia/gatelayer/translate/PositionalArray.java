package br.com.bancoamazonia.gatelayer.translate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface PositionalArray {
    /**
     * Posicional de quantas iterações tem nessa mensagem
     */
    int positionalIterationSize();

    /**
     * Parâmetro do máximo de iterações para andar com o cursor
     */
    int maxIterations();

    /**
     * Tamanho do objeto por iteração
     */
    int rowLength();

    String description() default "";
}
