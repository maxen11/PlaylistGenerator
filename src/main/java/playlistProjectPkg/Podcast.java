package playlistProjectPkg;

import java.io.IOException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Podcast extends MP3 implements LocalFile{

	private String description;
	private String host;
	
	public Podcast(String filepath) throws UnsupportedTagException, InvalidDataException, IOException {
		super(filepath);
		initializeFile();
	}
	
	public String getDescription() {
		return description;
	}
	
	//Formats and sets description
	public void setDescription(String description) {
		String[] descSplit = description.split(" ");
		int charCounter = 0;
		String formattedDesc = "";
		for(String element : descSplit) {
			charCounter += element.length();
			if(charCounter > 50) {
				element+="\n";
				formattedDesc+=element;
			}else {
				formattedDesc+=element+" ";
			}
		}
		this.description = formattedDesc;
	}

	public String getHost() {
		return host;
	}
	
	//Returns the fields used for searchMp3Library() in one string
	public String getAllSearchableFields() {
		String allSearchableFields = super.getTitle();
		allSearchableFields +=  String.valueOf(super.getRating());
		allSearchableFields += host;
		for(String category: super.getCategorySet()) 
			allSearchableFields += category;
		return allSearchableFields.toUpperCase();
	}
	
	//Takes all dynamic fields and saves them in id3v2
	@Override
	public void writeToFile() {
		super.getId3v2Tag().setComment("PODCAST");
		String temp = "PODCAST";
		if(getCategorySet().contains("PODCAST")) 
			temp = "";
		
		for(String Category: this.getCategorySet()) 
				temp+= Category+" ";
		
		super.getId3v2Tag().setComment(temp);
		super.getId3v2Tag().setGenre(getRating());
		super.getId3v2Tag().setLyrics(description);
		
		save();
	}
	
	//Initializes all used id3v2 data into the program
	@Override
	public void initializeFile() {
		this.host = super.getId3v2Tag().getArtist();
  	  	setTitle(super.getId3v2Tag().getTitle());
  	  	this.description = super.getId3v2Tag().getLyrics();
  	  	setRating(super.getId3v2Tag().getGenre());
  	  	String categories = "PODCAST";
  	  	if(super.getId3v2Tag().getComment() != null) {
  	  			categories = super.getId3v2Tag().getComment();
  	  	} 
  	  		addToCategorySet(categories.toUpperCase());
	}
}
