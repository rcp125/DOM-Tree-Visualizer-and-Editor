package structures;

public class Node {
	String tag;
	Node firstChild;
	Node sibling;
	
	public Node(String tag, Node firstChild, Node sibling) {
		this.tag = tag;
		this.firstChild = firstChild;
		this.sibling = sibling;
	}

	public String toString() {
		if (firstChild != null) {
			return "<" + tag + ">";
		} else {
			return tag;
		}
	}
}
