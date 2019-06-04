package structures;
import java.util.*;

public class DOM {
	Node root=null;
	Scanner sc;
	
	public DOM(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	// Builds a DOM tree for the HTML file
	
	public void build() {
		if (sc == null) {
			return;
		}

		String curr = sc.nextLine();
		
		Stack<Node> tag = new Stack<Node>();
		root = new Node(tagFormat(curr), null, null);
		tag.push(root);

		while (sc.hasNextLine()) {
			curr = sc.nextLine();

			if (openCheck(curr)) {
				if (tag.peek().firstChild == null) {
					tag.peek().firstChild = new Node(tagFormat(curr), null, null);
					tag.push(tag.peek().firstChild);
				} 
				else {
					Node ptr = tag.peek().firstChild;
					
					while (ptr.sibling != null) {
						ptr = ptr.sibling;
					}
					
					ptr.sibling = new Node(tagFormat(curr), null, null);
					tag.push(ptr.sibling);
				}
			}
			
			else if (closeCheck(curr)) {
				tag.pop();
			} 
			
			else {
				if (tag.peek().firstChild == null) {
					tag.peek().firstChild = new Node(curr, null, null);
				} 
				
				else { 
					Node ptr = tag.peek().firstChild;
					while (ptr.sibling != null) {
						ptr = ptr.sibling;
					}
					ptr.sibling = new Node(curr, null, null);
				}
			}
		}
	}
	
	private boolean openCheck(String curr) {
		if(curr.contains("<") && curr.contains(">") && !curr.contains("/")) {
			return true;
		}
		return false;
	}
	
	private boolean closeCheck(String curr) {
		if(curr.contains("<") && curr.contains(">") && curr.contains("/")) {
			return true;
		}
		return false;
	}
	
	private String tagFormat(String currTag) {
		return currTag.replace("<", "").replace(">", "");
	}

	// Replaces all occurrences of an original tag in the DOM tree with the new tag
	public void replace(String originalTag, String newTag) {
		if(root != null && originalTag != null && newTag != null) {
			replace(originalTag, newTag, root);
		}
		return;
	}
	
	private void replace(String originalTag, String newTag, Node ptr) {
		if(ptr == null) {
			return;
		}
		
		if(ptr.tag.equals(originalTag)) {
			ptr.tag = newTag;
		}
		
		replace(originalTag, newTag, ptr.firstChild);
		replace(originalTag, newTag, ptr.sibling);
	}
	
	// Boldfaces every column of a given row
	public void boldRow(int row) {
		bold(row, 0, root);
	}
	
	private void bold(int row, int trCount, Node curr) {
		if(curr == null) {
			return;
		}
		
		if(curr.tag.equals("tr")){
			trCount++;
		}
		
		if(row == trCount && curr.tag.equals("td")) {
			curr.firstChild = new Node("b", curr.firstChild, null);
		} 

		bold(row, trCount, curr.firstChild); 
		bold(row, trCount, curr.sibling);
		
	}
	
	
	// Removes all occurrences of specified tag (p, em, b, ol or ul)
	public void removeTag(String tag) {
		remove(tag, null, root);
	}
	
	private void remove(String target, Node prevTag, Node root) {
		Node ptr = root;
		Node prev = null;
		
		while (ptr != null) {
			if (ptr.firstChild != null) {
				remove(target, ptr, ptr.firstChild);
				
				if(removeOpt1(ptr.tag, target)) {
					if(prev == null) {
						prevTag.firstChild = ptr.firstChild;
					}
					else {
						prev.sibling = ptr.firstChild;
					}
					
					Node lastSib = ptr.firstChild;
					
					while(lastSib.sibling != null) {
						lastSib = lastSib.sibling;
					}
					
					lastSib.sibling = ptr.sibling;
					ptr = ptr.firstChild;
				}
				
				else if(removeOpt2(ptr.tag, target)) {
					Node liCheckPtr = ptr.firstChild;
					while (liCheckPtr != null) {
						if(liCheckPtr.tag.equals("li")) {
							liCheckPtr.tag = "p"; // replacing all li with p
						}
						liCheckPtr = liCheckPtr.sibling;
					}
					
					if(prev == null) {
						prevTag.firstChild = ptr.firstChild;
					}
					else {
						prev.sibling = ptr.firstChild;
					}
					
					Node lastSib = ptr.firstChild;
					
					while(lastSib.sibling != null) {
						lastSib = lastSib.sibling;
					}
					
					lastSib.sibling = ptr.sibling;
					ptr = ptr.firstChild;
				}
			}
			
			prev = ptr;
			ptr = ptr.sibling; 
		}
	}
	
	private boolean removeOpt1(String tag, String target) {
		if((tag.equals("b") || tag.equals("p") || tag.equals("em")) && tag.equals(target)) {
			return true;
		}
		return false;
	}
	
	private boolean removeOpt2(String tag, String target) {
		if((tag.equals("ol") || tag.equals("ul")) && tag.equals(target)) {
			return true;
		}
		return false;
	}
	
	
	// Adds tag to all occurrences of a specified word
	
	public void addTag(String word, String tag) {
		add(root, word, tag);
	}
	
	private void add(Node curr, String word, String tag) {
		if(curr == null) {
			return;
		}
		
		if(curr.firstChild != null && !curr.tag.equals(tag)) {
			String [] StringArray = stringArr(word, curr.firstChild.tag);
			String prev = StringArray[0];
			String at = StringArray[1];
			String after = StringArray[2];
			
			
			if(prev == "" && after == "" && at != "") {
				Node temp = curr.firstChild;
				curr.firstChild = new Node(tag, null, null);
				Node tagged = curr.firstChild;
				tagged.firstChild = temp;
				tagged.sibling = temp.sibling;
				temp.sibling = null;
			}
			
			else if(prev == "" && at != "" && after != "") {
				Node temp = curr.firstChild;
				curr.firstChild = new Node(tag, null, null);
				Node tagged = curr.firstChild;
				tagged.firstChild = new Node(at, null, null);
				tagged.sibling = new Node(after, null ,null);
				tagged.sibling.sibling = temp.sibling;
				temp.sibling = null;
			}
			
			else if(prev != "" && at != "" && after != "") {
				Node temp = curr.firstChild;
				curr.firstChild = new Node(prev, null, null);
				Node beforeTag = curr.firstChild;
				beforeTag.sibling = new Node(tag, null, null);
				Node tagged = beforeTag.sibling;
				tagged.firstChild = new Node(at, null, null);
				tagged.sibling = new Node(after, null, null);
				tagged.sibling.sibling = temp.sibling;
				temp.sibling = null;
			}
			else if(after == "" && at != "" && prev != "") {
				Node temp = curr.firstChild;
				curr.firstChild = new Node(prev, null, null);
				Node beforeTag = curr.firstChild;
				beforeTag.sibling = new Node(tag, null, null);
				Node tagged = beforeTag.sibling;
				tagged.firstChild = new Node(at, null, null);
				tagged.sibling = temp.sibling;
				temp.sibling = null;
			}
		}
		
		if(curr.sibling != null) {
			String [] StringArray = stringArr(word, curr.sibling.tag);
			String prev = StringArray[0];
			String at = StringArray[1];
			String after = StringArray[2];
			
			
			if(prev == "" && after == "" && at != "") {
				Node temp = curr.sibling;
				curr.sibling = new Node(tag, null, null);
				Node tagged = curr.sibling;
				tagged.firstChild = temp;
				tagged.sibling = temp.sibling;
				temp.sibling = null;
			}
			
			else if(prev == "" && at != "" && after != "") {
				Node temp = curr.sibling;
				curr.sibling = new Node(tag, null, null);
				Node tagged = curr.sibling;
				tagged.firstChild = new Node(at, null, null);
				tagged.sibling = new Node(after, null ,null);
				tagged.sibling.sibling = temp.sibling;
				temp.sibling = null;
			}
			
			else if(prev != "" && at != "" && after != "") {
				Node temp = curr.sibling;
				curr.sibling = new Node(prev, null, null);
				Node beforeTag = curr.sibling;
				beforeTag.sibling = new Node(tag, null, null);
				Node tagged = beforeTag.sibling;
				tagged.firstChild = new Node(at, null, null);
				tagged.sibling = new Node(after, null, null);
				tagged.sibling.sibling = temp.sibling;
				temp.sibling = null;
			}
			else if(after == "" && at != "" && prev != "") {
				Node temp = curr.sibling;
				curr.sibling = new Node(prev, null, null);
				Node beforeTag = curr.sibling;
				beforeTag.sibling = new Node(tag, null, null);
				Node tagged = beforeTag.sibling;
				tagged.firstChild = new Node(at, null, null);
				tagged.sibling = temp.sibling;
				temp.sibling = null;
			}
		}
		
		add(curr.firstChild, word, tag);
		add(curr.sibling, word, tag);
		
	}
	
	private String[] stringArr(String word, String nodeTag) {
		String [] arr = nodeTag.split(" ");
		String [] rslt = new String[3];
		String prev = "";
		int k = 0;
		while(nodeTag.charAt(k) == ' ') {
			if(nodeTag.charAt(k) == ' ') {
				prev += "";
			}
			k++;
		}
		String at = "";
		String after = "";
		int i;
		for(i = 0; i < arr.length; i++) {
			if(word.equalsIgnoreCase(arr[i])) {
				at = arr[i];
				i++;
				break;
			}
			if(word.length() + 1 == arr[i].length()) {
				if(arr[i].substring(0, word.length()).equals(word)) {
					if(isValid(arr[i].charAt(arr[i].length()-1))) {
						at = arr[i];
						i++;
						break;
					}
				}
			}
			prev += arr[i] + " ";
		}

		for(int j = i; j<arr.length; j++) {
			after += " " + arr[j];
		}
		
		rslt[0] = prev;
		rslt[1] = at;
		rslt[2] = after;
		return rslt;
	}
	
	private boolean isValid(char x) {
		if(x == '.' || x == ',' || x == '?' || x == '!' || x == ':' || x ==';') {
			return true;
		}
		return false;
	}
		
	// returns the HTML that is inputted and formatted by application
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(Node root, StringBuilder sb) {
		for (Node ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	//Prints the DOM tree. 
	public void print() {
		print(root, 1);
	}
	
	private void print(Node root, int level) {
		for (Node ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
