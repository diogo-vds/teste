package br.com.bancoamazonia.gatelayer.translate;

/**
 * Se na anotação PositionalField existir o campo transactionFieldName
 * E
 * O valor contido nesse campo bater com algum valor no transactionLayout
 * Parte da mensagem será convertida para o novo objeto
 */
public @interface ConditionalObject { //TODO implementar
    String[] transactionLayout();

    Class<?> type();
}
