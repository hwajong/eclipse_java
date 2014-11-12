import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MyJHUHashMap<K, V> implements JHUHashMap<K, V> {
    
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    
    private static final float DEFAULT_LOAD_FACTOR = 0.5f;
    
    private final float loadFactor;
    
    private int size;
    
    private int threshold;
    
    private Item<K,V>[] table;
    
    // no-argument constructor
    public MyJHUHashMap() {
	this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    
    // 2-argument constructor
    public MyJHUHashMap(int capacity, float loadFactor) {
	
	if(capacity < 0) {
	    throw new IllegalArgumentException("Illegal capacity: " + capacity);	    
	}
	
	if(loadFactor <= 0) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);	    
	}
	
	this.loadFactor = loadFactor;
	
	size = 0;
	
	threshold = (int)(capacity * loadFactor);

	table = new Item[capacity];
    }

    // -- linear probing for collision resolution, with step size 1
    private int searchItem(Object key)
    {
	int hashcode = Math.abs(key.hashCode());
	int index = hashcode % table.length;
//	System.out.println("hashcode : " + hashcode);
//	System.out.println("table.length : " + table.length);
//	System.out.println("index : " + index);
	
	while(true) {
	    Item<K, V> item = table[index];
	    if(item == null || (item.hash == hashcode && item.key.equals(key))) {
		break;
	    }
	    
	    index++;
	    if(index == table.length) {
		index = 0;
	    }
	}
	
	return index;
    }
    
    
    @Override
    public V put(K key, V value) {
	
	int index = searchItem(key);
	
	V oldValue = null;
	if(table[index] == null) {
	    table[index] = new Item<K, V>(key, value, Math.abs(key.hashCode()));
	    
	    // resizing & rebuild
	    if(++size >= threshold) {
		Item<K,V>[] table_old = table;
		
		// resizing
		table = new Item[2 * table_old.length];
		threshold = (int)(table.length * loadFactor);
		clear();
		
		// rebuild
		for(int i=0; i<table_old.length; i++) {
		    if(table_old[i] != null) {
			put(table_old[i].key, table_old[i].value);
		    }
		}
		Arrays.fill(table_old, null);
		table_old = null;
	    }		    
	}
	else {
	    oldValue = table[index].value;
	    table[index].value = value;
	}
	    
	return oldValue;
    }

    @Override
    public V get(Object key) {
	int index = searchItem(key);
	if(table[index] != null) {
	    return table[index].value;
	}
	
	return null;
    }

    @Override
    public boolean containsKey(Object key) {
	int index = searchItem(key);
	return table[index] != null;
    }

    @Override
    public V remove(Object key) {
	V oldValue = null;
	int index = searchItem(key);
	if(table[index] != null) {
	    oldValue = table[index].value; 
	    table[index] = null;
	    size--;
	}
	
	return oldValue;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    @Override
    public int size() {
	return size;
    }

    @Override
    public Set<K> keySet() {
	Set<K> ks = new HashSet<K>();
	
	for(int i=0; i<table.length; i++) {
	    if(table[i] != null) {
		ks.add(table[i].key);
	    }
	}
	
	return ks;
    }

    @Override
    public Collection<V> values() {
	Collection<V> list = new ArrayList<V>();
	
	for(int i=0; i<table.length; i++) {
	    if(table[i] != null) {
		list.add(table[i].value);
	    }
	}	

	return list;
    }

    static class Item<K, V> {
	private final K key;
	private V value;
	private int hash;
	
	Item(K key, V value, int hash) {
	    this.key = key;
	    this.value = value;
	    this.hash = hash;
        }
	
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
        
        public final String toString() {
            return key + "=" + value;
        }
    }
    
    // TEST
    public static void main(String args[]) {
	JHUHashMap<Integer, String> map = new MyJHUHashMap<Integer, String>();
	map.put(1, "ohj");
	map.put(2, "KKK");
	map.put(1, "aaa");
	map.put(2, "bbb");
	map.put(3, "ccc");
	map.put(4, "ddd");
	map.put(5, "eee");
	map.put(6, "fff");
	map.put(7, "ggg");
	map.put(7, "gg1");
	map.put(7, "gg2");
	map.put(7, "gg3");
	map.put(8, "hhh");
	map.put(9, "iii");
	map.put(10, "jjj");
	map.put(11, "kkk");
	map.put(12, "lll");
	map.put(13, "mmm");
	
	
	Set<Integer> ks = map.keySet();
	for(Integer key : ks) {
	    System.out.println("key : " + key + "   value : " + map.get(key));    
	}
	
	System.out.println("map size : " + map.size());
    }

}
