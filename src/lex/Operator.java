package lex;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by akhil on 3/19/17.
 */
public class Operator {
    enum Type {
        add,
        subtract,
        negate,
        multiply,
        divide,
        base,
        range,
        power,
        log,
        ln,
        sin,
        cos,
        tan,
        arcsin,
        arccos,
        arctan,
        derivative,
        integral;
    }

    Type type;
    String value;

    public final static Set<String> ops = Stream
            .of("+",
                "-",
                "*",
                "/",
                "_",
                "^",
                "log",
                "ln",
                "sin",
                "cos",
                "tan",
                "arcsin",
                "arccos",
                "arctan",
                "derivative",
                "integral")
            .collect(Collectors.toCollection(HashSet::new));
}
