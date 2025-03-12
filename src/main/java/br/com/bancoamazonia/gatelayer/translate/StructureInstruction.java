package br.com.bancoamazonia.gatelayer.translate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO quando parar de aparecer coisa, quebrar em classes menores
public class StructureInstruction {
    //Unico campo que é sempre preenchido
    private final Annotation positionalType;
    private final String fieldName;
    private final Class<?> type;

    //Informações para quando o objeto é um array
    private Integer positionalIterationSize;
    private Integer maxIterations;
    private Integer rowLength;

    //Nesse objeto ou teremos um registro na lista
    private final List<StructureInstruction> childrens = new ArrayList<>();

    //Conditional objects
    private String transactionFieldName;
    private final Map<String, List<StructureInstruction>> conditionalChildrens = new HashMap<>();

    //OU os campos de construção
    private Integer length;
    private Integer precision;
    private final String description;
    private Boolean hasAgencyInformation = false;
    private Boolean hasAgencyInformationInsideAccount = false;

    public StructureInstruction(Annotation positionalType, String fieldName, Class<?> type, String description) {
        this.positionalType = positionalType;
        this.fieldName = fieldName;
        this.type = type;
        this.description = description;
    }

    public StructureInstruction(Annotation positionalType, String fieldName, Class<?> type, Integer length, Integer precision, String description, Boolean hasAgencyInformationInsideAccount) {
        this.positionalType = positionalType;
        this.fieldName = fieldName;
        this.type = type;
        this.length = length;
        this.precision = precision;
        this.description = description;
        this.hasAgencyInformation = true;
        this.hasAgencyInformationInsideAccount = hasAgencyInformationInsideAccount;
    }

    public StructureInstruction(Annotation positionalType, String fieldName, Class<?> type, Integer length, Integer precision, String description) {
        this.positionalType = positionalType;
        this.fieldName = fieldName;
        this.type = type;
        this.length = length;
        this.precision = precision;
        this.description = description;
    }

    public StructureInstruction(Annotation positionalType, String fieldName, Class<?> type, Integer positionalIterationSize, Integer maxIterations, Integer rowLength, String description) {
        this.positionalType = positionalType;
        this.fieldName = fieldName;
        this.type = type;
        this.positionalIterationSize = positionalIterationSize;
        this.maxIterations = maxIterations;
        this.rowLength = rowLength;
        this.description = description;
    }

    public void addConditionalChildren(String transactionLayout,
                                       List<StructureInstruction> conditionalChildren) {
        conditionalChildrens.put(transactionLayout, conditionalChildren);
    }


    public List<StructureInstruction> searchConditionalChildrens(String transactionLayout) {
        return conditionalChildrens.get(transactionLayout);
    }

    public Map<String, List<StructureInstruction>> getConditionalChildrens() {
        return conditionalChildrens;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getType() {
        return type;
    }

    public Integer getPositionalIterationSize() {
        return positionalIterationSize;
    }

    public Integer getMaxIterations() {
        return maxIterations;
    }

    public Integer getRowLength() {
        return rowLength;
    }

    public List<StructureInstruction> getChildrens() {
        return childrens;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getHasAgencyInformation() {
        return hasAgencyInformation;
    }

    public Boolean getHasAgencyInformationInsideAccount() {
        return hasAgencyInformationInsideAccount;
    }

    public String getTransactionFieldName() {
        return transactionFieldName;
    }

    public void setTransactionFieldName(String transactionFieldName) {
        this.transactionFieldName = transactionFieldName;
    }

    public Annotation getPositionalType() {
        return positionalType;
    }
}
