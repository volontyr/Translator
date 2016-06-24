package compiler;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by santos on 28.02.16.
 */
public class LexicalParser implements Parser {
    private InputStreamReader file;
    private ArrayList<Lexeme> lexCodesResultArray;
    private ArrayList<String> errors;
    private int charIndex;
    private int stringIndex;
    private Tables tables;


    public LexicalParser(InputStream file) {
        this.file = new InputStreamReader(file);
        String[] multiCharDelimiters = {":="};
        String[] keyWords = {"BEGIN", "END", "PROGRAM", "CONST", "VAR", "INTEGER", "BYTE"};
        tables = new Tables(keyWords, multiCharDelimiters);
        lexCodesResultArray = new ArrayList<>();
        errors = new ArrayList<>();
    }

    public void addLexCode(int lexCode, String lexeme) {
        if (lexCode == -1) {
            errors.add(lexeme);
        } else {
            lexCodesResultArray.add(new Lexeme(
                    lexCode, stringIndex, charIndex - lexeme.length() + 1
            ));
        }
    }

    public Tables getTables() {
        return tables;
    }

    public ArrayList<Lexeme> getLexCodesResultArray() {
        return lexCodesResultArray;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    @Override
    public void parser() {
        int symbol;

        try (BufferedReader reader = new BufferedReader(file)) {
            charIndex = 0;
            stringIndex = 0;
            symbol = reader.read();
            while (symbol != -1) {

                switch (tables.getAttribute((char) symbol)) {
                    case 0:
                        symbol = whitespaceHandling(reader, symbol);
                        break;
                    case 1:
                        symbol = constHandling(reader, symbol);
                        break;
                    case 2:
                        symbol = identifierHandling(reader, symbol);
                        break;
                    case 3:
                        symbol = commentHandling(reader, symbol);
//                        symbol = reader.read();
                        break;
                    case 4:
                        if ((char) symbol == '-') {
                            symbol = constHandling(reader, symbol);
                            break;
                        }
                        symbol = checkForMultiDelimiter(reader, symbol);
                        break;
                    case 5:
                        String buffer = "Illegal symbol at line " + stringIndex + " on column " + charIndex;
                        addLexCode(-1, buffer);
                        symbol = reader.read();
                        showError("Illegal symbol");

                    default:
                        break;
                }
                charIndex++;
                if ((char) symbol == '\n') {
                    do {
                        charIndex = 0;
                        stringIndex++;
                    } while ((char)(symbol = reader.read()) == '\n');
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public int whitespaceHandling(BufferedReader r, int symbol) {
        try {
            while ((symbol = r.read()) != -1 &&
                    tables.getAttribute((char) symbol) == 0) {
                charIndex++;
                if ((char) symbol == '\n') {
                    charIndex = 0;
                    stringIndex++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return symbol;
    }

    public int constHandling(BufferedReader r, int symbol) {
        String buffer = "";
        int lexCode;

        buffer += (char) symbol;

        try {
            while ((symbol = r.read()) != -1 &&
                    tables.getAttribute((char) symbol) == 1) {
                charIndex++;
                buffer += (char) symbol;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!buffer.equals("-")) {
            if (tables.constTableSearch(buffer))
                lexCode = tables.getConstCode(buffer);
            else
                lexCode = tables.constTableAdd(buffer);
        } else
            lexCode = '-';

        addLexCode(lexCode, buffer);

        return symbol;
    }

    public int identifierHandling(BufferedReader r, int symbol) {
        String buffer = "";
        int lexCode;

        buffer += (char) symbol;

        try {
            while ((symbol = r.read()) != -1 &&
                    (tables.getAttribute((char) symbol) == 2 ||
                            tables.getAttribute((char) symbol) == 1)) {
                charIndex++;
                buffer += (char) symbol;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer = buffer.toUpperCase();

        if (tables.keyTableSearch(buffer))
            lexCode = tables.getKeyWordCode(buffer);
        else
            if (tables.identifierTableSearch(buffer))
                lexCode = tables.getIdentifierCode(buffer);
            else
                lexCode = tables.identifierTableAdd(buffer);

        addLexCode(lexCode, buffer);

        return symbol;
    }

    public int commentHandling(BufferedReader r, int symbol) {
        String errMessage = "";
        int c = symbol;
        int commentStartStringIndex = stringIndex;
        int commentStartCharIndex = charIndex;

        try {
            if ((c = r.read()) == -1)
                addLexCode(symbol, "" + (char) symbol);
            else
                if ((char) c == '*') {
                    charIndex++;
                    do {
                        do {
                            charIndex++;
                            if ((char) c == '\n') {
                                charIndex = 0;
                                stringIndex++;
                            }
                        } while ((c = r.read()) != -1 && (char) c != '*');
                        while ((c = r.read()) != -1 && (char) c == '*') charIndex++;
                        if (c == -1) {
                            stringIndex = commentStartStringIndex;
                            charIndex = commentStartCharIndex;
                            errMessage = "*) expected but end of file found (comment start at line "
                                    + stringIndex + " on column " + charIndex + ")";
                            addLexCode(-1, errMessage);
                            break;
                        }
                    } while ((char) c != ')');
                    c = r.read();
                } else {
                    addLexCode(symbol, "" + (char) symbol);
                    return c;
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!errMessage.equals(""))
            showError(errMessage);

        return c;
    }

    public int checkForMultiDelimiter(BufferedReader r, int symbol) {
        String buffer = "";
        int lexCode = symbol;

        buffer += (char) symbol;
        try {
            if ((symbol = r.read()) != -1) {
                buffer += (char) symbol;
                charIndex++;
            }

            if (tables.multiDelimiterSearch(buffer)) {
                lexCode = tables.multiDelimiterCode(buffer);
                symbol = r.read();
            }
            else {
                buffer = "" + buffer.charAt(0);
                if (charIndex > 0) charIndex--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addLexCode(lexCode, buffer);

        return symbol;
    }

    public void showError(String message) {
        System.out.println(message);
    }

}
