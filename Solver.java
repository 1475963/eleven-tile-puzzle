/**
 * author: mb785, BENZAHRA Marc
 */

import java.io.*;
import java.util.*;

/**
 * Solver class, this class computes the states necessary to go from a start state to a end state
 */
public class Solver {
    private static final String TXT_FILE_EXT = new String(".txt");
    private static final char CONF_SEPARATOR = '2';
    private static final char CONF_CURRENT_POS_REPR = '_';
    private static final char CONF_WALL_REPR = '*';
    private static final char CONF_WINDOWS_WALL_REPR = '+';
    private static final char CONF_RESULT_PUZZLE_SEPARATOR = ' ';
    private static final int STATE_WIDTH = 4;
    private static final int STATE_HEIGHT = 4;
    private String[] fileNames = {"dadd*dbb*adb**_c2da_d*ddd*bba**cb",
                                  "dabb*cbd*add**d_2dbbd*abd*ca_**dd",
                                  "dbdd*dac*_bb**da2dbdd*baa*bdd**c_",
                                  "dcd_*abd*adb**db2dacb*d_d*abd**bd",
                                  "ddba*db_*bdd**ca2dbdb*da_*bcd**ad",
                                  "d_ba*ddb*bcd**ad2ddb_*dba*bdd**ca",
                                  "ddbb*d_b*ada**dc2dddb*ab_*dad**bc",
                                  "ddda*b_d*cad**bb2dbdd*aa_*dcb**bd",
                                  "dcbd*a_a*bbd**dd2_dcd*adb*abd**db",
                                  "dcbd*baa*ddd**b_2dbad*cad*bd_**bd",
                                  "db_b*dda*bcd**ad2ddbb*dad*b_c**da",
                                  "ddd_*add*cbb**ab2dddd*dbb*a_a**bc",
                                  "dadc*_db*abd**bd2_ddb*abd*acd**bd",
                                  "ddba*bdb*cdd**a_2d_ad*bdb*cdb**ad",
                                  "dad_*bbd*adb**dc2dadd*a_b*dbd**cb",
                                  "d_dd*ada*bcb**db2_dcd*dda*abb**db"};
    private List<State> currentStates = null;
    private List<State> targetStates = null;
    private List<List<State>> allGraphs = null;

    Solver() {
        this.currentStates = new ArrayList<>();
        this.targetStates = new ArrayList<>();
        this.allGraphs = new LinkedList<>();
        this.getStates();
    }

    /**
     * getStates function builds data structures from filenames strings
     */
    private void getStates() {
        String[] states;
        State currentState;
        State targetState;
        List<Node> currentLine;
        List<Node> targetLine;
        Node currentNode;
        Node targetNode;
        int i = 0;
        while (i < this.fileNames.length) {
            currentState = new State(0, 0, null);
            targetState = new State(0, 0, null);
            this.currentStates.add(currentState);
            this.targetStates.add(targetState);
            states = this.fileNames[i].split("" + this.CONF_SEPARATOR); // magic concat trick for java
            for (int y = 0; y < this.STATE_HEIGHT; y++) {
                currentLine = new ArrayList<>();
                targetLine = new ArrayList<>();
                currentState.getPuzzle().add(currentLine);
                targetState.getPuzzle().add(targetLine);
                for (int x = 0; x < this.STATE_WIDTH; x++) {
                    currentNode = new Node(states[0].charAt((y * this.STATE_WIDTH) + x), x, y);
                    targetNode = new Node(states[1].charAt((y * this.STATE_WIDTH) + x), x, y);
                    currentLine.add(currentNode);
                    targetLine.add(targetNode);
                }
            }
            i++;
        }
    }

    /**
     * Solving function, finds shortest path for each puzzle then dump results
     */
    public void solve() {
        List<State> currentGraph = null;
        for (int i = 0; i < this.currentStates.size(); i++) {
            currentGraph = new ArrayList<>();
            this.allGraphs.add(currentGraph);
            this.shortestPath(currentGraph, this.currentStates.get(i), this.targetStates.get(i));
        }
        dumpResults();
    }

    /**
     * Usage of 2 queues, one to store visited states and another to order next states to compute,
     * compute heuristic for each neighbour and search in the search space to find the solution as quickly as possible
     * by exploring first the neighbour with the lowest heuristic score (which is considered as the best next state
     * @param graph, current puzzle states
     * @param start, start state
     * @param end, end state
     * @return
     */
    private int shortestPath(List<State> graph, State start, State end) {
        Queue<State> closedList = new LinkedList<>();
        PriorityQueue<State> openList = new PriorityQueue<>(this.STATE_HEIGHT * this.STATE_WIDTH, new Comparator<State>() {
            @Override
            public int compare(State state, State anotherState) {
                if (state.getHeuristic() > anotherState.getHeuristic())
                    return (1);
                else if (state.getHeuristic() == anotherState.getHeuristic())
                    return (0);
                else
                    return (-1);
            }
        });
        openList.add(start);
        State tmp;
        while ((tmp = openList.poll()) != null) {
            if (tmp.realEquals(end)) {
                Stack<State> bestPath = new Stack<>();
                while (tmp != null) {
                    bestPath.push(tmp);
                    tmp = tmp.getPreviousState();
                }
                State tmpBestPath;
                while (!bestPath.empty()) {
                    tmpBestPath = bestPath.pop();
                    graph.add(tmpBestPath);
                }
                //System.out.println("number of states (start and end included): " + graph.size());
                return (1);
            }
            else {
                List<State> neighbours = next_configs(tmp);
                for (State neighbour : neighbours) {
                    if (!closedList.contains(neighbour) && !openList.contains(neighbour)) {
                        neighbour.setCost(neighbour.getCost() + 1);
                        neighbour.computeHeuristic(end);
                        openList.add(neighbour);
                    }
                }
            }
            closedList.add(tmp);
        }
        return (0);
    }

    /**
     * Create all possible next states from current state
     * @param config current state
     * @return list of state objects
     */
    private List<State> next_configs(State config) {
        List<State> configs = new LinkedList<>();
        Node currentNode = config.findTileByValue(this.CONF_CURRENT_POS_REPR);
        State tmp;

        if (currentNode == null)
            return null;

        tmp = createConfig(config, currentNode.getX(), currentNode.getY(), currentNode.getX() + 1, currentNode.getY());
        if (tmp != null)
            configs.add(tmp);
        tmp = createConfig(config, currentNode.getX(), currentNode.getY(), currentNode.getX() - 1, currentNode.getY());
        if (tmp != null)
            configs.add(tmp);
        tmp = createConfig(config, currentNode.getX(), currentNode.getY(), currentNode.getX(), currentNode.getY() + 1);
        if (tmp != null)
            configs.add(tmp);
        tmp = createConfig(config, currentNode.getX(), currentNode.getY(), currentNode.getX(), currentNode.getY() - 1);
        if (tmp != null)
            configs.add(tmp);
        return configs;
    }

    /**
     * Create a new state by cloning current state and swapping star tile into new position
     * @param config, old state
     * @return state object
     */
    private State createConfig(State config, int currentXPos, int currentYPos, int expectedXPos, int expectedYPos) {
        if (expectedXPos < 0 || expectedXPos >= this.STATE_WIDTH
                || expectedYPos < 0 || expectedYPos >= this.STATE_HEIGHT
                || config.getPuzzle().get(expectedYPos).get(expectedXPos).getValue() == this.CONF_WALL_REPR)
            return null;

        State result = config.clone(true); // clone with parent track kept

        result.getPuzzle().get(currentYPos).get(currentXPos).swap(result.getPuzzle().get(expectedYPos).get(expectedXPos));

        return result;
    }

    /**
     * Write each puzzle states into a different file
     */
    public void dumpResults() {
        for (int i = 0; i < this.allGraphs.size(); i++) {
            try {
                FileWriter graphFileWriter = new FileWriter(this.fileNames[i].replace(this.CONF_WALL_REPR, this.CONF_WINDOWS_WALL_REPR) + this.TXT_FILE_EXT);
                BufferedWriter bufferedWriter = new BufferedWriter(graphFileWriter);

                for (int y = 0; y < this.STATE_HEIGHT; y++) {
                    for (int j = 0; j < this.allGraphs.get(i).size(); j++) {
                        for (int x = 0; x < this.STATE_WIDTH; x++) {
                            bufferedWriter.write(this.allGraphs.get(i).get(j).getPuzzle().get(y).get(x).getValue());
                        }
                        bufferedWriter.write(this.CONF_RESULT_PUZZLE_SEPARATOR);
                    }
                    if (y < this.STATE_HEIGHT - 1)
                        bufferedWriter.newLine();
                }

                bufferedWriter.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Solver solver = new Solver();
        solver.solve();
    }
}
