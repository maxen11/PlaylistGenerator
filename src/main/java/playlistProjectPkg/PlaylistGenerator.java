package playlistProjectPkg;

import java.io.IOException;
import java.util.ArrayList;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class PlaylistGenerator {
	
	Playlist playlist = new Playlist();
	CategoryList categoryList = new CategoryList();
	UI ui = new UI();
	FilepathList filepathList = new FilepathList();
	
	//Initializes all fields required in the program
	public void start() throws IOException, UnsupportedTagException, InvalidDataException {
		categoryList.init();
		filepathList.init();
		filepathList.removeFromFilepathSet(playlist.initmp3Library(filepathList.getSongFilepathSet(), filepathList.getPodcastFilepathSet()));
		filepathList.save();
		menuTop(false);
	}
	//Code skeleton that handles calls to other methods in the main menu
	public void menuTop(boolean error) throws IOException, UnsupportedTagException, InvalidDataException {
		ui.drawTopMenu();
		String choice = ui.inValue("Choice: ",error).toUpperCase();
		switch(choice) {
		case ("A"):
			menuSearch(false);
			break;
		case ("B"):
			String searchCriteria = ui.inValue("\nInput search criteria: ", false);
			ArrayList<MP3> results = playlist.searchMp3Library(searchCriteria.toUpperCase());
			String filepath = ui.inValue("Enter filepath: ", false);
			boolean success = playlist.createPlaylist(filepath, searchCriteria, results);
			if(success) {
				ui.print("\nPlaylist successfully created!");
				ui.inValue("Press Enter to continue", false);
			}else if (!success && results.size() == 0){
				ui.print("\nSearch criteria did not match any mp3 files!");
				ui.inValue("Press Enter to continue", false);
			}else if(!success) {
				ui.print("\nInvalid filepath!");
				ui.inValue("Press Enter to continue", false);
			}
			menuTop(false);
			break;
		case ("C"):
			menuSearchResults(); 
			break;
		case("D"):
			menuChangeCategory(false);
			break;
		default:
			menuTop(true);
		}
	}	
	
	//Code skeleton that handles calls to other methods in the menu for the internal search results
	public void menuSearchResults() throws IOException, UnsupportedTagException, InvalidDataException { //Lägg till back
		String criteria = ui.inValue("\nInput search criteria: ", false).toUpperCase();
		ArrayList<String> usedMenuAlternatives = ui.drawMenuSearchResults(playlist.searchResults(criteria), criteria);
		String choice = ui.inValue("\nChoice: ", false).toUpperCase();

		while(!(usedMenuAlternatives.contains(choice))) //Ful implementation, risk för fel //Gör contains med en arraylist
			choice = ui.inValue("\nChoice: ", true).toUpperCase();
		if(choice.equals(usedMenuAlternatives.get(usedMenuAlternatives.size()-1)))
			menuTop(false);
		else
		menuFor(false, choice);
	}
	
	//Checks if entered categories are allowed (menu for a specific mp3)
	public String menuForAdd(String inValue) {
		
		String[] splitInValues = inValue.split(" ");
		boolean enumFound;
		String invalidCategories = "";
		String output = "";
		for(String splitInValue: splitInValues) {
			try {
				Categories.valueOf(splitInValue.toUpperCase());
				enumFound = true;
			}
			catch(IllegalArgumentException e) {
				enumFound = false;
			}
			if(enumFound || categoryList.getCategoryListSet().contains(splitInValue)) 
				output += splitInValue + " ";
			else 
				invalidCategories += splitInValue + " ";
			
		}
		if(invalidCategories.length() > 0)
			ui.print("Invalid categories: "+invalidCategories);// formatera
		return output;
	}
	
	//Code skeleton that handles calls to other methods in the menu for a specific mp3
	public void menuFor(boolean error, String index) throws IOException, UnsupportedTagException, InvalidDataException { //Alldeles för lång fixa
		ui.drawMenuFor(playlist.getMenuForTitle(index));
		String choice = ui.inValue("\nChoice: ", error).toUpperCase();
		String inValue;
		switch(choice) {
		case("A"):
            String output = menuForAdd(ui.inValue("Enter the categories you want to add: ", false).toUpperCase());
			if(output.length() > 0) {
				playlist.specifiedMP3Options(choice, output);
				ui.print("Added categories: " + output);
				ui.inValue("Press Enter to continue", false);
				menuFor(false, index);
			}else 
				menuFor(true,index);
            break;
		case("B"):
			playlist.specifiedMP3Options(choice, ui.inValue("Enter the categories you want to remove: ", false));
			menuFor(false, index);
			break;
			
		case("C"):
			inValue  = ui.inValue("Enter rating: ", false);
			if(inValue.matches("[1-5]")) {
				playlist.specifiedMP3Options(choice, inValue);
				menuFor(false, index);
			} else 
				menuFor(true,index);
			
			break;
		case("D"):
			playlist.specifiedMP3Options(choice, ui.inValue("Enter lyrics/description: ", false));
			menuFor(false, index);
			break;
		case("E"):
			ui.print("\nDescription/Lyrics:\n\n" + playlist.specifiedMP3Options(choice, null));
			ui.inValue("Press Enter to continue", false);
			menuFor(false, index);
			break;
		case("F"):	
			menuTop(false);
			break;
		default:
			menuFor(true, index);
			break;
		}
	}
	//Code skeleton that handles calls to other methods in the menu for handling the programs category database
	public void menuChangeCategory(boolean error) throws IOException, UnsupportedTagException, InvalidDataException {
		ui.drawChangeCategories();
		String choice = ui.inValue("Choice: ", error).toUpperCase();
		switch(choice) {
		case ("A"):
			String resultAdd = categoryList.addNewCategories(ui.inValue("Enter the categories you want to add: ", false).toUpperCase());
			if(resultAdd.length() > 0) {
				ui.print(resultAdd);
				ui.inValue("Press Enter to continue", false);
			}
			menuChangeCategory(false);
			break;
		case ("B"):
			String resultRem = categoryList.removeCategories(ui.inValue("Specify category to remove: ", false).toUpperCase());
			if(resultRem.length() > 0) {
				ui.print(resultRem);
				ui.inValue("Press Enter to continue", false);
			}
			menuChangeCategory(false);
			break;
		case ("C"):
			ui.print(categoryList.getFormattedCategoryList());
			ui.inValue("Press Enter to continue", false);
			menuChangeCategory(false);
			break;
		case("D"):
			menuTop(false);
			break;
		default:
			menuChangeCategory(true);
		}
	}
	
	//Code skeleton that handles calls to other methods in the menu for adding mp3 files to the program
	public void menuSearch(boolean error) throws IOException, UnsupportedTagException, InvalidDataException {
		ui.drawMenuSearch();
		String choice = ui.inValue("Choice: ", error).toUpperCase();
		boolean isSong = true;
		switch(choice) {
		case ("A"):
			isSong = true;
			boolean error2 = true;
			String inValue = ui.inValue("Enter filepath: ", !error2);
			while(error2) {
				if(filepathList.search(inValue, isSong)) {
					ui.print("Found "+filepathList.getCounter()+" mp3 files");
					filepathList.counterReset();
					error2 = false;
					filepathList.save();
					ui.inValue("Press Enter to continue", false);
					menuSearch(false);
				} else {
					inValue = ui.inValue("Enter filepath: ", error2);
					}
			}
			break;
		case ("B"):
			isSong = false;
			error2 = true;
			inValue = ui.inValue("Enter filepath: ", !error2);
			while(error2) {
				if(filepathList.search(inValue, isSong)) {
					ui.print("Found "+filepathList.getCounter()+" mp3 files");
					filepathList.counterReset();
					error2 = false;
					filepathList.save();
					ui.inValue("Press Enter to continue", false);
					menuSearch(false);
				} else {
					inValue = ui.inValue("Enter filepath: ", error2);
				}
			}
			break;
		case ("C"):
			playlist.initmp3Library(filepathList.getSongFilepathSet(), filepathList.getPodcastFilepathSet());
			menuTop(false);
			break;
		default:
			menuSearch(true);
		}

	}
}
