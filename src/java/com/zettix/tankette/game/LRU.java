/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import java.util.LinkedHashMap;
import java.util.Map;

/** LRU: Last Recently Used Cache Hash Map.
 *  
 * Will maintain initial size and not get bigger.
 * Using Java's internal LRU code.
 *
 * @author sean
 * @param <K> Key type for LRU
 * @param <V> Value type for LRU
 */
public class LRU<K,V> extends LinkedHashMap<K, V> {
    
    private static final int DEFAULT_SIZE = 100;
    //private final int initial;
    private final int sz;

    public LRU() {
        this(16, 0.75f, DEFAULT_SIZE);
    }

    public LRU(int initialCapacity) {
        this(initialCapacity, 0.75f, DEFAULT_SIZE);
    }

    public LRU(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, DEFAULT_SIZE);
    }

    public LRU(int initialCapacity, float loadFactor, int size) {
        super(initialCapacity, loadFactor, true);
        sz = size;
    }

    public LRU(Map<? extends K,? extends V> m) {
        //Constructs an insertion-ordered LinkedHashMap instance with the same mappings as the specified map.
        super(16, 0.75f, true);
        sz = DEFAULT_SIZE;
        putAll(m);
      }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        //System.out.println("size:" + size() + " limit:" + sz);
        return size() > sz;
    }
}