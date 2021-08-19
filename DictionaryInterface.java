package hashedDictionary;

import java.util.Iterator;

/**
 * An interface for a dictionary with distinct search keys (no duplicate keys allowed).
 * Search keys and associated values are not null (null values not permitted for keys and values).
 * 
 * -- ALLOWING NULL VALUES --
 * null is used to signify that a search key does not exist, which is why null values are not permitted.
 * If null values were allowed, then an exception would have to be thrown instead of returning null for unsuccessful searches.
 * 
 * -- ALLOWING DUPLICATE KEYS --
 * Duplicate keys would require methods remove() and getValue() to deal with multiple entries with the same search key.
 * The method remove() could remove the first value associated with the key, or remove all values.
 * getValue() could return the first value it finds, or return a list of values.
 * Another possibility is to have a secondary search key, that is only used when multiple entries have the same primary 
 * search key.
 *
 * @param <K> Object type of the search key
 * @param <V> Object type of the value associated with the key.
 */

public interface DictionaryInterface<K, V> {
	
	/**
	 * Adds a new entry to this dictionary. If the given search key already exists in the dictionary, replaces the corresponding value.
	 * @param key An object search key of the new entry.
	 * @param value An object associated with the search key.
	 * @return Either null if the new entry was added to the dictionary or the value that was associated with key if that value was replaced.
	 */
	public V add(K key, V value);
	
	/**
	 * Removes a specific entry from this dictionary.
	 * @param key An object search key of the entry to be removed.
	 * @return Either the value associated with the search key or null if no such object exists.
	 */
	public V remove(K key);
	
	/**
	 * Retrieves from this dictionary the value associated with a given search key.
	 * @return Either the value that is associated with the search key or null if no such object exists.
	 */
	public V getValue(K key);
	
	/**
	 * Sees whether a specific entry is in this dictionary.
	 * @param key An object search key of the desired entry.
	 * @return True if the key is associated with an entry in the dictionary.
	 */
	public boolean contains(K key);
	
	/**
	 * Creates an iterator that traverses all search keys in this dictionary.
	 * @return An iterator that provides sequential access to the search keys in the dictionary.
	 */
	public Iterator<K> getKeyIterator();
	
	/**
	 * Creates an iterator that traverses all values in this dictionary.
	 * @return An iterator that provides sequential access to the values in this dictionary.
	 */
	public Iterator<V> getValueIterator();
	
	/**
	 * Sees whether this dictionary is empty.
	 * @return True if the dictionary is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Gets the size of this dictionary.
	 * @return The number of entries(key-value pairs) currently in the dictionary.
	 */
	public int getSize();
	
	/**
	 * Removes all entries in this dictionary.
	 */
	public void clear();

}
