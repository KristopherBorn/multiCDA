package org.eclipse.emf.henshin.multicda.cda.eval;

import java.io.File;

public class ClearLogDirectories {
	public static void main(String[] args) {
		File file1 = new File("logs/results");
		File file2 = new File("logs/time");
		deleteFolder(file1);
		deleteFolder(file2);
		System.out.println("Cleared log directories.");
	}
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	}
}
