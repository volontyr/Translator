package compiler;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by santos on 28.02.16.
 */
public class LexicalParser implements Parser {
    private InputStreamReader file;
    private String[] keyWords = {"BEGIN", "END", "PROGRAM", "CONST"};
    private String[] multiCharDelimiters = {":=", "<=", ">=", "<>"};
    private ArrayList<Integer> lexCodesResultArray;
    private int charIndex;
    private Tables tables;


    public LexicalParser(InputStream file) {
        this.file = new InputStreamReader(file);
        tables = new Tables(keyWords, multiCharDelimiters);
        lexCodesResultArray = new ArrayList<>();
    }

    public void addLexCode(int lexCode) {
        if (lexCode != 0)
            lexCodesResultArray.add(lexCode);
    }

    public Tables getTables() {
        return tables;
    }

    public ArrayList<Integer> getLexCodesResultArray() {
        return lexCodesResultArray;
    }

    @Override
    public void parser() {
        String currentStr;

        try (BufferedReader reader = new BufferedReader(file)) {

            while ((currentStr = reader.readLine()) != null) {
                charIndex = 0;
                while (charIndex < currentStr.length()) {
                    switch (tables.getAttribute(currentStr.charAt(charIndex))) {
                        case 0:
                            charIndex = whitespaceHandling(currentStr, charIndex);
                            break;
                        case 1:
                            charIndex = constHandling(currentStr, charIndex);
                            break;
                        case 2:
                            charIndex = identifierHandling(currentStr, charIndex);
                            break;
                        case 3:
                            currentStr = commentHandling(reader, currentStr + "\n", charIndex);
                            charIndex = 0;
                            break;
                        case 4:
                            charIndex = checkForMultiDelimiter(currentStr, charIndex);
                            break;
                        case 5:
                            charIndex++;
                            addLexCode(-1);
                            showError("Illegal symbol");

                        default:
                            break;
                    }

                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public int whitespaceHandling(String str, int index) {
        while (++index < str.length() &&
                tables.getAttribute(str.charAt(index)) == 0);
        return index;
    }

    public int constHandling(String str, int index) {
        String buffer = "";
        int lexCode;

        buffer += str.charAt(index);
        while (++index < str.length() &&
                tables.getAttribute(str.charAt(index)) == 1) {
            buffer += str.charAt(index);
        }

        if (tables.constTableSearch(buffer))
            lexCode = tables.getConstCode(buffer);
        else
            lexCode = tables.constTableAdd(buffer);

        addLexCode(lexCode);

        return index;
    }

    public int identifierHandling(String str, int index) {
        String buffer = "";
        int lexCode;

        buffer += str.charAt(index);

        while (++index < str.length() &&
                (tables.getAttribute(str.charAt(index)) == 2 ||
                tables.getAttribute(str.charAt(index)) == 1)) {
            buffer += str.charAt(index);
        }

        buffer = buffer.toUpperCase();

        if (tables.keyTableSearch(buffer))
            lexCode = tables.getKeyWordCode(buffer);
        else
            if (tables.identifierTableSearch(buffer))
                lexCode = tables.getIdentifierCode(buffer);
            else
                lexCode = tables.identifierTableAdd(buffer);

        addLexCode(lexCode);

        return index;
    }

    public String commentHandling(BufferedReader r, String str, int index) {
        String restOfString = "", errMessage = "";

        if (index + 1 == str.length())
            addLexCode((int) str.charAt(index));
        else
            if (str.charAt(++index) == '*') {
                ++index;
                do {
                    while (index < str.length() && str.charAt(index) != '*') ++index;
                    if (index >= str.length())
                        try {
                            while ((str = r.readLine() + "\n").equals("\n"));
                            if (str.equals("null\n")) {
                                addLexCode(-1);
                                errMessage = "*) expected but end of file found";
                                break;
                            } else {
                                index = 0;
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                } while (str.charAt(index) != '*' || str.charAt(++index) != ')');
            } else {
                index--;
                addLexCode((int) str.charAt(index));
            }

        if (str != null && ++index < str.length())
            restOfString = str.substring(index, str.length());

        if (!errMessage.equals(""))
            showError(errMessage);

        return restOfString;
    }

    public int checkForMultiDelimiter(String str, int index) {
        String buffer = "";
        int lexCode;

        buffer += str.charAt(index);
        if (++index < str.length())
            buffer += str.charAt(index);

        if (tables.multiDelimiterSearch(buffer)) {
            lexCode = tables.multiDelimiterCode(buffer);
            ++index;
        } else
            lexCode = (int) buffer.charAt(0);


        addLexCode(lexCode);
        return index;
    }

    public void showError(String message) {
        System.out.println(message);
    }

}
