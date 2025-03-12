package br.com.bancoamazonia.gatelayer.translate.structure;

import br.com.bancoamazonia.gatelayer.translate.AgencyCode;
import br.com.bancoamazonia.gatelayer.translate.PositionalField;

import java.math.BigDecimal;

public class EstParticao219 {

    //EstLogRecebimentoCodigoBarras
    @PositionalField(length = 4, description = "Código da agência origem")
    private Integer codigoAgenciaOrigem;

    @PositionalField(length = 4, description = "Número do contrato")
    private Integer numeroContrato;

    @PositionalField(length = 4, description = "Código Receita Federal")
    private Integer codReceita;

    @PositionalField(length = 2, description = "Tipo de Recolhimento (DomTipRcl)")
    private Integer tipoRecolhimento;

    @AgencyCode
    @PositionalField(length = 4, description = "Código da Agência da Conta")
    private Integer codigoAgenciaConta;

    @PositionalField(length = 10, description = "Número da Conta Corrente")
    private Long numeroContaCorrente;

    @PositionalField(length = 1, description = "Variação da Conta de Crédito (DomCtaVarAge)")
    private Integer variacaoContaCredito;

    @PositionalField(length = 1, description = "Número da via do cartão")
    private Integer numeroViaCartao;

    @PositionalField(length = 8, description = "Data de vencimento (yyyyMMdd)")
    private String dataVencimento;

    @PositionalField(length = 8, description = "Data de referência do lançamento (yyyyMMdd)")
    private String dataReferenciaLancamento;

    @PositionalField(length = 13, precision = 2, description = "Valor base do documento")
    private BigDecimal valorBaseDocumento;

    @PositionalField(length = 13, precision = 2, description = "Valor pago em multa e juros")
    private BigDecimal valorPagoMultaJuros;

    @PositionalField(length = 13, precision = 2, description = "Valor pago em juros")
    private BigDecimal valorPagoJuros;

    @PositionalField(length = 13, precision = 2, description = "Valor Pago em Dinheiro")
    private BigDecimal valorPagoDinheiro;

    @PositionalField(length = 13, precision = 2, description = "Valor pago em acréscimos")
    private BigDecimal valorPagoAcrescimos;

    @PositionalField(length = 13, precision = 2, description = "Valor dos Descontos")
    private BigDecimal valorDescontos;

    @PositionalField(length = 13, precision = 2, description = "Valor de abatimentos")
    private BigDecimal valorAbatimentos;

    @PositionalField(length = 13, precision = 2, description = "Valor Debitado")
    private BigDecimal valorDebito;

    @PositionalField(length = 13, precision = 2, description = "Valor pago em cheque")
    private BigDecimal valorPagoCheque;

    @PositionalField(length = 2, description = "Número de dias de Bloqueio do Cheque")
    private Integer numeroDiasBloqueioCheque;

    @PositionalField(length = 44, description = "Código de barras")
    private String codigoBarras;

    @PositionalField(length = 12, description = "Nsu da autoricao de consulta ao jdnpc")
    private Long nsuAutCnsJdnpc;

    @PositionalField(length = 12, description = "Numero da autorizacão")
    private Long numeroAutorizacao;

    @PositionalField(length = 12, description = "Número do documento")
    private Long numDoc;

    @PositionalField(length = 16, description = "Complemento")
    private String complemento;

    @PositionalField(length = 20, description = "Descrição")
    private String descricao;

    @PositionalField(length = 20, description = "Numero do cartão")
    private String numCmg;

    //EstLogRecebimentoCodigoBarrasTitulo
    @PositionalField(length = 50, description = "Nome/Razão Social do Cedente")
    private String nomeRazaoSocialCedente;

    @PositionalField(length = 14, description = "CPF/CNPJ do Cedente")
    private String cpfCnpjCedente;

    @PositionalField(length = 1, description = "Tipo de pessoa cedente (DomTipPesSpb)")
    private String tipPesCed;

    @PositionalField(length = 14, description = "CPF/CNPJ do Sacado")
    private String cpfCnpjSacado;

    @PositionalField(length = 1, description = "Tipo de pessoa sacado (DomTipPesSpb)")
    private String tipPesSac;

    @PositionalField(length = 4, description = "DDD do Telefone")
    private String telDDD;

    @PositionalField(length = 9, description = "Telefone do sacado com nove digitos")
    private String telefoneNove;

    @PositionalField(length = 1, description = "Status do recebimento do titulo.")
    private Integer statusTitulo;

    @PositionalField(length = 12, description = "Flag para reenvio de pagamento.")
    private Long numAtrReenviaPagTitSpb;

}
