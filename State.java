/**
 * author: mb785, BENZAHRA Marc
 */

import java.util.ArrayList;
import java.util.List;

/**
 * State class which describes a puzzle state
 */
public class State {
    private List<List<Node>> puzzle;
    private State previousState;
    private int cost;
    private int heuristic;

    State(int cost, int heuristic, State previousState) {
        this.puzzle = new ArrayList<>();
        this.cost = cost;
        this.heuristic = heuristic;
        this.previousState = previousState;
    }

    public List<List<Node>> getPuzzle() {
        return this.puzzle;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return (this.cost);
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public int getHeuristic() {
        return (this.heuristic);
    }

    public State getPreviousState() {
        return this.previousState;
    }

    /**
     * Clone a puzzle
     * @param withParent this boolean can trigger which is the previousState, if it's true its not a complete copy,
     *                   instead of that the parent field is the state object from which the cloning has been done
     * @return a new state object
     */
    public State clone(boolean withParent) {
        State state = null;

        if (withParent)
            state = new State(this.getCost(), this.getHeuristic(), this);
        else
            state = new State(this.getCost(), this.getHeuristic(), this.getPreviousState());

        for (int y = 0; y < this.getPuzzle().size(); y++) {
            state.getPuzzle().add(new ArrayList<Node>());
            for (int x = 0; x < this.getPuzzle().get(y).size(); x++) {
                state.getPuzzle().get(y).add(this.getPuzzle().get(y).get(x).clone());
            }
        }

        return state;
    }

    /**
     * Override equals to implement the queue items comparison from the .contains() queue function, little trick here
     * @param state
     * @return
     */
    @Override
    public boolean equals(Object state) {
        for (int y = 0; y < this.getPuzzle().size(); y++) {
            for (int x = 0; x < this.getPuzzle().get(y).size(); x++) {
                if (this.getPuzzle().get(y).get(x).getValue() != ((State)state).getPuzzle().get(y).get(x).getValue()) {
                    return false;
                }
            }
        }
        // implementation of puzzle comparison through queue contains + state equals
        if (this.getHeuristic() != 0 && this.getHeuristic() >= ((State)state).getHeuristic())
            return true;
        else
            return false;
    }

    /**
     * Standard equal function to differentiate two states
     * @param state
     * @return
     */
    public boolean realEquals(State state) {
        for (int y = 0; y < this.getPuzzle().size(); y++) {
            for (int x = 0; x < this.getPuzzle().get(y).size(); x++) {
                if (this.getPuzzle().get(y).get(x).getValue() != state.getPuzzle().get(y).get(x).getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Heuristic function
     * @param state end state usually
     */
    public void computeHeuristic(State state) {
        this.setHeuristic(this.getCost() + this.getHammingDistance(state));
    }

    /**
     * Hamming distance which counts how many different tiles there is between two puzzles
     * @param state end state usually
     * @return number of different tiles
     */
    public int getHammingDistance(State state) {
        int diffValues = 0;

        for (int y = 0; y < this.getPuzzle().size(); y++) {
            for (int x = 0; x < this.getPuzzle().get(y).size(); x++) {
                if (this.getPuzzle().get(y).get(x).getValue() != state.getPuzzle().get(y).get(x).getValue()) {
                    diffValues += 1;
                }
            }
        }

        return diffValues;
    }

    /**
     * Search for a tile which has the specified value
     * @param value searched character
     * @return node object which is a tile of the puzzle
     */
    public Node findTileByValue(char value) {
        for (int y = 0; y < this.getPuzzle().size(); y++) {
            for (int x = 0; x < this.getPuzzle().get(y).size(); x++) {
                if (this.getPuzzle().get(y).get(x).getValue() == value) {
                    return this.getPuzzle().get(y).get(x);
                }
            }
        }
        return null;
    }
}
