package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.Layout;
import br.com.bancoamazonia.gatelayer.translate.PositionalArray;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;

@Layout(value = {"999998"},
        messageLength = 15,
        description = "Layout de teste para o @ConditionalObject")
public class EstArrayFake {

    @PositionalField(length = 6, description = "Código do layout")
    private String msgTrn;

    @PositionalArray(maxIterations = 3, positionalIterationSize = 1,
            rowLength = 3, description = "Estrutura array strings")
    private String[] status;

    @PositionalArray(maxIterations = 2, positionalIterationSize = 1,
            rowLength = 5, description = "Estrutura array objeto")
    private EstItemFake[] itens;

    @PositionalArray(maxIterations = 2, positionalIterationSize = 1,
            rowLength = 5, description = "Estrutura array números")
    private Integer[] sequencia;

    @PositionalField(length = 3, description = "Teste de final de arquivo")
    private String endFile;

}