import java.io.InputStream;
import java.util.Scanner;

import lex.Lexer;
import lex.Token;

/**
 * REPL runs the "Read-Evaluate-Print-Loop" when
 * nvcalc is executed from the command line. This class
 * is the main entry point for the program.
 */
public class REPL {

    private Scanner scanner;
    final static String invalid = "\\\r\n";

    REPL(InputStream ins) {
        scanner = new Scanner(ins);
    }

    // does not let through invalid characters
    String NextExpression() {
        StringBuilder s = new StringBuilder();
        boolean lastline = false;
        while (!lastline) {
            String raw = scanner.nextLine();
            lastline = raw.indexOf('\\') < 0;
            s.append(filter(raw));
        }
        return s.toString();
    }

    private static String filter(String raw) {
        StringBuilder s = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (valid(c)) {
                s.append(c);
            }
        }
        return s.toString();
    }

    private static boolean valid(int c) {
        if (invalid.indexOf(c) >= 0) {
            return false;
        }
        return true;
    }

    static void println(Object ...sarr) {
        String out = "";
        for (Object s : sarr) {
            out += s.toString() + " ";
        }
        System.out.println(out.substring(0, out.length()-1));
    }

    static void printtokens(Lexer l) {
        Token t = l.Next();
        if (t != null) {
            System.out.print(t);
        }
        while ((t = l.Next()) != null ) {
            System.out.print(", " + t);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        REPL stdin = new REPL(System.in);

        while (true) {
            // Read input expression
            String expression = stdin.NextExpression();
            // Initialize lexer for input
            Lexer l = new Lexer("", expression);

            System.out.println(expression);
            printtokens(l);
        }
    }
}
