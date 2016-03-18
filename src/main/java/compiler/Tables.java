package compiler;

import java.util.HashMap;

/**
 * Created by santos on 28.02.16.
 */
public class Tables {

    private static int keyWordsCode;
    private static int identifiersCode;
    private static int constCode;
    private static int multiCharDelimitersCode;

    private HashMap<Character, Integer> attributes;
    private HashMap<String, Integer> keyWords;
    private HashMap<String, Integer> multiCharDelimiters;
    private HashMap<String, Integer> identifiersTable;
    private HashMap<String, Integer> constTable;

    public Tables(String[] keyWords, String[] multiCharDelimiters) {
        attributes = new HashMap<>();
        this.keyWords = new HashMap<>();
        this.multiCharDelimiters = new HashMap<>();

        /*initializing*/
        multiCharDelimitersCode = 301;
        keyWordsCode = 401;
        constCode = 501;
        identifiersCode = 1001;

        for(int i = 0; i < 128; i++) {

            if (i >= 9 && i <= 13 || i >= 28 && i <= 32)
                attributes.put((char) i, 0);
            else if (i >= 48 && i <= 57)
                attributes.put((char) i, 1);
            else if (i >= 65 && i <= 90 || i >= 97 && i <= 122)
                attributes.put((char) i, 2);
            else  if (i == 40)
                attributes.put((char) i, 3);
            else if (i >= 41 && i <= 47 || i >= 58 && i <= 62)
                attributes.put((char) i, 4);
            else
                attributes.put((char) i, 5);
        }

        for(int i = 0; i < keyWords.length; i++)
            this.keyWords.put(keyWords[i], keyWordsCode++);

        for(int i = 0; i < multiCharDelimiters.length; i++)
            this.multiCharDelimiters.put(multiCharDelimiters[i], multiCharDelimitersCode++);

        identifiersTable = new HashMap<>();
        constTable = new HashMap<>();
    }

    //  checks if string 'value' is in the table
    public boolean identifierTableSearch(String value) {
        return identifiersTable.containsKey(value);
    }

    //  returns hashcode of the string 'value' that inserted into table
    public int identifierTableAdd(String value) {
        identifiersTable.put(value, identifiersCode++);
        return identifiersCode - 1;
    }

    public int getIdentifierCode(String value) {
        return identifiersTable.get(value);
    }

    //  checks if string 'value' is in the table
    public boolean keyTableSearch (String value) {
        return keyWords.containsKey(value);
    }

    public int getKeyWordCode(String value) {
        return keyWords.get(value);
    }

    //  checks if string 'value' is in the table
    public boolean constTableSearch(String value) {
        return constTable.containsKey(value);
    }

    //  returns hashcode of the string 'value' that inserted into table
    public int constTableAdd(String value) {
        constTable.put(value, constCode++);
        return constCode - 1;
    }

    public int getConstCode(String value) {
        return constTable.get(value);
    }

    public boolean multiDelimiterSearch(String value) {
        return multiCharDelimiters.containsKey(value);
    }

    public int multiDelimiterCode(String value) {
        return multiCharDelimiters.get(value);
    }

    // returns an attribute of current character in range of [0..5]
    public int getAttribute (char c) {
        return attributes.get(c);
    }

    public HashMap<String, Integer> getKeyWords() {
        return keyWords;
    }

    public HashMap<String, Integer> getMultiCharDelimiters() {
        return multiCharDelimiters;
    }

    public HashMap<String, Integer> getIdentifiersTable() {
        return identifiersTable;
    }

    public HashMap<String, Integer> getConstTable() {
        return constTable;
    }
}
