package br.com.bancoamazonia.gatelayer.translate.exception;

/**
 * O código do layout é encontrado com base em um regex
 * Se ele não vier no json o sistema deve lançar essa exceção
 */
public class TranslatorLayoutNotFound extends RuntimeException {
    public TranslatorLayoutNotFound(String message) {
        super("Recived message: " + message);
    }
}
