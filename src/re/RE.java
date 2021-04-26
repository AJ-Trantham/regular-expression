package re;

import fa.nfa.*;
import java.util.LinkedHashSet;
import java.util.Set;


// Our proposed Grammar
/**
 * regex: term "|" regex | term
 * term: factor
 * factor: ground | ground*factor
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
    private String startOfRegex;

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
        startOfRegex = "";
    }

    @Override
    public NFA getNFA() {


        // this method will be our parser
        RE regex = regex();


        // parse the regex - TODO: change to walk through entire grammar
//        while (more()) {
//            previousState = currentState;
//            RE regex = regex();
//            currentState = nextStateName();
//            nfa.addState(currentState);
//            nfa.addTransition(previousState, regex.toString().charAt(0), currentState);
//        }
//        nfa.addFinalState(finalState);
//        nfa.addTransition(currentState, 'e', finalState);
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
        //  (a | b) | b
        String splitState = currentState;
        // make buffer states to prevent epsilon transitions from travelling back to shared split states
        String bufferStateRight = nextStateName();
        String bufferStateLeft = nextStateName();
        nfa.addState(bufferStateLeft);
        nfa.addState(bufferStateRight);
        nfa.addTransition(splitState, 'e', bufferStateLeft);
        nfa.addTransition(splitState, 'e', bufferStateRight);
        currentState = bufferStateLeft;
        RE term = term();
        if (more() && peek() == '|') {
            eat('|');
            String mergeState = nextStateName();
            nfa.addState(mergeState);
            nfa.addTransition(currentState, 'e', mergeState);
            currentState = bufferStateRight;
            term = regex();
            nfa.addTransition(currentState,'e',mergeState);
            currentState = mergeState;
        }
        return term;
    }

    private RE term() {
        return factor();
    }

    private RE factor() {
        //a*
        // (ab)*a*
        //String beginingOfRepetition = currentState;
        startOfRegex = ""; // set this to empty so ground can set it to the state where we begin processing a regex
        RE ground = ground();
        String endOfRepetition = currentState;
        // problem here don't want
        if (more() && peek() == '*') { // changed from while but we should only ever look for one star here and two stars doesn't make sense
            eat('*');
            String beginingOfRepitition = ground.startOfRegex.equals("") ? previousState : ground.startOfRegex;
            nfa.addTransition(endOfRepetition, 'e', beginingOfRepitition); // either the previous state or a whole regex ()
            nfa.addTransition(beginingOfRepitition, 'e', endOfRepetition);
            if (more() && peek() != '|' && peek() != '*' && peek() != ')') {
                ground = factor();
            }
        }

        return ground;
    }

    private RE ground() {

        RE base = base();
        while(more() && peek() != '|' && peek() != '*' && peek() != ')') {
            base = ground();
        }
        return base;
    }

    private RE base() {
        switch(peek()) {
            case '(': {
                eat('(');
                String theFollowingRegexBeginsOnThisState = currentState;
                RE regex = regex();
                regex.startOfRegex = theFollowingRegexBeginsOnThisState;
                eat(')');
                return regex;
            }
            // for char and car base production rules aa(ab)*  aaa
            default:
                // for single char
                previousState = currentState;
                RE character = character();
                currentState = nextStateName();
                nfa.addState(currentState);
                nfa.addTransition(previousState, character.toString().charAt(0), currentState);
                if (more() && peek() != '|' && peek() != '*' && peek() != ')') { // continue to process if string of chars
                    character = base();
                }

                return character; // what needs to be returned here??
        }

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
