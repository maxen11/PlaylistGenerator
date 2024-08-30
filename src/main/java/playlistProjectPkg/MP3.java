package playlistProjectPkg;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

public abstract class MP3 {
	
	private int rating;
	private HashSet<String> categorySet = new HashSet<String>();
	private String title;
	private Mp3File mp3file;
	private ID3v2 id3v2Tag;
	private String filepath;
	
	//Creates an mp3 file and id3v2tag tied to the object for use in the program
	//A new tag is created if the file does not have one
	public MP3(String filepath) throws UnsupportedTagException, InvalidDataException, IOException{
		this.mp3file = new Mp3File(filepath);
		this.filepath = filepath;
		if (mp3file.hasId3v2Tag()) {
		  this.id3v2Tag = mp3file.getId3v2Tag();
		} else {
		  this.id3v2Tag = new ID3v24Tag();
		  this.mp3file.setId3v2Tag(id3v2Tag);
		}
	}
	
	public Mp3File getMp3file() {
		return mp3file;
	}
	
	public ID3v2 getId3v2Tag() {
		return id3v2Tag;
	}
	//Returns all of the objects current categories as one string
	public String getCategoryString() {
		String categoryString = "";
		for(String category: categorySet) 
			categoryString += category + " ";
		return categoryString;
	}
	//Adds one or more categories to the object
	public void addToCategorySet(String inValue) { 
		String[] categories = inValue.split(" ");
		if(categorySet.contains("NULL"))
			categorySet.remove("NULL");//Funkar ej för podcast
		
		for(String category: categories)
			categorySet.add(category.toUpperCase());
	}
	
	//Removes one or more categories from the object
	public void removeFromCategorySet(String inValue) {
		String[] categories = inValue.split(" ");
		for(String category: categories)
			categorySet.remove(category.toUpperCase());
	}

	public HashSet<String> getCategorySet() {
		return categorySet;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRating() {
		return rating;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getFilepath() {
		return filepath;
	}
	//Saves id3v2 data to file
	//By temporarily saving the file with a different name
	//and then using rename to overwrite the old file 
	//(the id3v2 save method cant overwrite)
	public void save() {
		try {
			getMp3file().save(getFilepath().replace(".mp3", "temp.mp3")); //Tom catch, se över
		} catch (NotSupportedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tempSplit = getFilepath().replace(".mp3","temp.mp3");
    	File someFile = new File(tempSplit);
    	File aFile = new File(getFilepath());
    	someFile.renameTo(aFile); 	
	}
	
}
