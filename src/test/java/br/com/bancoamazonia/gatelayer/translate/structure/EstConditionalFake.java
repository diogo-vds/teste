package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.AgencyCode;
import br.com.bancoamazonia.gatelayer.translate.ConditionalObject;
import br.com.bancoamazonia.gatelayer.translate.Layout;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;

@Layout(value = {"999999"},
        messageLength = 15,
        description = "Layout de teste para o @ConditionalObject")
public class EstConditionalFake {

    @PositionalField(length = 6, description = "Código do layout")
    private String msgTrn;

    @PositionalField(length = 5, description = "Código da transação")
    private Integer msgTtrCod;

    @AgencyCode
    @PositionalField(length = 5, description = "Código da agência")
    private String codigoAgencia;

    @PositionalField(length = 10,
            description = "Parte variável de teste",
            transactionFieldName = "msgTtrCod",
            conditionalObjects = {
                    @ConditionalObject(transactionLayout = {"21000"}, type = EstLogFake.class)
            })
    private String msgLogValPrtFepCmg;

}