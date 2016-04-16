package compiler;

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
    private Tables tables;
    private HashMap<String, Integer> identifiersTable;
    private HashMap<String, Integer> constTable;
    private Tree<Integer> parseTree;

    public SyntaxParser(ArrayList<Lexeme> resultArray, Tables tables) {
        this.resultArray = resultArray;
        this.tables = tables;
        constTable = new HashMap<>();
        identifiersTable = new HashMap<>(tables.getIdentifiersTable());
        parseTree = new Tree<>(SIGNAL_PROGRAM);
        for (String identifier : identifiersTable.keySet()) {
            identifiersTable.put(identifier, null);
        }
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
                    } else {
                        errorFlag = true;
                        break;
                    }
                    break;
                case 2:
                    if (resultArray.get(pointer).getLexCode() == 401) {
                        copyConstants();
                        address = 3;
                        pointer++;
                        blockNode.addChild(401);
                        currentNode = blockNode.addChild(STATEMENTS_LIST);
                        break;
                    }

                    if (resultArray.get(pointer).getLexCode() > 1000) {
                        buffer = findIdentifier(tables.getIdentifiersTable(),
                                resultArray.get(pointer).getLexCode());
                        if (buffer == null || identifiersTable.get(buffer) != null) {
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
                    if (resultArray.get(pointer).getLexCode() == 402) {
                        address = -1;
                        pointer++;
                        blockNode.addChild(402);
                        break;
                    }

                    if (resultArray.get(pointer).getLexCode() > 1000) {
                        buffer = findIdentifier(tables.getIdentifiersTable(),
                                resultArray.get(pointer).getLexCode());
                        if (buffer == null) {
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
                    if (resultArray.get(pointer).getLexCode() == 46)
                        if (resultArray.size() == pointer + 1) {
                            programEnd = true;
                            break;
                        }
                    errorFlag = true;
                    break;
            }
            if (errorFlag) {
                System.out.println("error in line " + resultArray.get(pointer).getStringIndex() +
                " and column " + resultArray.get(pointer).getCharIndex());
            }
        }

    }

    public boolean addConstToTable(int pointer, String buffer) {
        String constantBuffer;
        if (resultArray.get(pointer).getLexCode() > 500) {
            constantBuffer = findIdentifier(tables.getConstTable(),
                    resultArray.get(pointer).getLexCode());
            identifiersTable.put(buffer, Integer.parseInt(constantBuffer));
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
}
