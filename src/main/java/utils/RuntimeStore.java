package utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Architected with Thread-confined Singleton = Prevents inheritance + ThreadLocal-based key-value store
 * [Lazy initialization + encapsulated access + explicit cleanup + thread-safe storage for runtime data]
 */

public final class RuntimeStore {

    private static final ThreadLocal<Map<String,Object>> storeThread = ThreadLocal.withInitial(HashMap::new);

    private RuntimeStore() {}

    /** Puts a key-value pair into the thread-local store */
    public static void put(String key, Object value) {
        storeThread.get().put(key, value);
    }

    /** Retrieves the value for the given key from the thread-local store */
    public static Object get(String key) {
        return storeThread.get().get(key);
    }

    /** Checks if the key exists in the thread-local store */
    public static boolean contains(String key) {
        return storeThread.get().containsKey(key);
    }

    /** Returns an unmodifiable set of all keys in the thread-local store */
    public static Set<String> getKeys() {
        return Collections.unmodifiableSet(storeThread.get().keySet());
    }

    /** Clears and removes the thread-local store for the current thread */
    public static void clearStoreThread() {
        storeThread.get().clear();
        storeThread.remove();
    }
}