import java.util.List;

import lex.Operator;

/**
 * Created by akhil on 3/19/17.
 */
class Expression {
    int pos;
    int end;
    Operator op;
    List<Expression> expressions;


    Expression Eval() {
        return null;
    }

    // For internal debugging only
    public String toString() {
        return "";
    }

    String Sprint() {
        return "";
    }

    Expression toType() {
        return null;
    }
}
