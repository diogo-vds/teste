package br.com.bancoamazonia.gatelayer.translate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * No JSON o layout deve poder ser encontrado na tag "msgTrn"
 * Foi padronizado assim já que o valor do layout estava contido dentro do header
 * <p>
 * Na conversão de json para posicional, existe um regex para encontrar essa tag dentro do texto
 * e encontrar a estrutura de layout
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface Layout {
    //TODO segundo Saulo, existem mensagens que não podem ir para o core novo
    String[] value();

    String description() default "";

    int messageLength();

}
