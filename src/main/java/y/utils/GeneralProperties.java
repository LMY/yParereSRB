package y.utils;

import java.util.HashMap;
import java.util.Map;

public class GeneralProperties<KeyType> {
	Map<Class<?>, Map<KeyType, ?>> maps;
	
	public GeneralProperties() {
		maps = new HashMap<Class<?>, Map<KeyType, ?>>();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, KeyType key) {
		final Map<KeyType, ?> themap = maps.get(type);
		return themap == null ? null : (T) themap.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrDefault(Class<T> type, KeyType key, T default_value) {
		final Map<KeyType, ?> themap = maps.get(type);
		if (themap == null)
			return null;
		
		final T value = (T) themap.get(key);
		return value != null ? value : default_value;
	}
	
	public <T> void set(KeyType key, T value) {
		set(value.getClass(), key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T, V> void set(Class<V> type, KeyType key, T value) {
		Map<KeyType, V> themap = (Map<KeyType, V>) maps.get(type);
		if (themap == null) {
			themap = new HashMap<KeyType, V>();
			maps.put(type, themap);
		}
		
		((Map<KeyType, V>) themap).put(key, (V) value);
	}
}
