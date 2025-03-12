package br.com.bancoamazonia.gatelayer.translate;

import br.com.bancoamazonia.gatelayer.translate.exception.PositionalBuildException;
import br.com.bancoamazonia.gatelayer.translate.exception.TranslatorLayoutNotFound;
import br.com.bancoamazonia.gatelayer.translate.exception.TypeConvertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(MessageTranslator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String PACKAGE_SCAN = "br.com.bancoamazonia.gatelayer.translate.structure";
    private static final int LAYOUT_SIZE = 6;
    private static final int AGENCY_ACCOUNT_SIZE = 3;

    private static final Map<String, StructureTranslator> translators = new HashMap<>();

    public static void startup() throws IOException, ClassNotFoundException {
        List<Class<?>> annotatedClasses = findAnnotatedClasses();
        for (Class<?> layoutClass : annotatedClasses) {
            Layout layoutAnnotation = layoutClass.getAnnotation(Layout.class);
            StructureTranslator structure = new StructureTranslator(
                    layoutAnnotation.messageLength(),
                    layoutAnnotation.description()
            );

            try {
                readFields(structure.getInstructions(), layoutClass, false);
                validateStructure(structure);

                for (String layoutCode : layoutAnnotation.value()) {
                    translators.put(layoutCode, structure);
                }
            } catch (PositionalBuildException e) {
                LOG.error(layoutAnnotation.value()[0] + "|" + e.getMessage(), e);
            }
        }
    }

    public static String searchForAgency(String message) {
        StructureTranslator structureTranslator = getStructureFromLayout(message);
        String stringAgency = null;
        if (structureTranslator.hasAgencyPosition()) {
            stringAgency = message.substring(structureTranslator.getAgencyStartPosition(),
                    structureTranslator.getAgencyStartPosition() + structureTranslator.getAgencyLength());
        } else if (structureTranslator.getTransactionCodeStartPosition() != null) {
            //busca em estruturas condicionais
            String transactionCode = message.substring(structureTranslator.getTransactionCodeStartPosition(),
                    structureTranslator.getTransactionCodeStartPosition() + structureTranslator.getTransactionCodeLength());
            StructureTranslator.AgencyPosition agencyPosition = structureTranslator.getConditionalAgencyPosition().get(String.valueOf(Integer.parseInt(transactionCode)));
            if (agencyPosition == null) {
                return null;
            }
            stringAgency = message.substring(agencyPosition.getAgencyStartPosition(),
                    agencyPosition.getAgencyStartPosition() + agencyPosition.getAgencyLength());
        }
        if (StringUtils.isEmpty(stringAgency)) {
            return null;
        }
        return String.valueOf(Integer.parseInt(stringAgency));
    }


    public static String translateToSequence(String json) throws JsonProcessingException {
        StringBuilder message = new StringBuilder();
        JsonNode root = MAPPER.readTree(json);

        String layoutCodeRegex = "\"msgTrn\":\"(.{6})\"";
        Pattern pattern = Pattern.compile(layoutCodeRegex);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String layoutCode = matcher.group(1);
            StructureTranslator structure = translators.get(layoutCode);
            if (structure == null) {
                throw new TranslatorLayoutNotFound(json);
            }
            try {
                buildMessage(message, structure.getInstructions(), root);
                return message.toString();
            } catch (Exception e) {
                LOG.error("Json message recived: {}", message);
                throw e;
            }
        } else {
            throw new TranslatorLayoutNotFound(json);
        }
    }

    public static String translateToJson(String message) {
        String layoutCode = message.substring(0, LAYOUT_SIZE);
        StructureTranslator structureTranslator = translators.get(layoutCode);
        if (structureTranslator == null) {
            throw new TranslatorLayoutNotFound(message);
        }
        ObjectNode rootNode = MAPPER.createObjectNode();
        try {
            buildJson(rootNode, structureTranslator.getInstructions(), 0, message);
            return rootNode.toString();
        } catch (Exception e) {
            LOG.error("Positional message recived: {}", message);
            throw e;
        }
    }

    private static void buildMessage(StringBuilder message, List<StructureInstruction> instructions, JsonNode node) {
        for (StructureInstruction instruction : instructions) {
            switch (instruction.getPositionalType()) {
                case PositionalField ignored -> {

                    boolean hasConditional = false;
                    if (StringUtils.isNoneEmpty(instruction.getTransactionFieldName())) {
                        JsonNode transactionCodeNode = node.get(instruction.getTransactionFieldName());
                        if (transactionCodeNode != null) {
                            String transactionCode = transactionCodeNode.asText();
                            List<StructureInstruction> conditionalChildrens = instruction.searchConditionalChildrens(transactionCode);
                            if (conditionalChildrens != null) {
                                hasConditional = true;
                                JsonNode chieldNode = node.get(instruction.getFieldName());
                                buildMessage(message, conditionalChildrens, chieldNode);
                            }
                        }
                    }

                    if (!hasConditional) {
                        convertAndAddPositional(message, node, instruction);
                    }
                }
                case PositionalArray ignored -> {
                    ArrayNode arrayNode = node.withArrayProperty(instruction.getFieldName());
                    Class<?> primitiveArray = isPrimitiveArray(instruction);
                    StringBuilder loopContent = new StringBuilder();
                    int iterationCount = 0;
                    for (JsonNode jsonNode : arrayNode) {
                        iterationCount++;
                        if (primitiveArray == null) {
                            buildMessage(loopContent, instruction.getChildrens(), jsonNode);
                        } else {
                            convertArrayAndAddPositional(loopContent, jsonNode, primitiveArray, instruction);
                        }
                    }
                    String messageField = StringUtils.leftPad(String.valueOf(iterationCount), instruction.getPositionalIterationSize(), '0');
                    message.append(messageField);
                    message.append(loopContent);
                    //blank space for non-existing iterations
                    int numEmptyIterations = instruction.getMaxIterations() - iterationCount;
                    if (numEmptyIterations > 0) {
                        message.append(
                                StringUtils.leftPad("", numEmptyIterations * instruction.getRowLength(), ' ')
                        );
                    }
                }
                case PositionalObject ignored -> {
                    JsonNode chieldNode = node.get(instruction.getFieldName());
                    buildMessage(message, instruction.getChildrens(), chieldNode);
                }
                case null, default ->
                        throw new PositionalBuildException("Positional annotation not expected: " + instruction.getPositionalType().getClass().getName());
            }
        }
    }

    private static void convertArrayAndAddPositional(StringBuilder message, JsonNode node, Class<?> primitiveClass, StructureInstruction instruction) {
        switch (primitiveClass.getSimpleName()) {
            case "String" -> {
                String jsonValue = node.asText();
                String messageField = StringUtils.rightPad(jsonValue, instruction.getRowLength(), ' ');
                message.append(messageField);
            }
            case "Integer", "Long" -> {
                String jsonValue = node.asText();
                String messageField = StringUtils.leftPad(jsonValue, instruction.getRowLength(), '0');
                message.append(messageField);
            }
            default ->
                    throw new TypeConvertException("convertAndAddNode not mapped! [" + instruction.getFieldName() + "]");
        }
    }

    private static void convertAndAddPositional(StringBuilder message, JsonNode node, StructureInstruction instruction) {
        switch (instruction.getType().getSimpleName()) {
            case "String" -> {
                String jsonValue = node.get(instruction.getFieldName()).asText();
                String messageField = StringUtils.rightPad(jsonValue, instruction.getLength(), ' ');
                message.append(messageField);
            }
            case "Integer", "Long" -> {
                String jsonValue = node.get(instruction.getFieldName()).asText();
                String messageField = StringUtils.leftPad(jsonValue, instruction.getLength(), '0');
                message.append(messageField);
            }
            case "Boolean" -> {
                boolean jsonValue = node.get(instruction.getFieldName()).asBoolean();
                message.append(jsonValue ? "1" : "0");
            }
            case "BigDecimal" -> {
                String jsonValue = node.get(instruction.getFieldName()).asText();
                String stringWithoutDecimal = jsonValue.replace(".", "");
                String messageField = StringUtils.leftPad(stringWithoutDecimal, instruction.getLength(), '0');
                message.append(messageField);
            }
            default ->
                    throw new TypeConvertException("convertAndAddNode not mapped! [" + instruction.getFieldName() + "]");
        }
    }

    private static int buildJson(ObjectNode node, List<StructureInstruction> instructions, int currentPosition, String message) {
        for (StructureInstruction instruction : instructions) {
            switch (instruction.getPositionalType()) {
                case PositionalField ignored -> {
                    boolean hasConditional = false;
                    if (StringUtils.isNotEmpty(instruction.getTransactionFieldName())) {
                        JsonNode transactionCodeNode = node.get(instruction.getTransactionFieldName());
                        if (transactionCodeNode != null) {
                            String transactionCode = transactionCodeNode.asText();
                            List<StructureInstruction> conditionalChildrens = instruction.searchConditionalChildrens(transactionCode);
                            if (conditionalChildrens != null) {
                                hasConditional = true;
                                ObjectNode newNode = node.putObject(instruction.getFieldName());
                                currentPosition = buildJson(newNode, conditionalChildrens, currentPosition, message);
                            }
                        }
                    }

                    if (!hasConditional) {
                        int limit = currentPosition + (instruction.getLength() + instruction.getPrecision());
                        if(limit > message.length()){
                            limit = message.length();
                        }
                        String positionStrip = message.substring(currentPosition, limit );
                        currentPosition = limit ;
                        convertAndAddNode(node, instruction.getFieldName(), instruction.getType(), instruction.getPrecision(), positionStrip);
                    }
                }
                case PositionalArray ignored -> {
                    ArrayNode arrayNode = node.putArray(instruction.getFieldName());
                    String positionStrip = message.substring(currentPosition, currentPosition += instruction.getPositionalIterationSize());
                    Integer nroIterations = Integer.parseInt(positionStrip);
                    Class<?> primitiveType = isPrimitiveArray(instruction);
                    for (int i = 0; i < nroIterations; i++) {
                        if (primitiveType == null) {
                            ObjectNode arrayItem = MAPPER.createObjectNode();
                            currentPosition = buildJson(arrayItem, instruction.getChildrens(), currentPosition, message);
                            arrayNode.add(arrayItem);
                        } else {
                            positionStrip = message.substring(currentPosition, currentPosition += instruction.getRowLength());
                            convertAndAddArray(arrayNode, positionStrip, primitiveType, instruction);
                        }
                    }
                    //jump cursor for next point
                    currentPosition += (instruction.getMaxIterations() - nroIterations) * instruction.getRowLength();
                }
                case PositionalObject ignored -> {
                    ObjectNode newNode = node.putObject(instruction.getFieldName());
                    currentPosition = buildJson(newNode, instruction.getChildrens(), currentPosition, message);
                }
                case null, default ->
                        throw new PositionalBuildException("Positional annotation not expected: " + instruction.getPositionalType().getClass().getName());
            }
        }
        return currentPosition;
    }

    private static void convertAndAddArray(ArrayNode arrayNode, String positionStrip, Class<?> type, StructureInstruction instruction) {
        switch (type.getSimpleName()) {
            case "String" -> arrayNode.add(positionStrip.trim());
            case "Integer" -> arrayNode.add(Integer.parseInt(positionStrip));
            default ->
                    throw new TypeConvertException("convertAndAddNode not mapped! [" + instruction.getFieldName() + "]");
        }
    }


    private static Class<?> isPrimitiveArray(StructureInstruction instruction) {
        Class<?> componentType = instruction.getType().getComponentType();
        if (componentType != null) {
            if (componentType == Integer.class
                    || componentType == String.class) {
                return componentType;
            }
        }
        return null;
    }

    private static void convertAndAddNode(ObjectNode node, String fieldName, Class<?> type, Integer precision, String positionStrip) {
        switch (type.getSimpleName()) {
            case "String" -> node.put(fieldName, positionStrip.trim());
            case "Integer" -> node.put(fieldName, Integer.parseInt(convertEmptyToZero(positionStrip)));
            case "Long" -> node.put(fieldName, Long.parseLong(convertEmptyToZero(positionStrip)));
            case "Boolean" -> node.put(fieldName, "1".equals(positionStrip) ? Boolean.TRUE : Boolean.FALSE);
            case "BigDecimal" -> {
                StringBuilder sb = new StringBuilder(positionStrip);
                sb.insert(positionStrip.length() - precision, ".");
                node.put(fieldName, sb.toString());
            }
            default -> throw new TypeConvertException("convertAndAddNode not mapped! [" + fieldName + "]");
        }
    }

    private static String convertEmptyToZero(String positionStrip) {
        if (StringUtils.isEmpty(positionStrip)) {
            return "0";
        }
        return positionStrip;
    }


    private static void readFields(List<StructureInstruction> instructions, Class<?> clazz, boolean hasAgency) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(PositionalObject.class)) {
                PositionalObject positionalObject = field.getAnnotation(PositionalObject.class);
                String description = positionalObject.description();
                StructureInstruction instruction = new StructureInstruction(positionalObject, field.getName(), field.getType(), description);
                instructions.add(instruction);
                readFields(instruction.getChildrens(), field.getType(), hasAgency);

            } else if (field.isAnnotationPresent(PositionalArray.class)) {
                PositionalArray positionalArray = field.getAnnotation(PositionalArray.class);
                String description = positionalArray.description();
                Integer maxIterations = positionalArray.maxIterations();
                Integer positionalIterationSize = positionalArray.positionalIterationSize();
                Integer rowLength = positionalArray.rowLength();

                StructureInstruction instruction = new StructureInstruction(positionalArray, field.getName(), field.getType(),
                        positionalIterationSize, maxIterations, rowLength, description);
                instructions.add(instruction);
                readFields(instruction.getChildrens(), field.getType().componentType(), hasAgency);

            } else if (field.isAnnotationPresent(PositionalField.class)) {
                PositionalField positionalField = field.getAnnotation(PositionalField.class);
                Integer length = positionalField.length();
                Integer precision = positionalField.precision();
                String description = positionalField.description();
                StructureInstruction instruction = null;
                if (field.isAnnotationPresent(AgencyCode.class)) {
                    if (hasAgency) {
                        throw new PositionalBuildException("Already have a field annotated with @AgencyCode for this layout");
                    }
                    AgencyCode agencyCode = field.getAnnotation(AgencyCode.class);
                    instruction = new StructureInstruction(positionalField, field.getName(), field.getType(), length, precision, description, agencyCode.insideAccount());
                } else {
                    instruction = new StructureInstruction(positionalField, field.getName(), field.getType(), length, precision, description);
                }

                ConditionalObject[] conditionalObjects = positionalField.conditionalObjects();
                if (conditionalObjects.length != 0) {
                    String transactionFieldName = positionalField.transactionFieldName();
                    for (ConditionalObject conditionalObject : conditionalObjects) {
                        List<StructureInstruction> conditionalChildren = new ArrayList<>();
                        readFields(conditionalChildren, conditionalObject.type(), hasAgency);
                        for (String transactionCode : conditionalObject.transactionLayout()) {
                            instruction.addConditionalChildren(transactionCode, conditionalChildren);
                        }
                    }
                    instruction.setTransactionFieldName(transactionFieldName);
                }

                instructions.add(instruction);
            }
        }
    }


    private static void validateStructure(StructureTranslator structure) {
        int count = 0;
        for (StructureInstruction instruction : structure.getInstructions()) {
            count = countChildren(instruction, count, structure);
            if (instruction.getHasAgencyInformation()) {
                if (instruction.getHasAgencyInformationInsideAccount()) {
                    structure.setAgencyPosition(count, AGENCY_ACCOUNT_SIZE);
                } else {
                    structure.setAgencyPosition(count, instruction.getLength());
                }
            }
            if (instruction.getLength() != null) {
                count += instruction.getLength();
            }
        }
    }

    private static int countChildren(StructureInstruction instruction, int count, StructureTranslator structure) {
        for (StructureInstruction children : instruction.getChildrens()) {
            if (children.getLength() != null) {
                count += children.getLength();
            }
            if (children.getHasAgencyInformation()) {
                structure.setAgencyPosition(count, children.getLength());
            }
            if (!children.getChildrens().isEmpty()) {
                for (StructureInstruction granchildren : children.getChildrens()) {
                    count = countChildren(granchildren, count, structure);
                }
            }
        }

        if (!structure.hasAgencyPosition()
                && !instruction.getConditionalChildrens().isEmpty()) {
            String transactionFieldName = instruction.getTransactionFieldName();
            StructureInstruction positionalInstruction = searchPositionalByName(transactionFieldName, structure.getInstructions());
            if (positionalInstruction == null) {
                throw new PositionalBuildException("TransactionFieldName not found!");
            }
            int cursorPoint = findCursorForInstruction(positionalInstruction, 0, structure.getInstructions());
            structure.setTransactionCodePosition(cursorPoint, positionalInstruction.getLength());
            for (String transactionLayout : instruction.getConditionalChildrens().keySet()) {
                int conditionalCount = count;
                List<StructureInstruction> conditionalInstructions = instruction.getConditionalChildrens().get(transactionLayout);
                for (StructureInstruction conditionalInstruction : conditionalInstructions) {
                    if (conditionalInstruction.getHasAgencyInformation()) {
                        structure.addConditionalAgencyPosition(transactionLayout, conditionalCount, conditionalInstruction.getLength());
                    }
                    if (conditionalInstruction.getLength() != null) {
                        conditionalCount += conditionalInstruction.getLength();
                    }
                    if (!conditionalInstruction.getChildrens().isEmpty()) {
                        for (StructureInstruction granchildren : conditionalInstruction.getChildrens()) {
                            conditionalCount = countChildren(granchildren, conditionalCount, structure);
                        }
                    }
                }
            }
        }

        return count;
    }

    private static int findCursorForInstruction(StructureInstruction instructionToFind, int cursor, List<StructureInstruction> instructions) {
        for (StructureInstruction instruction : instructions) {
            if (instructionToFind.getFieldName().equals(instruction.getFieldName())) {
                break;
            } else if (!instruction.getChildrens().isEmpty()) {
                cursor = findCursorForInstruction(instructionToFind, cursor, instruction.getChildrens());
            } else {
                cursor += instruction.getLength();
            }
        }
        return cursor;
    }

    private static StructureInstruction searchPositionalByName(String transactionFieldName, List<StructureInstruction> instructions) {
        for (StructureInstruction instruction : instructions) {
            if (transactionFieldName.equals(instruction.getFieldName())) {
                return instruction;
            } else if (!instruction.getChildrens().isEmpty()) {
                StructureInstruction resultInstruction = searchPositionalByName(transactionFieldName, instruction.getChildrens());
                if (resultInstruction != null) {
                    return resultInstruction;
                }
            }
        }
        return null;
    }

    private static List<Class<?>> findAnnotatedClasses() throws IOException, ClassNotFoundException {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        String packagePath = PACKAGE_SCAN.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            if (directory.isDirectory()) {
                for (File file : directory.listFiles()) {
                    if (file.getName().endsWith(".class")) {
                        String className = PACKAGE_SCAN + '.' + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(Layout.class)) {
                            annotatedClasses.add(clazz);
                        }
                    }
                }
            } else if ("jar".equals(resource.getProtocol())) {
                if (resource.getPath().contains("gatelayer-legacy-banklink-mq")) {
                    //rodando dentro de um jar (build)
                    String fullPath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
                    String adapterJarFile = fullPath.substring("nested:".length(), fullPath.indexOf("!"));
                    try (JarFile jarFile = new JarFile(adapterJarFile)) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String entryName = entry.getName();
                            if (entryName.contains("gatelayer-legacy-core")) {
                                try (InputStream is = jarFile.getInputStream(entry)) {
                                    File tempJar = File.createTempFile("internal-", ".jar");
                                    tempJar.deleteOnExit();

                                    // Copiar o conteúdo do InputStream para o arquivo temporário
                                    try (FileOutputStream fos = new FileOutputStream(tempJar)) {
                                        byte[] buffer = new byte[1024];
                                        int bytesRead;
                                        while ((bytesRead = is.read(buffer)) != -1) {
                                            fos.write(buffer, 0, bytesRead);
                                        }
                                    }

                                    try (JarFile internalJar = new JarFile(tempJar)) {
                                        coreJar(internalJar, packagePath, annotatedClasses);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //rodando dentro local com o core como lib
                    try (JarFile jarFile = new JarFile(extractJarPathFromFile(resource))) {
                        coreJar(jarFile, packagePath, annotatedClasses);
                    }
                }
            }
        }

        return annotatedClasses;
    }

    private static void coreJar(JarFile jarFile, String packagePath, List<Class<?>> annotatedClasses) throws IOException, ClassNotFoundException {
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                String className = entryName.replace('/', '.').replace(".class", "");
                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(Layout.class)) {
                    annotatedClasses.add(clazz);
                }
            }
        }
    }

    private static String extractJarPathFromFile(URL resource) throws IOException {
        String fullPath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
        return fullPath.substring("file:".length(), fullPath.lastIndexOf("!"));
    }

    private static StructureTranslator getStructureFromLayout(String message) {
        String layoutCode = message.substring(0, LAYOUT_SIZE);
        return translators.get(layoutCode);
    }
}
