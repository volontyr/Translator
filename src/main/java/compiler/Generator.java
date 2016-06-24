package compiler;

import java.io.*;

/**
 * Created by santos on 5/21/16.
 */
public class Generator {
    private final int BLOCK = -3;
    private final int DECLARATIONS = -4;
    private final int STATEMENTS_LIST = -10;
    private final int VARIABLE_DECLARATIONS_LIST = -16;

    private Tree<Integer> tree;
    private Tables tables;

    public Generator(Tree<Integer> tree, Tables tables) {
        this.tree = tree;
        this.tables = tables;
    }

    public void generate() throws IOException {
        Tree<Integer> block = null;
        Tree<Integer> constants = null;
        Tree<Integer> statements = null;
        Tree<Integer> variable = null;
        String tempStr = "";
        int lexemeCode;
        boolean data_segment_start = false;

        File fout = new File("/home/santos/IdeaProjects/Translator/src/main/resources/result.asm");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter file = new BufferedWriter(new OutputStreamWriter(fos));

        if (!tree.isLeaf() && !tree.getChildren().get(0).isLeaf())
           for(Tree<Integer> node : tree.getChildren().get(0).getChildren()) {
               if (node.getData() == BLOCK) {
                   block = node;
                   break;
               }
           }

        if (block != null && !block.isLeaf()) {
            for (Tree<Integer> node : block.getChildren()) {
                if (node.getData() == DECLARATIONS && !node.isLeaf()) {
                    if (!data_segment_start) {
                        file.write("Data Segment");
                        file.newLine();
                        data_segment_start = true;
                    }
                    data_segment_start = true;
//                    if (!node.getChildren().get(0).getChildren().get(1).isLeaf())
                    constants = node.getChildren().get(0).getChildren().get(1);
                }

                if (node.getData() == VARIABLE_DECLARATIONS_LIST && !node.isLeaf()) {
                    if (!data_segment_start) {
                        file.write("Data Segment");
                        file.newLine();
                        data_segment_start = true;
                    }
                    variable = node.getChildren().get(0);
                }

                if (node.getData() == STATEMENTS_LIST && !node.isLeaf()) {
                    statements = node;
                }
            }

            if (variable != null && !variable.isLeaf()) {

                lexemeCode = variable.getChildren().get(1).getChildren().get(0)
                        .getChildren().get(0).getData();
                for (String key : tables.getIdentifiersTable().keySet()) {
                    if (tables.getIdentifiersTable().get(key).equals(lexemeCode)) {
                        tempStr = key;
                        break;
                    }
                }
                lexemeCode = variable.getChildren().get(3).getChildren().get(0).getData();
                if (lexemeCode == 406)
                    tempStr += " dd ?";
                else if (lexemeCode == 407)
                    tempStr += " db ?";
                file.write(tempStr);
                file.newLine();
            }

            if (constants != null) {
                if (!constants.isLeaf()) {
                    for (Tree<Integer> node : constants.getChildren()) {
                        lexemeCode = node.getChildren().get(0).getChildren().get(0)
                                .getChildren().get(0).getData();
                        for (String key : tables.getIdentifiersTable().keySet()) {
                            if (tables.getIdentifiersTable().get(key).equals(lexemeCode)) {
                                tempStr = key + " dd ";
                                break;
                            }
                        }
                        lexemeCode = node.getChildren().get(2).getChildren().get(0)
                                .getChildren().get(0).getData();
                        for (String key : tables.getConstTable().keySet()) {
                            if (tables.getConstTable().get(key).equals(lexemeCode)) {
                                tempStr += key;
                                double num = Double.parseDouble(key);
                                if (num < -Math.pow(2, 31) || num > Math.pow(2, 31) - 1)
                                    file.write("Error: integer type of constant expected\n");
                                break;
                            }
                        }
                        file.write(tempStr);
                        file.newLine();
                    }

                }
            }

            if (data_segment_start) {
                file.write("Data ends");
                file.newLine();
            }

            if (statements != null && !statements.isLeaf()) {
                file.write("Code segment\nstart:\n");
                for (Tree<Integer> node : statements.getChildren()) {
                    lexemeCode = node.getChildren().get(0).getChildren().get(0)
                            .getChildren().get(0).getData();
                    for (String key : tables.getIdentifiersTable().keySet()) {
                        if (tables.getIdentifiersTable().get(key).equals(lexemeCode)) {
                            tempStr = "\tmov " + key + ", ";
                            break;
                        }
                    }
                    lexemeCode = node.getChildren().get(2).getChildren().get(0)
                            .getChildren().get(0).getData();
                    for (String key : tables.getConstTable().keySet()) {
                        if (tables.getConstTable().get(key).equals(lexemeCode)) {
                            tempStr += key;
                            double num = Double.parseDouble(key);
                            if (num < -Math.pow(2, 31) || num > Math.pow(2, 31) - 1)
                                file.write("\tError:  integer type of constant expected\n");
                            break;
                        }
                    }
                    file.write(tempStr);
                    file.newLine();
                }
                file.write("Code ends\nend start");
            }
        }

        file.close();
    }
}
