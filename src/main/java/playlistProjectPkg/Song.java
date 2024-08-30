package playlistProjectPkg;

import java.io.IOException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Song extends MP3 implements LocalFile {

	private String lyrics;
	private String artist;

	public Song(String filepath) throws UnsupportedTagException, InvalidDataException, IOException {
		super(filepath);
		initializeFile();
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getLyrics() {
		return lyrics;
	}
	
	//Formats and sets lyrics
	public void setLyrics(String lyrics) {
		String[] descSplit = lyrics.split(" ");
		int charCounter = 0;
		String formattedLyr = "";
		for(String element : descSplit) {
			charCounter += element.length();
			if(charCounter > 20) {
				element += "\n";
				formattedLyr += element;
				charCounter = 0;
			}else {
				formattedLyr += element+" ";
			}
		}
		this.lyrics = formattedLyr;
	}
	//Returns the fields used for searchMp3Library() in one string
	public String getAllSearchableFields() {
		String allSearchableFields = super.getTitle();
		allSearchableFields += String.valueOf(super.getRating());
		allSearchableFields += artist;
		for(String category: super.getCategorySet()) 
			allSearchableFields += category;
		return allSearchableFields.toUpperCase();
	}
	//Takes all dynamic fields and saves them in id3v2
	@Override
	public void writeToFile() {
		String temp = "NULL";
		if(getCategorySet().size() > 0)
			temp = "";
		
		for(String Category: this.getCategorySet()) 
				temp+= Category+" ";
		
		super.getId3v2Tag().setComment(temp);
		super.getId3v2Tag().setGenre(getRating());
		super.getId3v2Tag().setLyrics(lyrics);
		
		save();
	}
	//Initializes all used id3v2 data into the program
	@Override
	public void initializeFile() { 
  	  	this.artist = super.getId3v2Tag().getArtist();
  	  	setTitle(super.getId3v2Tag().getTitle());
  	  	this.lyrics = super.getId3v2Tag().getLyrics();
  	  	setRating(super.getId3v2Tag().getGenre());
  	  	
  	  	String categories = "NULL";
  	  	if(super.getId3v2Tag().getComment() != null) {
  	  		if(!super.getId3v2Tag().getComment().equals("NULL")) {
  	  			categories = super.getId3v2Tag().getComment();
  	  		}
  	  	} 
  	  		addToCategorySet(categories.toUpperCase());
	}
}
