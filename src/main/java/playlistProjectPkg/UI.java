package playlistProjectPkg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UI {
	
	private Scanner input = new Scanner(System.in);
	
	public void drawTopMenu() {
		
		for(byte i = 0; i < 20; i++)
			System.out.println();
		
		System.out.println("Home\n====");
		System.out.println("A)  Traverse file structure and search for MP3:s");
		System.out.println("B)  Create a playlist");
		System.out.println("C)  Search for a MP3 file");
		System.out.println("D)  Change categories");
		
		}	

	public void drawChangeCategories() {
		for(byte i = 0; i < 20; i++)
			System.out.println();
		
		System.out.println("Change categories\n=================");
		System.out.println("A)  Add new categories");
		System.out.println("B)  Remove categories");
		System.out.println("C)  List all categories");
		System.out.println("D)  Back");	
		
	}
	
	public void drawMenuSearch() {
		for(byte i = 0; i < 20; i++)
			System.out.println();
		
		System.out.println("Search for .mp3 files\n=====================");
		System.out.println("A) Search for songs");
		System.out.println("B) Search for podcasts");
		System.out.println("C) Back");
	}
	//Draws the results from the internal search method using the ArrayList of formatted strings,
	//generates the menu entry letters and returns a list of them
	public ArrayList<String> drawMenuSearchResults(ArrayList<String> results, String menuTitle) {
		List<String> alphabet = Arrays.asList("A","B","C","D","E","F","G","H","I","J");
		String usedMenuAlternatives = new String();
		ArrayList<String> usedMenu = new ArrayList<String>();
		results.add("Back");
		for(byte i = 0; i < 20; i++)
			System.out.println();
		
		System.out.println("Search Results for: "+menuTitle+"\n"+"=".repeat(menuTitle.length()+20));
		for(int i = 0; i < results.size(); i++) {
			 String choice = alphabet.get(i%10).repeat((int)Math.ceil((double)(i+1)/10.0));
			 usedMenu.add(choice);
			 System.out.println(choice + ") " + results.get(i));
		}
		return usedMenu;

	}
	
	public void drawMenuFor(String menuTitle) {
		for(byte i = 0; i < 20; i++)
			System.out.println();
		
		System.out.println("Menu for "+menuTitle+"\n"+"=".repeat(menuTitle.length()+9));
		System.out.println("A) Add new categories");
		System.out.println("B) Remove categories");
		System.out.println("C) Rate MP3");
		System.out.println("D) Set description/lyrics");
		System.out.println("E) View description/lyrics");
		System.out.println("F) Back");
	}
	//Prints an entered string and returns a scanner value
	//Also has the ability to print an error if desired
	public String inValue(String str, boolean error) {
		if(error == true)
			System.out.print("Invalid input!\n"+str);
		else
			System.out.print(str);
		return input.nextLine();
	}
	
	public void print(String str) {
		System.out.println(str);
		
	}

}
