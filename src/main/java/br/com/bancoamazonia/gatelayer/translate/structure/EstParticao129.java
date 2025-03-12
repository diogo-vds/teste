package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.AgencyCode;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;

import java.math.BigDecimal;

public class EstParticao129 {

    @PositionalField(length = 4, description = "Sem descrição")
    private Integer log129PtaCodOri;
    @PositionalField(length = 4, description = "Sem descrição")
    private Integer log129CnaNum;
    @PositionalField(length = 4, description = "Sem descrição")
    private Integer log129CodRce;
    @PositionalField(length = 2, description = "Sem descrição (DomTipRcl)")
    private Integer log129CrrTipRcl;
    @AgencyCode
    @PositionalField(length = 4, description = "Sem descrição")
    private Integer log129PtaCodCta;
    @PositionalField(length = 10, description = "Sem descrição")
    private Long log129CtaNum;
    @PositionalField(length = 1, description = "Sem descrição (DomCtaVarAge)")
    private Integer log129CtaVar;
    @PositionalField(length = 1, description = "Sem descrição")
    private Integer log129CmgVia;
    @PositionalField(length = 8, description = "Sem descrição (yyyyMMdd)")
    private String log129DatVen;
    @PositionalField(length = 8, description = "Sem descrição (yyyyMMdd)")
    private String log129DatRef;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValBas;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValMul;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValJur;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValCrt;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValAcr;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValDcn;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValAbt;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValDin;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private BigDecimal log129ValChq;
    @PositionalField(length = 13, precision = 2, description = "Sem descrição")
    private Integer log129NumDiaBlq;
    @PositionalField(length = 44, description = "Sem descrição")
    private String log129CodBar;
    @PositionalField(length = 12, description = "Sem descrição")
    private long log129AgdSeq;
    @PositionalField(length = 12, description = "Sem descrição")
    private long log129NumAtr;
    @PositionalField(length = 12, description = "Sem descrição")
    private long log129NumDoc;
    @PositionalField(length = 16, description = "Sem descrição")
    private String log129Cpl;
    @PositionalField(length = 20, description = "Sem descrição")
    private String log129Dsc;
    @PositionalField(length = 20, description = "Sem descrição")
    private String cmgNum;


}
