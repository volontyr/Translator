package compiler;

/**
 * Created by santos on 4/12/16.
 */
public class Lexeme {
    private int lexCode;
    private int charIndex;
    private int stringIndex;

    public Lexeme(int lexCode, int stringIndex, int charIndex) {
        this.lexCode = lexCode;
        this.stringIndex = stringIndex;
        this.charIndex = charIndex;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public int getLexCode() {
        return lexCode;
    }
}
