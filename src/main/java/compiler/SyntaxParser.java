package compiler;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by santos on 4/16/16.
 */
public class SyntaxParser implements Parser {

    private final int SIGNAL_PROGRAM = -1;
    private final int PROGRAM = -2;
    private final int BLOCK = -3;
    private final int DECLARATIONS = -4;
    private final int CONSTANT_DECLARATIONS = -5;
    private final int CONSTANT_DECLARATIONS_LIST = -6;
    private final int CONSTANT_DECLARATION = -7;
    private final int CONSTANT_IDENTIFIER = -8;
    private final int CONSTANT = -9;
    private final int STATEMENTS_LIST = -10;
    private final int STATEMENT = -11;
    private final int VARIABLE_IDENTIFIER = -12;
    private final int PROCEDURE_IDENTIFIER = -13;
    private final int IDENTIFIER = -14;
    private final int UNSIGNED_INTEGER = -15;

    private ArrayList<Lexeme> resultArray;
    private ArrayList<String> errors;
    private Tables tables;
    private HashMap<String, Integer> identifiersTable;
    private HashMap<String, Integer> constTable;
    private Tree<Integer> parseTree;
    private String fileXML;

    public SyntaxParser(ArrayList<Lexeme> resultArray, Tables tables) {
        this.resultArray = resultArray;
        this.tables = tables;
        this.fileXML = "/home/santos/IdeaProjects/Translator/src/main/webapp/resources/tree.xml";
        errors = new ArrayList<>();
        constTable = new HashMap<>();
        identifiersTable = new HashMap<>(tables.getIdentifiersTable());
        parseTree = new Tree<>(SIGNAL_PROGRAM);
        for (String identifier : identifiersTable.keySet()) {
            identifiersTable.put(identifier, null);
        }
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public HashMap<String, Integer> getConstTable() {
        return constTable;
    }

    public HashMap<String, Integer> getIdentifiersTable() {
        return identifiersTable;
    }

    public Tree<Integer> getParseTree() {
        return parseTree;
    }

    @Override
    public void parser() {
        int pointer = 0;
        int address = 0;
        boolean errorFlag = false, programEnd = false;
        String buffer;
        Tree<Integer> currentNode = null;
        Tree<Integer> blockNode = null;

        while (!errorFlag && !programEnd) {
            switch (address) {
                case 0:
                    if (!hasNext(pointer + 2)) {
                        errorFlag = true;
                        break;
                    }
                    if (resultArray.get(pointer).getLexCode() == 403) {
                        pointer++;
                        currentNode = parseTree.addChild(PROGRAM);
                        currentNode.addChild(403);
                    } else {
                        errorFlag = true;
                        break;
                    }
                    if (resultArray.get(pointer).getLexCode() > 1000) {
                        buffer = findIdentifier(tables.getIdentifiersTable(),
                                                resultArray.get(pointer).getLexCode());
                        identifiersTable.remove(buffer);
                        currentNode.addChild(PROCEDURE_IDENTIFIER)
                               .addChild(IDENTIFIER)
                               .addChild(resultArray.get(pointer).getLexCode());

                        pointer++;
                    } else {
                        errorFlag = true;
                        break;
                    }
                    if (resultArray.get(pointer).getLexCode() == 59) {
                        pointer++;
                        address = 1;
                    } else {
                        errorFlag = true;
                        break;
                    }
                    break;
                case 1:
                    if (!hasNext(pointer + 1)) {
                        errorFlag = true;
                        break;
                    }

                    currentNode = currentNode.addChild(BLOCK);
                    blockNode = currentNode;

                    if (resultArray.get(pointer).getLexCode() == 404) {
                        address = 2;
                        pointer++;
                        currentNode = currentNode.addChild(DECLARATIONS)
                                                .addChild(CONSTANT_DECLARATIONS)
                                                .addChild(404);
                        currentNode = currentNode.getParent().addChild(CONSTANT_DECLARATIONS_LIST);
                    } else if (resultArray.get(pointer).getLexCode() == 401) {
                        address = 3;
                        pointer++;
                        blockNode.addChild(401);
                        currentNode = blockNode.addChild(STATEMENTS_LIST);
                    } else {
                        errorFlag = true;
                        break;
                    }
                    break;
                case 2:
                    if (hasNext(pointer) && resultArray.get(pointer).getLexCode() == 401) {
                        copyConstants();
                        address = 3;
                        pointer++;
                        blockNode.addChild(401);
                        currentNode = blockNode.addChild(STATEMENTS_LIST);
                        break;
                    }
                    if (!hasNext(pointer + 3)) {
                        errorFlag = true;
                        break;
                    }
                    if (resultArray.get(pointer).getLexCode() > 1000) {
                        buffer = findIdentifier(tables.getIdentifiersTable(),
                                resultArray.get(pointer).getLexCode());
                        if (buffer == null || identifiersTable.get(buffer) != null ||
                                !identifiersTable.containsKey(buffer)) {
                            errorFlag = true;
                            break;
                        }
                        pointer++;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    if (resultArray.get(pointer).getLexCode() == 61) {
                        pointer++;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    if (addConstToTable(pointer, buffer)) {
                        pointer++;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    if (resultArray.get(pointer).getLexCode() == 59) {
                        currentNode = currentNode.addChild(CONSTANT_DECLARATION);
                        currentNode.addChild(CONSTANT_IDENTIFIER)
                                .addChild(IDENTIFIER)
                                .addChild(resultArray.get(pointer-3).getLexCode());
                        currentNode.addChild(61);
                        currentNode.addChild(CONSTANT)
                                .addChild(UNSIGNED_INTEGER)
                                .addChild(resultArray.get(pointer-1).getLexCode());
                        currentNode = currentNode.getParent();

                        pointer++;
                        address = 2;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    break;
                case 3:
                    if (hasNext(pointer) && resultArray.get(pointer).getLexCode() == 402) {
                        address = -1;
                        pointer++;
                        blockNode.addChild(402);
                        break;
                    }
                    if (!hasNext(pointer + 3)) {
                        errorFlag = true;
                        break;
                    }
                    if (resultArray.get(pointer).getLexCode() > 1000) {
                        buffer = findIdentifier(tables.getIdentifiersTable(),
                                resultArray.get(pointer).getLexCode());
                        if (buffer == null || !identifiersTable.containsKey(buffer)) {
                            errorFlag = true;
                            break;
                        }
                        pointer++;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    if (resultArray.get(pointer).getLexCode() == 301) {
                        pointer++;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    if (addConstToTable(pointer, buffer)) {
                        pointer++;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    if (resultArray.get(pointer).getLexCode() == 59) {
                        currentNode = currentNode.addChild(STATEMENT);
                        currentNode.addChild(VARIABLE_IDENTIFIER)
                                .addChild(IDENTIFIER)
                                .addChild(resultArray.get(pointer-3).getLexCode());
                        currentNode.addChild(301);
                        currentNode.addChild(CONSTANT)
                                .addChild(UNSIGNED_INTEGER)
                                .addChild(resultArray.get(pointer-1).getLexCode());
                        currentNode = currentNode.getParent();

                        pointer++;
                        address = 3;
                    } else {
                        errorFlag = true;
                        break;
                    }

                    break;
                default:
                    if (hasNext(pointer) && resultArray.get(pointer).getLexCode() == 46)
                        if (!hasNext(pointer + 1)) {
                            programEnd = true;
                            break;
                        }
                    errorFlag = true;
                    break;
            }
            if (errorFlag) {
                if (!hasNext(pointer))
                    pointer--;
                errors.add("error in line " + resultArray.get(pointer).getStringIndex() +
                            " and column " + resultArray.get(pointer).getCharIndex());
            }
        }
        if (errors.size() == 0) buildXMLTree();
    }

    public boolean hasNext(int index) {
        return resultArray.size() > index;
    }

    public boolean addConstToTable(int pointer, String buffer) {
        String constantBuffer;
        if (resultArray.get(pointer).getLexCode() > 500) {
            constantBuffer = findIdentifier(tables.getConstTable(),
                    resultArray.get(pointer).getLexCode());
            int num = (int) Double.parseDouble(constantBuffer);
            identifiersTable.put(buffer, num);
            return true;
        }
        return false;
    }

    public String findIdentifier(HashMap<String, Integer> table, Integer value) {
        for (String key : table.keySet()) {
            if (table.get(key).equals(value))
                return key;
        }
        return null;
    }

    public void copyConstants() {
        ArrayList<String> keys = new ArrayList<>();

        for (String key : identifiersTable.keySet()) {
            if (identifiersTable.get(key) != null) {
                constTable.put(key, identifiersTable.get(key));
                keys.add(key);
            }
        }
        for (String key : keys)
            identifiersTable.remove(key);
    }

    public void buildXMLTree() {
        Document dom;
        Element e;
        Tree<Integer> currentNode;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

            // create the root element
            Element rootElem = dom.createElement("non-terminal");
            rootElem.setAttribute("name", "SIGNAL-PROGRAM");
            e = rootElem;

            if (!parseTree.isLeaf()) {
                currentNode = parseTree.getChildren().get(0);
                parseTree(currentNode, rootElem, dom);
            }

            dom.appendChild(e);

            try (FileOutputStream xmlFile = new FileOutputStream(fileXML)) {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");

                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(xmlFile));
            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    public void parseTree(Tree<Integer> currentNode, Element rootElem, Document dom) {
        String textNode = "";
        Element e;
        if (currentNode.getData() < 0) {
            e = dom.createElement("non-terminal");
        }
        else {
            e = dom.createElement("terminal");
        }
        switch (currentNode.getData()) {
            case -2:
                textNode = "PROGRAM";
                break;
            case -3:
                textNode = "BLOCK";
                break;
            case -4:
                textNode = "DECLARATIONS";
                break;
            case -5:
                textNode = "CONSTANT-DECLARATIONS";
                break;
            case -6:
                textNode = "CONSTANT-DECLARATIONS-LIST";
                break;
            case -7:
                textNode = "CONSTANT-DECLARATION";
                break;
            case -8:
                textNode = "CONSTANT-IDENTIFIER";
                break;
            case -9:
                textNode = "CONSTANT";
                break;
            case -10:
                textNode = "STATEMENTS-LIST";
                break;
            case -11:
                textNode = "STATEMENT";
                break;
            case -12:
                textNode = "VARIABLE-IDENTIFIER";
                break;
            case -13:
                textNode = "PROCEDURE-IDENTIFIER";
                break;
            case -14:
                textNode = "IDENTIFIER";
                break;
            case -15:
                textNode = "INTEGER";
                break;
            default:
                if (currentNode.getData() > 1000) {
                    for (String key : tables.getIdentifiersTable().keySet()) {
                        if (tables.getIdentifiersTable().get(key).equals(currentNode.getData()))
                            textNode = key;
                    }
                } else if (currentNode.getData() > 500) {
                    for (String key : tables.getConstTable().keySet()) {
                        if (tables.getConstTable().get(key).equals(currentNode.getData()))
                            textNode = key;
                    }
                } else {
                    for (String key : tables.getKeyWords().keySet()) {
                        if (tables.getKeyWords().get(key).equals(currentNode.getData()))
                            textNode = key;
                    }
                }
                textNode += " (" + currentNode.getData().toString() + ")";
                break;
        }
        e.setAttribute("name", textNode);
        rootElem.appendChild(e);

        if (!currentNode.isLeaf()) {
            rootElem = e;
            for (Tree<Integer> node : currentNode.getChildren()) {
                parseTree(node, rootElem, dom);
            }
        }
    }
}
