package br.com.bancoamazonia.gatelayer.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureTranslator {

    private final int messageMaxLength;
    private final String messageDescription;
    private final List<StructureInstruction> instructions;

    //Posição da agência se a mesma estiver na tripa incondicionalmente
    private AgencyPosition agencyPosition;

    //Posição da agência se a mesma estiver na partição condicional das tripas genéricas
    private Integer transactionCodeStartPosition;
    private Integer transactionCodeLength;
    private Map<String, AgencyPosition> conditionalAgencyPosition = new HashMap<>();

    public StructureTranslator(int messageMaxLength, String messageDescription) {
        this.messageMaxLength = messageMaxLength;
        this.messageDescription = messageDescription;
        this.instructions = new ArrayList<>();
    }

    public int getMessageMaxLength() {
        return messageMaxLength;
    }

    public String getMessageDescription() {
        return messageDescription;
    }

    public List<StructureInstruction> getInstructions() {
        return instructions;
    }

    public void setAgencyPosition(Integer startPosition, Integer agencyLength) {
        this.agencyPosition = new AgencyPosition(startPosition, agencyLength);
    }

    public Integer getAgencyStartPosition() {
        if (agencyPosition == null) {
            return 0;
        }
        return agencyPosition.getAgencyStartPosition();
    }

    public Integer getAgencyLength() {
        if (agencyPosition == null) {
            return 0;
        }
        return agencyPosition.getAgencyLength();
    }

    public void setTransactionCodePosition(Integer startPosition, Integer length) {
        this.transactionCodeStartPosition = startPosition;
        this.transactionCodeLength = length;
    }

    public void addConditionalAgencyPosition(String transactionCode, Integer startPosition, Integer agencyLength) {
        this.conditionalAgencyPosition.put(transactionCode, new AgencyPosition(startPosition, agencyLength));
    }

    public boolean hasAgencyPosition() {
        return agencyPosition != null;
    }

    public Integer getTransactionCodeStartPosition() {
        return transactionCodeStartPosition;
    }

    public Integer getTransactionCodeLength() {
        return transactionCodeLength;
    }

    public Map<String, AgencyPosition> getConditionalAgencyPosition() {
        return conditionalAgencyPosition;
    }

    public class AgencyPosition {

        private final Integer agencyStartPosition;
        private final Integer agencyLength;

        public AgencyPosition(Integer agencyStartPosition, Integer agencyLength) {
            this.agencyStartPosition = agencyStartPosition;
            this.agencyLength = agencyLength;
        }

        public Integer getAgencyStartPosition() {
            return agencyStartPosition;
        }

        public Integer getAgencyLength() {
            return agencyLength;
        }
    }
}
