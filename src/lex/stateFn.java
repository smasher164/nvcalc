package lex;

/**
 * Created by akhil on 3/19/17.
 */
public interface stateFn {
    stateFn state(Lexer l);
}
