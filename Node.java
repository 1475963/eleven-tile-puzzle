/**
 * author: mb785, BENZAHRA Marc
 */

/**
 * Node class which describes a puzzle tile
 */
public class Node {
    private char value;
    private int x;
    private int y;

    Node(char value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public char getValue() {
        return (this.value);
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return (this.x);
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return (this.y);
    }

    public Node clone() {
        return new Node(this.getValue(), this.getX(), this.getY());
    }

    /**
     * This function swaps the value of 2 tiles
     * @param anotherNode other tile object
     */
    public void swap(Node anotherNode) {
        Node tmp = anotherNode.clone();
        anotherNode.setValue(this.getValue());
        this.setValue(tmp.getValue());
    }
}
