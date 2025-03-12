package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.ConditionalObject;
import br.com.bancoamazonia.gatelayer.translate.Layout;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;

@Layout(value = {"999996"},
        messageLength = 15,
        description = "Layout de teste para o @AgencyCode dentro do @ConditionalObject")
public class EstAgencyConditionalFake {

    @PositionalField(length = 6, description = "Código do layout")
    private String msgTrn;

    @PositionalField(length = 5, description = "Código da transação")
    private Integer msgTtrCod;

    @PositionalField(length = 15,
            description = "Parte variável de teste",
            transactionFieldName = "msgTtrCod",
            conditionalObjects = {
                    @ConditionalObject(transactionLayout = {"21000"}, type = EstLogAgenciaFake.class)
            })
    private String msgLogValPrtFepCmg;

}