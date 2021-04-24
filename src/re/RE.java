package re;

import fa.nfa.*;
import java.util.LinkedHashSet;
import java.util.Set;


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
    private String previousState;
    private String currentState;
    private String finalState; // all states which should be a final state should map to this state with an e transition
    private int stateNameCount;

    public RE(String regEx) {
        this.regexString = regEx;
        // set up NFA
        this.nfa = new NFA();
        Set<Character> alphabet = new LinkedHashSet<Character>();
        alphabet.add('a');
        alphabet.add('b');
        alphabet.add('e');
        this.nfa.addAbc(alphabet);
        previousState = null;
        stateNameCount = 0;
        nfa.addStartState(nextStateName());
        currentState = nfa.getStartState().getName();
        finalState = "final";
    }

    @Override
    public NFA getNFA() {


        // this method will be our parser



        // parse the regex - TODO: change to walk through entire grammar
        while (more()) {
            previousState = currentState;
            RE character = character();
            currentState = nextStateName();
            nfa.addState(currentState);
            nfa.addTransition(previousState, character.toString().charAt(0), currentState);
        }
        nfa.addFinalState(finalState);
        nfa.addTransition(currentState, 'e', finalState);

        return nfa;
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
        String c = String.valueOf(peek());
        eat(peek());
        return new RE(c);
    }

    private String nextStateName() {
        return Integer.toString(stateNameCount++);
    }

    public String toString() {
        return regexString;
    }
}
