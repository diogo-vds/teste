package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.ConditionalObject;
import br.com.bancoamazonia.gatelayer.translate.Layout;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;
import br.com.bancoamazonia.gatelayer.translate.PositionalObject;
import br.com.bancoamazonia.gatelayer.translate.structure.EstMsgHdr;

@Layout(value = {"000304"},
        messageLength = 538,
        description = "Solicitação transação inter-agência com partição de 450")
public class EstFinPrtSol {

    @PositionalObject(description = "Header da mensagem")
    private EstMsgHdr msgHdr;
    @PositionalField(length = 2, description = "Código do canal de atendimento (DomCnlAtd)")
    private Integer cnlCod;
    @PositionalField(length = 2, description = "Tipo do terminal (DomTerTip)")
    private Integer terTip;
    @PositionalField(length = 4, description = "Código da agência origem")
    private Integer msgPtaCodOri;
    @PositionalField(length = 4, description = "Código da agência destino")
    private Integer msgPtaCodDsn;
    @PositionalField(length = 4, description = "Número do terminal")
    private Integer msgTerNum;
    @PositionalField(length = 8, description = "Data (yyyyMMdd) de realização da transação AAAAMMDD")
    private String msgLogDat;
    @PositionalField(length = 12, description = "Nsu da solicitação")
    private Long msgSolNum;
    @PositionalField(length = 12, description = "Nsu da solicitação a ser estornada")
    private Long msgNsuSolEsr;
    @PositionalField(length = 1, description = "Indicador de teste de limite de saque")
    private Boolean msgIdcLimSaq;
    @PositionalField(length = 5, description = "Código da transação")
    private Integer msgTtrCod;
    @PositionalField(length = 4, description = "Número da partição")
    private Integer msgLogNumPrt;
    @PositionalField(length = 450,
            description = "Parte variável do Log com num cmg (igual do FEP)",
            transactionFieldName = "msgLogNumPrt",
            conditionalObjects = {
                    @ConditionalObject(transactionLayout = {"129"}, type = EstParticao129.class),
                    @ConditionalObject(transactionLayout = {"219"}, type = EstParticao219.class)
            })
    private String msgLogValPrtFepCmg;

}
