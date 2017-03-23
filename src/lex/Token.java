package lex;

/**
 * Created by akhil on 3/19/17.
 */
public class Token {
    enum Type {
        EOF, // finished input
        Error, // error occurred during parsing
        Newline,
        Identifier,
        LeftBrack,
        LeftParen,
        Number,
        Operator,
        RightBrack,
        RightParen,
    }

    Type type;
    String value;
    int pos;
    int line;

    // For tokens with specific forms, like variables and constants
    Token(Type ttype, String v, int p, int l) {
        type = ttype;
        value = v;
        pos = p;
        line = l;
    }

    public String toString() {
        if (type == Type.EOF) {
            return "EOF";
        } else if (type == Type.Error) {
            return "error: " + value;
        } else if (value.length() > 10) {
            return String.format("%s: %.10s...", type, value);
        }
        return String.format("%s: %s", type, value);
    }
}
