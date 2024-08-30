package playlistProjectPkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class FilepathList {
	private HashSet<String> songFilepathSet = new HashSet<String>();
	private HashSet<String> podcastFilepathSet = new HashSet<String>();
	private File filepaths = new File("FilePaths.txt");
	private int counter;
	
	//Recursively searches a file structure for mp3 files using an absolute filepath
	public boolean search(String filepath, boolean isSong) throws IOException {
		try {
		File dir = new File(filepath);
		for(File file: dir.listFiles()) {
			if(file.isDirectory() && file.getAbsolutePath() != filepath) {
				search(file.getAbsolutePath(), isSong);
			}else if(file.toString().contains(".mp3")) {
				counter++;
				if(isSong)
					songFilepathSet.add(file.getAbsolutePath());
				else
					podcastFilepathSet.add(file.getAbsolutePath());
			}
		}
		}catch(NullPointerException e) {
			return false;
		}
		save();
		return true;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void counterReset() {
		this.counter = 0;
	}

	public HashSet<String> getSongFilepathSet() {
		return songFilepathSet;
	}
	//Removes filepaths from the library of current mp3:s
	public void removeFromFilepathSet(HashSet<String> invalidFilepaths) {
		for(String filepath: invalidFilepaths) {
			songFilepathSet.remove(filepath);
			podcastFilepathSet.remove(filepath);
		}
	}

	public HashSet<String> getPodcastFilepathSet() {
		return podcastFilepathSet;
	}
	
	//Initializes the set of filepaths by reading from the
	//saved text file and putting them into their respective sets
	public void init() throws IOException {
		FileInputStream load = new FileInputStream(filepaths);
		
		int characterInt;
		String str = "";
		boolean isPodcast = false;
		while((characterInt = load.read()) != -1) {
			char character = (char)characterInt;
			
			if(str.contains(".mp3") && isPodcast) {
				podcastFilepathSet.add(str);
				str = "";
			}
			
			else if(str.contains(".mp3") && !isPodcast) {
				songFilepathSet.add(str);
				str = "";
			}
			
			else {
				str+=character;
			}
			if(str.contains("|")) {
				isPodcast = true;
				str = "";
			}
		}
	}
	
	//Saves the filepaths from the sets into the text file
	public int save() throws IOException {
		File filepathFile = new File("src/main/java/playlistProjectPkg/FilePaths.txt"); 
		
		FileOutputStream save = new FileOutputStream(filepathFile);
		String temp = "";
		byte[] byteArray;
		
		for(String filepath : songFilepathSet) {
			byteArray = (filepath + "\n").getBytes();
			save.write(byteArray);	
		}
		byteArray = ("|").getBytes();
		save.write(byteArray);	
		
		for(String filepath: podcastFilepathSet) {
			byteArray = (filepath + "\n").getBytes();
			save.write(byteArray);	
		}
		save.close();
		return songFilepathSet.size();
	}
}
