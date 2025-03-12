package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.AgencyCode;
import br.com.bancoamazonia.gatelayer.translate.Layout;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;
import br.com.bancoamazonia.gatelayer.translate.PositionalObject;

import java.math.BigDecimal;

@Layout(value = {"999997"},
        messageLength = 16,
        description = "Mensagem de teste para conversão de objeto")
public class EstObjectFake {

    @PositionalField(length = 6, description = "Código do layout")
    private String msgTrn;

    @PositionalObject(description = "Teste de objeto 1")
    private EstItemFake obj1;

    @PositionalField(length = 8, description = "Mensagem posicional")
    private String data;

    @PositionalObject(description = "Teste de objeto 1")
    private EstItemFake obj2;

    @PositionalField(length = 1, description = "Teste de boleano")
    private Boolean ativo;

    @PositionalField(length = 3, precision = 2, description = "Teste de bigDecimal")
    private BigDecimal valor;

    @AgencyCode(insideAccount = true)
    @PositionalField(length = 10, description = "Teste de Long")
    private Long numeroConta;

    @PositionalField(length = 3, description = "Código de retorno")
    private Integer msgCodRet;

}