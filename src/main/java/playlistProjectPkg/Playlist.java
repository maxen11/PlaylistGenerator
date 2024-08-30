package playlistProjectPkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Playlist {
	
	private HashSet<MP3> mp3Library = new HashSet<MP3>();
	private String mp3Fields;
	private String operators = "&&||!";
	private ArrayList<MP3> results;
	private MP3 mp3InMenuFor;
	
	//Sets the priority of the logical operators used in the internal search method
	private static int priority(String op) { //kanske ska ligga i Not_G_UI
		switch(op) {
		case("||"):
			return 1;
		case("&&"):
			return 1;
		case("!"):
			return 2;
		case("("):
			return 0;
		default:
			return -1;
		}
	}
	
	//Converts the logical search criteria from infix to postfix
	private ArrayList<String> toPostfix(String[] inputArr) { 
		Stack<String> stack = new Stack<String>();
		ArrayList<String> output = new ArrayList<String>();
		for(String element: inputArr) {
			if(operators.contains(element)) {
				if (stack.isEmpty()) {
					stack.push(element);
				} else {
					while(!stack.isEmpty() && priority(element)<=priority(stack.peek())) {
						output.add(stack.pop());
					}
					stack.push(element);
				}
			}
			else if(element.equals("(")) {
				stack.push(element);
			}else if(element.equals(")")) {
				while(!stack.peek().equals("(")) {
					output.add(stack.pop());
				}
				stack.pop();
			}
			else if(!element.equals("!")) {
				output.add(element);
				if(!stack.isEmpty()) {
					if(stack.peek().equals("!")) {
						output.add(stack.pop());
					}
				}
			}
		}
		while(!stack.isEmpty()) {
			output.add(stack.pop());
		}
		return output;
	}
	
	//Searches for mp3 files that match the logical search criteria
	//using boolean algebra
	public ArrayList<MP3> searchMp3Library(String input){
		String[] inputArray = input.split("(?<=!)|(?=\\&\\&)|(?<=\\&\\&)|(?=\\|\\|)|(?<=\\|\\|)|(?<=\\()|(?=\\))");
		for(int i = 0; i<inputArray.length;i++)
			inputArray[i] = inputArray[i].trim();
		ArrayList<MP3> results = new ArrayList<MP3>();
		ArrayList<String> postfixArray = toPostfix(inputArray);
		Stack<String> operandStack = new Stack<String>();
		for(MP3 mp3: mp3Library) {
			if(mp3 instanceof Song) {
				this.mp3Fields = ((Song) mp3).getAllSearchableFields();
			}
			else {
				this.mp3Fields = ((Podcast)mp3).getAllSearchableFields();
			}
			for(int i = 0; i < postfixArray.size(); i++) {
				String element = postfixArray.get(i);
				if(postfixArray.size() == 1) 
					if(operation(element,element,"||")) 
						operandStack.push("1");
					
					else 
						operandStack.push("0");
					
				
				else if(element.equals("!")) 
					if(operation("", operandStack.pop(), element))
						operandStack.push("1");
				
					else
						operandStack.push("0");
				
				else if(operators.contains(element)) 
					if(operation(operandStack.pop(), operandStack.pop(), element)) 
						operandStack.push("1");
				
					else
						operandStack.push("0");
				
				else 
					operandStack.push(element);
			}
			if(operandStack.peek().equals("1")) {
				results.add(mp3);
				operandStack.clear();
			}
		}
		return results;
	}
	//Calculates and returns the result of an operation as a 1 or a 0
	//(The three different sets of calculators are for handling the mix
	//of original invalues and booleans (Depicted in strings))
	private boolean operation(String second, String first, String op) {
		if(!(first.equals("1") || first.equals("0"))) {
			if(op.equals("||")) 
				return mp3Fields.contains(first) || mp3Fields.contains(second);
		
			if(op.equals("&&"))
				return mp3Fields.contains(first) && mp3Fields.contains(second);
		
			if(op.equals("!")) {
				return !(mp3Fields.contains(first));
			}
				
		}
		
		else if(second.equals("1") || second.equals("0")) {
			boolean firstBool = first.equals("1");
			boolean secondBool = second.equals("1");
			if(op.equals("||")) 
				return firstBool || secondBool;
		
			if(op.equals("&&"))
				return firstBool && secondBool;
		}
			
		else {
			boolean firstBool = first.equals("1");
			
			if(op.equals("||")) 
				return firstBool || mp3Fields.contains(second);
		
			if(op.equals("&&"))
				return firstBool && mp3Fields.contains(second);
		
			if(op.equals("!"))
				return !firstBool;
		}
		
		return false;
	}
	//Creates a playlist in the m3u format using logical search critera and a filepath
	public boolean createPlaylist(String filepath, String searchCriteria, ArrayList<MP3> searchResults) throws IOException {
		if(searchResults.size() == 0)
			return false;
		
		String fileName = searchCriteria.replaceAll(" \\&\\& | \\|\\| ", "_");
		fileName = fileName.replace("!", "not_") + ".m3u";
		File playlist = new File(filepath + fileName);
		
		try {
		playlist.createNewFile();
		}
		catch(IOException e) {
			return false;
		}
		String person;
		FileOutputStream save = new FileOutputStream(playlist);
		String m3uText = "#EXTM3U";
		
		for(MP3 mp3: searchResults) {
			if(mp3 instanceof Song) {
				person = ((Song) mp3).getArtist();
			}else {
				person = ((Podcast)mp3).getHost();
			}
			m3uText += ("\n\n#EXTINF:420, " + person + " - " + mp3.getTitle() + "\n" + mp3.getFilepath());
		}
		byte[] byteArray = m3uText.getBytes();
		save.write(byteArray);
		save.close();
		return true;
	}
	//Returns formatted strings (for each mp3 file found with 
	//the internal search method) in an ArrayList for drawing 
	//the menu showing the results of the internal search method
	public ArrayList<String> searchResults(String inValue) {
		results = searchMp3Library(inValue);
		ArrayList<String> resultFields = new ArrayList<String>();
		for(MP3 mp3: results) {
			if(mp3 instanceof Song) {
				Song song = (Song) mp3;
				String printval=song.getArtist() + " - ";
				printval+=song.getTitle() + " ";
				printval+="( "+song.getCategoryString() + ")";
				resultFields.add(printval);
			}
			else {
				Podcast podcast = (Podcast) mp3;
				String printVal = podcast.getHost() + " - ";
				printVal += podcast.getTitle()+ " ";
				printVal += "( " + podcast.getCategoryString() + ")";
				resultFields.add(printVal);
			}
		}
		return resultFields;
}
	//Gets the title of the mp3 file for that files own menu
	//to be used in that menus header
	public String getMenuForTitle(String choice) {
		List<String> alphabet = Arrays.asList("A","B","C","D","E","F","G","H","I","J");
		int index = (choice.length()-1)*11+alphabet.indexOf(String.valueOf(choice.charAt(0)));
		mp3InMenuFor = results.get(index);
		String menuTitle = "";
		if(mp3InMenuFor instanceof Song) {
			Song song = (Song)mp3InMenuFor;
			menuTitle += song.getArtist()+" - "+song.getTitle();
		} else {
			Podcast podcast = (Podcast)mp3InMenuFor;
			menuTitle += podcast.getHost()+" - "+podcast.getTitle();
		}
		return menuTitle;
	}
	
	//Calls methods to manipulate the data in a specific mp3 file
	public String specifiedMP3Options(String choice, String inValue) {
		switch(choice) {
		case("A"):
			if(mp3InMenuFor instanceof Song) {
				Song song = (Song)mp3InMenuFor;
				song.addToCategorySet(inValue);
				song.writeToFile();
			} else {
				Podcast podcast = (Podcast)mp3InMenuFor;
				podcast.addToCategorySet(inValue);
				podcast.writeToFile();
		}
			break;
		case("B"):
			if(mp3InMenuFor instanceof Song) {
				Song song = (Song)mp3InMenuFor;
				mp3InMenuFor.removeFromCategorySet(inValue);
				song.writeToFile();
			} else {
				Podcast podcast = (Podcast)mp3InMenuFor;
				podcast.removeFromCategorySet(inValue);
				podcast.writeToFile();
			}
			break;
		case("C"):
			mp3InMenuFor.setRating(Integer.parseInt(inValue));
			break;
		case("D"):
			if(mp3InMenuFor instanceof Song) { 
				Song song = (Song)mp3InMenuFor;
				song.setLyrics(inValue);
				song.writeToFile();
			} else {
				Podcast podcast = (Podcast)mp3InMenuFor;
				podcast.setDescription(inValue);
				podcast.writeToFile();
			}
			break;
		case("E"):
			String lyricsorDescription = "";
			if(mp3InMenuFor instanceof Song) { 
				Song song = (Song)mp3InMenuFor;
				lyricsorDescription = song.getLyrics();
			}else {
				Podcast podcast = (Podcast)mp3InMenuFor;
				lyricsorDescription = podcast.getDescription();
			}
			return lyricsorDescription;
		}
		return null;
	}
	
	//Initializes all currently available mp3 files using the filepaths
	//saved in the text file named "FilePaths"
	public HashSet<String> initmp3Library(HashSet<String> songFilepathSet, HashSet<String> podcastFilepathSet) throws UnsupportedTagException, InvalidDataException, IOException {
		HashSet<String> invalidFilepaths = new HashSet<String>();
		for(String filepath: songFilepathSet) {
			try {
				boolean valid = true;
				for(MP3 mp3: mp3Library) {
					if(mp3.getFilepath().equals(filepath)) {
						valid = false;
					}
				}
				if(valid) {
					Song song = new Song(filepath);
					mp3Library.add(song);// Lägger på en ny song, nytt objekt. när vi initar läggs allt in, sen när vi gör search och initar lägger vi på allt igen
				}
			} catch(Exception e) {
				invalidFilepaths.add(filepath);
			}
		}
		for(String filepath: podcastFilepathSet) {
			try {
				boolean valid = true;
				for(MP3 mp3: mp3Library) {
					if(mp3.getFilepath().equals(filepath)) {
						valid = false;
					}
				}
				if(valid) {
					Podcast podcast = new Podcast(filepath);
					mp3Library.add(podcast);
				}
			} catch(FileNotFoundException e) {
				invalidFilepaths.add(filepath);
			}
		}
		return invalidFilepaths;
	}
}
