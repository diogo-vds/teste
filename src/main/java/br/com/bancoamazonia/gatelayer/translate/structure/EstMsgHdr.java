package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.PositionalField;

public class EstMsgHdr {

    @PositionalField(length = 6, description = "Código da transação")
    private String msgTrn;
    @PositionalField(length = 4, description = "Código da agência origem")
    private Integer msgPtaCodOri;
    @PositionalField(length = 4, description = "Código do posto de origem")
    private Integer msgPstCodOri;
    @PositionalField(length = 4, description = "Número do terminal")
    private Integer msgTerNum;
    @PositionalField(length = 4, description = "Identificador da mensagem")
    private String msgIde;
    @PositionalField(length = 6, description = "Versão da automação ")
    private Integer msgVer;
    @PositionalField(length = 1, description = "Indicador de transmisso compactada")
    private Boolean msgIdcCpc;
    @PositionalField(length = 1, description = "Indicador de mensagem com continuação")
    private Boolean msgIdcCtn;
}
