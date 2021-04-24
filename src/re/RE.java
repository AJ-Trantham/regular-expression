package re;

import fa.nfa.NFA;


// Our proposed Grammar
/**
 * regex: term "|" regex | term
 * term: factor
 * factor: ground | ground*factor | ground factor
 * ground: base ground| base
 * base: char | char base | "(" regex ")"
 * char: a | b | e
 */

public class RE implements REInterface {

    private String regexString;
    private NFA nfa;

    public RE(String regEx) {
        this.regexString = regEx;
        this.nfa = new NFA();
    }

    @Override
    public NFA getNFA() {


        // this method will be our parser

        // set up NFA

        // parse the regex

        // as we parse we need to know the current state

        // as we do, add appropriate items to NFA: states and transitions


        return null;
    }

    private char peek() {
        return regexString.charAt(0);
    }

    private void eat(char c) {
        if (peek() == c) {
            this.regexString = this.regexString.substring(1);
        } else {
            throw new RuntimeException("Not a valid place to eat. " + c + " != " + peek());
        }
    }

    private boolean more() {
        return regexString.length() > 0;
    }

    private RE regex () {
        return null;
    }

    private RE term() {
        return null;
    }

    private RE factor() {
        return null;
    }

    private RE ground() {
        return null;
    }

    private RE base() {
        return null;
    }

    private RE character() {
        char c = peek();
    }
}
