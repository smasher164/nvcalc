package lex;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by akhil on 3/19/17.
 * Produce sequence of tokens
 */
public class Lexer {
    String name;
    String input;
    stateFn fn;
    BlockingQueue<Token> tokens;
    int start;
    int pos;
    int width;
    int line;
    final static int eof = -1;

    public Lexer(String name, String input) {
        this.name = name;
        this.input = input;
        tokens = new ArrayBlockingQueue<Token>(2);
        line = 0;
        start = 0;
        pos = 0;
        fn = lexAny;
    }

    // called by parser
    public Token Next() {
        Token t;
        while ((t = tokens.poll()) == null) {
            if (fn == null) {
                break;
            }
            fn = fn.state(this);
        }
        return t;
    }

    private static stateFn lexAny = (Lexer l) -> {
        int c = l.next();
        if (c == eof) {
            return null;
        } else if (Character.isWhitespace(c)) {
            return Lexer.lexSpace;
        } else if (c == '.' || ('0' <= c && c <= '9')) {
            l.backup();
            return Lexer.lexNumber;
        } else if (l.isOperator(c)) {
            return Lexer.lexOperator;
        } else if (isAlphanumeric(c)) {
            l.backup();
            return Lexer.lexIdentifier;
        } else if (c == '[') {
            l.emit(Token.Type.LeftBrack);
            return Lexer.lexAny;
        } else if (c == ']') {
            l.emit(Token.Type.RightBrack);
            return Lexer.lexAny;
        } else if (c == '(') {
            l.emit(Token.Type.LeftParen);
            return Lexer.lexAny;
        } else if (c == ')') {
            l.emit(Token.Type.RightParen);
            return Lexer.lexAny;
        } else {
            return l.errorf("unrecognized character: U+%d", c);
        }
    };

    private static stateFn lexIdentifier = (Lexer l) -> {
        while (true) {
            int c = l.next();
            if (isAlphanumeric(c)) {
            } else {
                l.backup();
                if (!l.atTerminator()) {
                    return l.errorf("bad character U+%d", c);
                }
                // Some identifiers are operators
                String word = l.input.substring(l.start, l.pos);
                if (Operator.ops.contains(word)) {
                    return Lexer.lexOperator;
                } else if (isAllDigits(word)) {
                    l.emit(Token.Type.Number);
                } else {
                    l.emit(Token.Type.Identifier);
                }
                break;
            }
        }
        return Lexer.lexAny;
    };

    private static stateFn lexNumber = (Lexer l) -> {
        String digits = "0123456789";
        do {
            l.accept("+-");
            l.acceptRun(digits);
            if (l.accept(".")) {
                l.acceptRun(digits);
            }
            if (l.accept("eE")) {
                l.accept("+-");
                l.acceptRun(digits);
            }
        } while(l.accept("^"));
        if (isAlphanumeric(l.peek())) {
            l.next();
            return l.errorf("bad number syntax: %s", l.input.substring(l.start, l.pos));
        }
        l.emit(Token.Type.Number);
        return Lexer.lexAny;
    };

    private static stateFn lexOperator = (Lexer l) -> {
        if (isIdentifier(l.input.substring(l.start, l.pos))) {
            l.emit(Token.Type.Identifier);
        } else {
            l.emit(Token.Type.Operator);
        }
        return Lexer.lexSpace;
    };

    private static stateFn lexSpace = (Lexer l) -> {
        while (Character.isWhitespace(l.peek())) {
            l.next();
        }
        l.ignore();
        return Lexer.lexAny;
    };

    private stateFn errorf(String format, Object ...args) {
        Token t = new Token(Token.Type.Error, String.format(format, args), start, line);
        try { tokens.put(t); } catch (InterruptedException e) {}
        return Lexer.lexAny;
    }

    private static boolean isAlphanumeric(int c) {
        return Character.isLetter(c) || Character.isDigit(c);
    }

    private static boolean isOperator(int c) {
        String valid = "+-*/_^";
        if (valid.indexOf(c) >= 0) {
            return true;
        }
        return false;
    }

    private void emit(Token.Type ttype) {
        if (ttype == Token.Type.Newline) {
            line++;
        }
        Token t = new Token(ttype, input.substring(start, pos), pos, line);
        try { tokens.put(t); } catch (InterruptedException e) {}
        start = pos;
        width = 0;
    }

    private static boolean isIdentifier(String s) {
        boolean first = true;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            if (Character.isDigit(c)) {
                if (first) {
                    return false;
                }
            } else if (c != '_' && !Character.isLetter(c)) {
                return false;
            }
            first = false;
        }
        return true;
    }

    private static boolean isPunct(int c) {
        int utype = Character.getType(c);
        switch (utype) {
            case Character.CONNECTOR_PUNCTUATION:
            case Character.DASH_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
            case Character.START_PUNCTUATION:
                return true;
            default:
                return false;
        }
    }

    private static boolean isSymbol(int c) {
        int utype = Character.getType(c);
        switch (utype) {
            case Character.CURRENCY_SYMBOL:
            case Character.MODIFIER_SYMBOL:
            case Character.MATH_SYMBOL:
            case Character.OTHER_SYMBOL:
                return true;
            default:
                return false;
        }
    }

    private static boolean isAllDigits(String word) {
        for (int i = 0; i < word.length(); i++) {
            int c = word.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean atTerminator() {
        int c = peek();
        if (c == eof || Character.isWhitespace(c) || isPunct(c) || isSymbol(c)) {
            return true;
        }
        return false;
    }


    private int next() {
        if (pos >= input.length()) {
            width = 0;
            return eof;
        }
        int c = input.charAt(pos);
        width = 1;
        pos += width;
        return c;
    }

    private void ignore() {
        start = pos;
    }

    private void backup() {
        pos -= width;
    }

    private int peek() {
        int c = next();
        backup();
        return c;
    }

    private boolean accept(String valid) {
        if (valid.indexOf(next()) >= 0) {
            return true;
        }
        backup();
        return false;
    }

    private void acceptRun(String valid) {
        while (valid.indexOf(next()) >= 0) {}
        backup();
    }
}