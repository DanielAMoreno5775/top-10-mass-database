
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Daniel Moreno
 */

//uses a red-black tree to store the data
public class OrderedAddOnce <E extends Comparable<? super E>> implements AddOnce <E>, Iterable<E>{
   //Aggregated private inner classes
   private class Node {
        //define variables
        private enum Child {LEFT, RIGHT}
        private enum Color {RED, BLACK}
        private E key;
        private Node parent;
        private Node left;
        private Node right;
        private Color nodeColor;
        
        
        
        //constructors
        public Node (E nodeKey, Node parentNode, boolean isRed) {
            this(nodeKey, parentNode, isRed, null, null);
        }
        public Node (E nodeKey, Node parentNode, boolean isRed, Node leftKid, Node rightKid) {
            key = nodeKey;
            parent = parentNode;
            left = leftKid;
            right = rightKid;
            if (isRed) {
                nodeColor = Color.RED;
            } else {
                nodeColor = Color.BLACK;
            }
        }
        
        
        
        //getter methods
        //method to get count
        public int count () {
            int count = 1;
            if (left != null) {
                count += left.count();
            }
            if (right != null) {
                count += right.count();
            }
            return count;
        }
        
        public Node getGrandparent(){
            if (parent == null) {
                return null;
            }
            return parent.parent;
        }
        
        public Node getSibling () {
            if (parent != null) {
                if (this == parent.left) {
                    return parent.right;
                }
                return parent.left;
            }
            return null;
        }
        public Node getPredecessor(){
            Node node = left;
            while (node.right != null) {
                node = node.right;
            }
            return node;
        }
        
        public Node getUncle(){
            Node grandparent = getGrandparent();
            if (grandparent == null) {
                return null;
            }
            if (grandparent.left == parent) {
                return grandparent.right;
            }
            return grandparent.left;
        }
        
        
        
        //boolean check methods
        //null kids are black
        public boolean areBothChildrenBlack() {
            if (left != null && left.isRed()) {
                return false;
            }
            if (right != null && right.isRed()) {
                return false;
            }
            return true;
        }
        
        //methods to check color
        public boolean isBlack(){
            return nodeColor == Color.BLACK;
        }
        
        public boolean isRed(){
            return nodeColor == Color.RED;
        }
        
        
        
        //mutator methods
        public boolean replaceChild(Node currentKid, Node newKid) {
            if (left == currentKid) {
                return setChild(Child.LEFT, newKid);
            } else if (right == currentKid) {
                return setChild(Child.RIGHT, newKid);
            }
            return false;
        }
        
        public boolean setChild(Child whichKid, Node kid) {
            if (whichKid == Child.LEFT) {
                left = kid;
            } else {
                right = kid;
            }
            
            if (kid != null) {
                kid.parent = this;
            }
            return true;
        }
    }
   
   private class TreeIterator implements Iterator<E> {
        private Stack<Node> traversal;
 
        TreeIterator(Node root) {
            traversal = new Stack<Node>();
            moveLeft(root);
        }
 
        private void moveLeft(Node current) {
            while (current != null) {
                traversal.push(current);
                current = current.left;
            }
        }
 
        @Override
        public boolean hasNext() {
            return !traversal.isEmpty();
        }
 
        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
 
            Node current = traversal.pop();
 
            if (current.right != null)
                moveLeft(current.right);
 
            return current.key;
        }
   }
   
   //attributes
   private Node root;
   private int numOfNodes;

   
   
   //constructor
   public OrderedAddOnce () {
        root = null;
        numOfNodes = 0;
    }

   
   
   //iterator method
   @Override
   public Iterator iterator() {
        return new TreeIterator(root);
   }
   
   
   
   //insertion methods
   @Override
   public E addOnce(E item) {
        Node searchResult = search(item);
        if (searchResult != null) {
           return searchResult.key;
        } else {
           //create a new node with the given value
           Node temp = new Node(item, null, true, null, null);
           //insert the new node
           insertNode(temp);
           numOfNodes++;
           return temp.key;
        }
    }
   
   private void insertNode(Node node) {
      // special case if empty
      if (root == null) {
         root = node;
      } else {
         Node currentNode = root;
         while (currentNode != null) {
            if (node.key.compareTo(currentNode.key) < 0) {
               if (currentNode.left == null) {
                  currentNode.setChild(Node.Child.LEFT, node);
                  break;
               } else {
                  currentNode = currentNode.left;
               }
            } else if (node.key.compareTo(currentNode.key) > 0) { 
               if (currentNode.right == null) {
                  currentNode.setChild(Node.Child.RIGHT, node);
                  break;
               } else {
                  currentNode = currentNode.right;
               }
            } else {
               break;
            }
         }
      }
      
      // Color the node red, then balance
      node.nodeColor = Node.Color.RED;
      insertionBalance(node);
   }
   
   private void insertionBalance(Node node) {
      // If node is the tree's root, then color node black and return
      if (node.parent == null) {
         node.nodeColor = Node.Color.BLACK;
         return;
      }
        
      // If parent is black, then return without any changes
      if (node.parent.isBlack()) {
         return;
      }
    
      // References to parent, grandparent, and uncle are needed for remaining operations
      Node parent = node.parent;
      Node grandparent = node.getGrandparent();
      Node uncle = node.getUncle();
        
      // If parent and uncle are both red, then color parent and uncle black, color grandparent
      // red, recursively balance  grandparent, then return
      if (uncle != null && uncle.isRed()) {
         parent.nodeColor = uncle.nodeColor = Node.Color.BLACK;
         grandparent.nodeColor = Node.Color.RED;
         insertionBalance(grandparent);
         return;
      }

      // If node is parent's right child and parent is grandparent's left child, then rotate left
      // at parent, update node and parent to point to parent and grandparent, respectively
      if (node == parent.right && parent == grandparent.left) {
         rotateLeft(parent);
         node = parent;
         parent = node.parent;
      }
      // Else if node is parent's left child and parent is grandparent's right child, then rotate
      // right at parent, update node and parent to point to parent and grandparent, respectively
      else if (node == parent.left && parent == grandparent.right) {
         rotateRight(parent);
         node = parent;
         parent = node.parent;
      }

      // Color parent black and grandparent red
      parent.nodeColor = Node.Color.BLACK;
      grandparent.nodeColor = Node.Color.RED;
                
      // If node is parent's left child, then rotate right at grandparent, otherwise rotate left
      // at grandparent
      if (node == parent.left) {
         rotateRight(grandparent);
      }
      else {
         rotateLeft(grandparent);
      }
   }
   
   
   
   //getter methods of root, length, and height
   public Node getRoot() {
      return root;
   }
   
   public int getLength() {
      if (root == null) {
         return 0;
      }
      return root.count();
   }
   
   public int getHeight() {
      return getHeightRecursive(root);
   }

   private int getHeightRecursive(Node node) {
      if (node == null) {
         return -1;
      }
      int leftHeight = getHeightRecursive(node.left);
      int rightHeight = getHeightRecursive(node.right);
      return 1 + Math.max(leftHeight, rightHeight);
   }
   
   
   
   // Performs a left rotation at the given node. Returns the subtree's new root.
   private Node rotateLeft(Node node) {
      // Define a convenience pointer to the right child of the 
      // left child.
      Node rightLeftChild = node.right.left;
        
      // Step 1 - the right child moves up to the node's position.
      // node is temporarily detached from the tree, but will be reattached
      // later.
      if (node.parent != null) {
         node.parent.replaceChild(node, node.right);
      }
      else { // node is root
         root = node.right;
         root.parent = null;
      }

      // Step 2 - the node becomes the left child of what used
      // to be node's right child, but is now node's parent. This will
      // detach rightLeftChild from the tree.
      node.right.setChild(Node.Child.LEFT, node);
        
      // Step 3 - reattach rightLeftChild as the right child of node.
      node.setChild(Node.Child.RIGHT, rightLeftChild);
        
      return node.parent;
   }
   
   // Performs a right rotation at the given node. Returns the subtree's new root.
   public Node rotateRight(Node node) {
      // Define a convenience pointer to the left child of the 
      // right child.
      Node leftRightChild = node.left.right;
        
      // Step 1 - the left child moves up to the node's position.
      // node is temporarily detached from the tree, but will be reattached
      // later.
      if (node.parent != null) {
         node.parent.replaceChild(node, node.left);
      }
      else { // node is root
         root = node.left;
         root.parent = null;
      }

      // Step 2 - the node becomes the right child of what used
      // to be node's left child, but is now node's parent. This will
      // detach leftRightChild from the tree.
      node.left.setChild(Node.Child.RIGHT, node);
        
      // Step 3 - reattach leftRightChild as the right child of node.
      node.setChild(Node.Child.LEFT, leftRightChild);
        
      return node.parent;
   }

   
   
   //removal methods
   public boolean remove(E key) {
      Node node = search(key);
      if (node != null) {
         removeNode(node);
         return true;
      }
      return false;
   }

   private void removeNode(Node node) {
      if (node.left != null && node.right != null) {
         Node predecessorNode = node.getPredecessor();
         E predecessorKey = predecessorNode.key;
         removeNode(predecessorNode);
         node.key = predecessorKey;
         return;
      }

      if (node.isBlack()) {
         prepareForRemoval(node);
      }
      bstRemoveNode(node);

      // One special case if the root was changed to red
      if (root != null && root.isRed()) {
         root.nodeColor = Node.Color.BLACK;
      }
   }
   
   private void bstRemoveNode(Node node) {
      if (node == null) {
         return;
      }

      // Case 1: Internal node with 2 children
      if (node.left != null && node.right != null) {
         // Find successor
         Node successorNode = node.right;
         while (successorNode.left != null) {
            successorNode = successorNode.left;
         }

         // Copy successor's key
         E successorKey = successorNode.key;

         // Recursively remove successor
         bstRemoveNode(successorNode);

         // Set node's key to copied successor key
         node.key = successorKey;
      }
      
      // Case 2: Root node (with 1 or 0 children)
      else if (node == root) {
         if (node.left == null) {
            root = node.left;
         }
         else {
            root = node.right;
         }
        
         // Make sure the new root, if non-null, has parent set to null
         if (root != null) {
            root.parent = null;
         }
      }
      
      // Case 3: Internal with left child only
      else if (node.left != null) {
         node.parent.replaceChild(node, node.left);
      }

      // Case 4: Internal with right child OR leaf
      else {
         node.parent.replaceChild(node, node.right);
      }
   }
   
   private void prepareForRemoval(Node node) {
      if (tryCase1(node)) {
         return;
      }

      Node sibling = node.getSibling();
      if (tryCase2(node, sibling)) {
         sibling = node.getSibling();
      }
      if (tryCase3(node, sibling)) {
         return;
      }
      if (tryCase4(node, sibling)) {
         return;
      }
      if (tryCase5(node, sibling)) {
         sibling = node.getSibling();
      }
      if (tryCase6(node, sibling)) {
         sibling = node.getSibling();
      }

      sibling.nodeColor = node.parent.nodeColor;
      node.parent.nodeColor = Node.Color.BLACK;
      if (node == node.parent.left) {
         sibling.right.nodeColor = Node.Color.BLACK;
         rotateLeft(node.parent);
      }
      else {
         sibling.left.nodeColor = Node.Color.BLACK;
         rotateRight(node.parent);
      }
   }
   
   
   
   // Searches for a node with a matching key
   public Node search(E desiredKey) {
      Node currentNode = root;
      while (currentNode != null) {
         // Return the node if the key matches
         if (currentNode.key.compareTo(desiredKey) == 0) {
            return currentNode;
         }
         
         // Navigate to the left if the search key is 
         // less than the node's key.
         else if (currentNode.key.compareTo(desiredKey) > 0) { //check comparison
            currentNode = currentNode.left;
         }
         
         // Navigate to the right if the search key is 
         // greater than the node's key.
         else {
            currentNode = currentNode.right;
         }
      }
      
      // The key was not found in the tree.
      return null;
   }
   
   
   
   //special verification methods
   private boolean isNullOrBlack(Node node) {
      if (node == null) {
         return true;
      }
      return node.isBlack();
   }
   
   private boolean isNotNullAndRed(Node node) {
      if (node == null) {
         return false;
      }
      return node.isRed();
   }

   private boolean tryCase1(Node node) {
      if (node.isRed() || node.parent == null) {
         return true;
      }
      return false; // node case 1
   }
   
   private boolean tryCase2(Node node, Node sibling) {
      if (sibling.isRed()) {
         node.parent.nodeColor = Node.Color.RED;
         sibling.nodeColor = Node.Color.BLACK;
         if (node == node.parent.left) {
            rotateLeft(node.parent);
         }
         else {
            rotateRight(node.parent);
         }
         return true;
      }
      return false; // not case 2
   }
   
   private boolean tryCase3(Node node, Node sibling) {
      if (node.parent.isBlack() && sibling.areBothChildrenBlack()) {
         sibling.nodeColor = Node.Color.RED;
         prepareForRemoval(node.parent);
         return true;
      }
      return false; // not case 3
   }
   
   private boolean tryCase4(Node node, Node sibling) {
      if (node.parent.isRed() && sibling.areBothChildrenBlack()) {
         node.parent.nodeColor = Node.Color.BLACK;
         sibling.nodeColor = Node.Color.RED;
         return true;
      }
      return false; // not case 4
   }
   
   private boolean tryCase5(Node node, Node sibling) {
      if (isNotNullAndRed(sibling.left)) {
         if (isNullOrBlack(sibling.right)) {
            if (node == node.parent.left) {
               sibling.nodeColor = Node.Color.RED;
               sibling.left.nodeColor = Node.Color.BLACK;
               rotateRight(sibling);
               return true;
            }
         }
      }
      return false; // not case 5
   }
   
   private boolean tryCase6(Node node, Node sibling) {
      if (isNullOrBlack(sibling.left)) {
         if (isNotNullAndRed(sibling.right)) {
            if (node == node.parent.right) {
               sibling.nodeColor = Node.Color.RED;
               sibling.right.nodeColor = Node.Color.BLACK;
               rotateLeft(sibling);
               return true;
            }
         }
      }
      return false; // not case 6
   }
}
