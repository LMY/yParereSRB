package y.utils;

import java.io.File;

public class LastUsedFolder {
	private static LastUsedFolder instance;
	public static LastUsedFolder getInstance() { return instance; }

	public static LastUsedFolder init(String path) { 
		instance = new LastUsedFolder(path, path);
		return instance;
	}
	
	private String path;
	private String defaultPath;
	
	private LastUsedFolder(String path, String defaultPath) {
		this.path = path;
		this.defaultPath = defaultPath;
	}
	
	public synchronized String get() {
		return new File(path).exists() ? path : defaultPath;	// se lastUsedFolder non esiste più, torna workingFolder
	}
	public synchronized void set(String path) {
		this.path = path;
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}
}
