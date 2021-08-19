package hashedDictionary;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * @param <K> Object type of the search key
 * @param <V> Object type of the value associated with the key.
 */
public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {
	
	// the dictionary
	private int numberOfEntries;
	private static final int DEFAULT_CAPACITY = 7;
	private static final int MAX_CAPACITY = 10000;
	
	// the hash table
	private LinkedList<Entry>[] hashTable;						// array of buckets (linked lists)
	private int tableSize;										// must be prime
	private static final int MAX_SIZE = 2 * MAX_CAPACITY;		// max number of buckets in hash table
	private boolean integrityOK = false;
	private static final double MAX_LOAD_FACTOR = 0.75;			// fraction of hash table that can be filled
	
	public HashedDictionary() {
		this(DEFAULT_CAPACITY);
	}
	
	public HashedDictionary(int initialCapacity) {
		initialCapacity = checkCapacity(initialCapacity);
		numberOfEntries = 0;
		
		// set hash table size to initialCapacity if it is prime
		// otherwise increase it until it is prime
		tableSize = getNextPrime(initialCapacity);
		checkSize(tableSize);
		
		// The cast is safe because the new array contains null entries
		@SuppressWarnings("unchecked")
		LinkedList<Entry>[] temp = (LinkedList<Entry>[])new LinkedList[tableSize];
		hashTable = temp;
		integrityOK = true;
	}

	@Override
	public V add(K key, V value) {
		checkIntegrity();
		if(key == null || value == null)
			throw new IllegalArgumentException();
		int index = getHashIndex(key);
		V replacedValue = null;
		Entry newEntry = new Entry(key, value);
		LinkedList<Entry> bucket;
		if(hashTable[index] == null) {
			bucket = new LinkedList<>();
			bucket.add(newEntry);
			hashTable[index] = bucket;
			numberOfEntries++;
		} else {
			// a bucket already exists in that index
			bucket = hashTable[index];
			// update the entry in the bucket if the entry already exists
			if(bucket.contains(newEntry)) {
				Iterator<Entry> it = bucket.iterator();
				Entry nextEntry;
				while(it.hasNext()) {
					nextEntry = it.next();
					if(nextEntry.equals(newEntry)) {
						replacedValue = nextEntry.getValue();
						nextEntry.setValue(newEntry.getValue());
						break;
					}
				}
			} else {
				// bucket does not contain the entry; add the entry to this bucket
				bucket.add(newEntry);
				numberOfEntries++;
			}
		}
		// ensure hash table is large enough for another addition
		if(isHashTableTooFull())
			enlargeHashTable();
		return replacedValue;
	}

	@Override
	public V remove(K key) {
		checkIntegrity();
		V removedValue = null;
		int index = getHashIndex(key);
		LinkedList<Entry> bucket = hashTable[index];
		if(bucket != null) {
			Iterator<Entry> it = bucket.iterator();
			while(it.hasNext()) {
				Entry nextEntry = it.next();
				if(nextEntry.getKey().equals(key)) {
					removedValue = nextEntry.getValue();
					bucket.remove(nextEntry);
					numberOfEntries--;
				}
			}
		}
		return removedValue;
	}

	@Override
	public V getValue(K key) {
		checkIntegrity();
		V result = null;
		int index = getHashIndex(key);
		LinkedList<Entry> bucket = hashTable[index];
		if(bucket != null) {
			Iterator<Entry> it = bucket.iterator();
			while(it.hasNext()) {
				Entry nextEntry = it.next();
				if(nextEntry.getKey().equals(key))
					result = nextEntry.getValue();
			}
		}
		return result;
	}

	@Override
	public boolean contains(K key) {
		checkIntegrity();
		for(int i = 0; i < tableSize; i++) {
			if(hashTable[i] != null) {
				LinkedList<Entry> bucket = hashTable[i];
				Iterator<Entry> it = bucket.iterator();
				while(it.hasNext()) {
					Entry nextEntry = it.next();
					if(nextEntry.getKey().equals(key))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public Iterator<K> getKeyIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<V> getValueIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return (numberOfEntries == 0);
	}

	@Override
	public int getSize() {
		return numberOfEntries;
	}

	@Override
	public void clear() {
		checkIntegrity();
		@SuppressWarnings("unchecked")
		LinkedList<Entry>[] temp = (LinkedList<Entry>[]) new LinkedList[tableSize];
		hashTable = temp;
		numberOfEntries = 0;
	}
	
	private void enlargeHashTable() {
		// expand table size to the next prime number after doubling size
		// add current entries to larger hash table (rehash entries)
		tableSize = getNextPrime(tableSize * 2);
		checkSize(tableSize);
		@SuppressWarnings("unchecked")
		LinkedList<Entry>[] newHashTable = (LinkedList<Entry>[]) new LinkedList[tableSize];
		for(int i = 0; i < hashTable.length; i++) {
			if(hashTable[i] != null) {
				LinkedList<Entry> bucket = hashTable[i];
				Iterator<Entry> it = bucket.iterator();
				while(it.hasNext()) {
					Entry nextEntry = it.next();
					int index = getHashIndex(nextEntry.getKey());
					if(newHashTable[index] == null)
						newHashTable[index] = new LinkedList<>();
					newHashTable[index].add(nextEntry);
				}
			}
		}
		hashTable = newHashTable;
		newHashTable = null;
	}

	private boolean isHashTableTooFull() {
		double loadFactor = (double)numberOfEntries / (double)tableSize;
		if(loadFactor > MAX_LOAD_FACTOR)
			return true;
		return false;
	}

	private void checkIntegrity() {
		if(!integrityOK)
			throw new IllegalStateException();
	}

	private int getHashIndex(K key) {
		int hashIndex = key.hashCode() % tableSize;
		if(hashIndex < 0)
			hashIndex = hashIndex + tableSize;
		return hashIndex;
	}

	private int getNextPrime(int num) {
		if(num <= 1)
			return 2;
		if(num % 2 == 0)	// ignore even numbers
			num++;
		// an odd integer 5 or greater is prime if it is not divisible by
		// every odd integer up to its square root
		boolean isPrime;
		while(true) {
			isPrime = true;
			int numSqrt = (int) Math.sqrt(num);
			for(int i = 3; i <= numSqrt; i+=2) {
				if(num % i == 0)
					isPrime = false;
			}
			if(isPrime)
				return num;
			else
				num += 2;
		}
	}

	private int checkCapacity(int initialCapacity) {
		if (initialCapacity < 0 || initialCapacity > MAX_CAPACITY)
			throw new IllegalArgumentException();
		return initialCapacity;
	}
	
	private void checkSize(int tableSize) {
		if(tableSize > MAX_SIZE)
			throw new IllegalArgumentException();
	}
	
	// any subclass of HashedDictionary will have access to Entry
	protected class Entry {
		private K key;
		private V value;
		
		Entry(K searchKey, V dataValue) {
			key = searchKey;
			value = dataValue;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null)
				return false;
			if(obj.getClass() != this.getClass())
				return false;
			@SuppressWarnings("unchecked")
			Entry other = (Entry) obj;
			if(!this.key.equals(other.key))
				return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int hash = 3;
			hash = 53 * hash + (this.key != null ? this.key.hashCode() : 0);
			return hash;
		}
	}

}
