
// Interface for COSC 2436 Labs 3 and 4

/**
 * @param <E> The class of the items in the ordered list
 */

interface AddOnce <E extends Comparable<? super E>> { 
   
/**
 * This method searches the list for a previously added 
 * object, if an object is found that is equal (according to the 
 * compareTo method) to the current object, the object that was 
 * already in the list is returned, if not the new object is 
 * added, in order, to the list. 
 * 
 * @param an object to search for and add if not already present
 * 
 * @return either item or an equal object that is already on the    
 *         list
 */

    public E addOnce(E item);

}