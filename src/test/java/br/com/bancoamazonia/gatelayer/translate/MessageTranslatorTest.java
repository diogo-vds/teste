package br.com.bancoamazonia.gatelayer.translate;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MessageTranslatorTest {

    @BeforeAll
    public static void startup() throws IOException, ClassNotFoundException {
        MessageTranslator.startup();
    }

    @Test
    public void findAgencyValue() {
        String agencyCode = "12578";
        String msg = createMsgConditional("21000", agencyCode);
        String agencyFound = MessageTranslator.searchForAgency(msg);
        assertEquals(agencyCode, agencyFound);
    }


    @Test
    public void findAgencyInsideConditionalValue() {
        String agencyCode = "12578";
        String msg = createMsgAgenciaConditional("21000", agencyCode);
        String agencyFound = MessageTranslator.searchForAgency(msg);
        assertEquals(agencyCode, agencyFound);
    }

    @Test
    public void findAgencyInsideAccount() {
        String agencyCode = "999";
        String msg = createMsgObject(agencyCode);
        String agencyFound = MessageTranslator.searchForAgency(msg);
        assertEquals(agencyCode, agencyFound);
    }


    @Test
    public void findNoAgencyValue() {
        String msg = createMsgArray();
        String agencyFound = MessageTranslator.searchForAgency(msg);
        assertNull(agencyFound);
    }


    @Test
    public void positional304WithConditional() throws JsonProcessingException {
        String positional = "0003040995000007003B3C02210000030709950000070020241230000134801967000000000000172390021900000000000099000700707634530020241230202412300000000000765000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000765000000000000000000034195994400000765001090011438971178219448000002082227274000134801967000000000000                                                                                                                                                     0000000000000";
        String expected = "{\"msgHdr\":{\"msgTrn\":\"000304\",\"msgPtaCodOri\":995,\"msgPstCodOri\":0,\"msgTerNum\":700,\"msgIde\":\"3B3C\",\"msgVer\":22100,\"msgIdcCpc\":false,\"msgIdcCtn\":false},\"cnlCod\":3,\"terTip\":7,\"msgPtaCodOri\":995,\"msgPtaCodDsn\":0,\"msgTerNum\":700,\"msgLogDat\":\"20241230\",\"msgSolNum\":134801967,\"msgNsuSolEsr\":0,\"msgIdcLimSaq\":true,\"msgTtrCod\":72390,\"msgLogNumPrt\":219,\"msgLogValPrtFepCmg\":{\"codigoAgenciaOrigem\":0,\"numeroContrato\":0,\"codReceita\":0,\"tipoRecolhimento\":99,\"codigoAgenciaConta\":7,\"numeroContaCorrente\":70763453,\"variacaoContaCredito\":0,\"numeroViaCartao\":0,\"dataVencimento\":\"20241230\",\"dataReferenciaLancamento\":\"20241230\",\"valorBaseDocumento\":\"0000000000765.00\",\"valorPagoMultaJuros\":\"0000000000000.00\",\"valorPagoJuros\":\"0000000000000.00\",\"valorPagoDinheiro\":\"0000000000000.00\",\"valorPagoAcrescimos\":\"0000000000000.00\",\"valorDescontos\":\"0000000000000.00\",\"valorAbatimentos\":\"0000000000000.00\",\"valorDebito\":\"0000000000765.00\",\"valorPagoCheque\":\"0000000000000.00\",\"numeroDiasBloqueioCheque\":0,\"codigoBarras\":\"34195994400000765001090011438971178219448000\",\"nsuAutCnsJdnpc\":2082227274,\"numeroAutorizacao\":134801967,\"numDoc\":0,\"complemento\":\"\",\"descricao\":\"\",\"numCmg\":\"\",\"nomeRazaoSocialCedente\":\"\",\"cpfCnpjCedente\":\"\",\"tipPesCed\":\"\",\"cpfCnpjSacado\":\"\",\"tipPesSac\":\"\",\"telDDD\":\"\",\"telefoneNove\":\"\",\"statusTitulo\":0,\"numAtrReenviaPagTitSpb\":0}}";
        testTranslator(positional, expected);
        Assertions.assertEquals("7", MessageTranslator.searchForAgency(positional));
    }

    @Test
    public void positionalWithLessSizeThenExpected() throws JsonProcessingException {
        String positional = "0003040995000007003B3C02210000030709950000070020241230000134801967000000000000172390021900000000000099000700707634530020241230202412300000000000765000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000765000000000000000000034195994400000765001090011438971178219448000002082227274000134801967000000000000";
        String expected = "{\"msgHdr\":{\"msgTrn\":\"000304\",\"msgPtaCodOri\":995,\"msgPstCodOri\":0,\"msgTerNum\":700,\"msgIde\":\"3B3C\",\"msgVer\":22100,\"msgIdcCpc\":false,\"msgIdcCtn\":false},\"cnlCod\":3,\"terTip\":7,\"msgPtaCodOri\":995,\"msgPtaCodDsn\":0,\"msgTerNum\":700,\"msgLogDat\":\"20241230\",\"msgSolNum\":134801967,\"msgNsuSolEsr\":0,\"msgIdcLimSaq\":true,\"msgTtrCod\":72390,\"msgLogNumPrt\":219,\"msgLogValPrtFepCmg\":{\"codigoAgenciaOrigem\":0,\"numeroContrato\":0,\"codReceita\":0,\"tipoRecolhimento\":99,\"codigoAgenciaConta\":7,\"numeroContaCorrente\":70763453,\"variacaoContaCredito\":0,\"numeroViaCartao\":0,\"dataVencimento\":\"20241230\",\"dataReferenciaLancamento\":\"20241230\",\"valorBaseDocumento\":\"0000000000765.00\",\"valorPagoMultaJuros\":\"0000000000000.00\",\"valorPagoJuros\":\"0000000000000.00\",\"valorPagoDinheiro\":\"0000000000000.00\",\"valorPagoAcrescimos\":\"0000000000000.00\",\"valorDescontos\":\"0000000000000.00\",\"valorAbatimentos\":\"0000000000000.00\",\"valorDebito\":\"0000000000765.00\",\"valorPagoCheque\":\"0000000000000.00\",\"numeroDiasBloqueioCheque\":0,\"codigoBarras\":\"34195994400000765001090011438971178219448000\",\"nsuAutCnsJdnpc\":2082227274,\"numeroAutorizacao\":134801967,\"numDoc\":0,\"complemento\":\"\",\"descricao\":\"\",\"numCmg\":\"\",\"nomeRazaoSocialCedente\":\"\",\"cpfCnpjCedente\":\"\",\"tipPesCed\":\"\",\"cpfCnpjSacado\":\"\",\"tipPesSac\":\"\",\"telDDD\":\"\",\"telefoneNove\":\"\",\"statusTitulo\":0,\"numAtrReenviaPagTitSpb\":0}}";
        String json = MessageTranslator.translateToJson(positional);
        assertEquals(expected, json);
        Assertions.assertEquals("7", MessageTranslator.searchForAgency(positional));
    }


    private void testTranslator(String originalPositional, String expected) throws JsonProcessingException {
        String json = MessageTranslator.translateToJson(originalPositional);
        assertEquals(expected, json);
        String convertedPositional = MessageTranslator.translateToSequence(json);
        assertEquals(originalPositional, convertedPositional);
    }

    private String createMsgConditional(String transactionCode, String agencyCode) {
        return "999999" + //layout
                transactionCode +//codigo de transacao
                agencyCode +//codigoAgencia
                "TRAMG" +//campo EstLogFake.campo1
                "01192";//campo EstLogFake.campo2
    }

    private String createMsgArray() {
        return "999998" + //layout
                "2" +//qnt campo itens
                "PND" +//status[0]
                "CON" +//status[1]
                "   " +//status[2]
                "1" +//qnt campo itens
                "01192" +//itens[0] EstItemArrayFake.campo1
                "     " +//itens[1] EstItemArrayFake.campo1
                "1" +//qnt campo itens
                "11552" +//sequencia[0]
                "     " +//sequencia[1]
                "END"; //endFile
    }

    private String createMsgObject(String agencyCode) {
        return "999997" + //layout
                "FINAL" +//obj1.campo1
                "20241224" +//data
                "P    " +//obj2.campo1
                "1" +//ativo
                "10099" +//valor
                agencyCode + "0025452" +//numeroConta
                "001";//msgCodRet
    }

    private String createMsgAgenciaConditional(String transactionCode, String agencyCode) {
        return "999996" + //layout
                transactionCode +//codigo de transacao
                "TRAMG" +//campo EstLogFake.campo1
                "01192" +
                agencyCode;//campo EstLogFake.campo2
    }

}



/*
TODO 304

17
97
105
106
107
108
118
123
129 -> OK
144
187
199
201
218
219 -> OK
221
225
226
228
230
253
259
262
268
*/