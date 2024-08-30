package playlistProjectPkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class CategoryList{
	
	private HashSet<String> categoryListSet = new HashSet<String>();
	private File categoryListFile = new File("CategoryList.txt");
	
	//Gets and returns all currently available categories in the program
	//with a reader friendly format for printing
	public String getFormattedCategoryList() { 
		String categoryString = "\nPreset categories:\n";
		for(Categories category: Categories.values()) {
			categoryString += category.toString() + " ";
		}
		categoryString += "\n\nUser added categories: \n";
		for(String category: categoryListSet) {
			categoryString += category + " ";
		}
		return categoryString += "\n";
	}
	
	public HashSet<String> getCategoryListSet() {
		return categoryListSet;
	}
	//Initializes the set of available categories by reading from the "internal" text file
	public void init() throws IOException {
		FileInputStream load = new FileInputStream(categoryListFile);
		
		int characterInt;
		String str = "";
		while((characterInt = load.read()) != -1) {
			char character = (char)characterInt;
			if(Character.isWhitespace(character)) {
				categoryListSet.add(str);
				str = "";
			}else {
				str+=character;
			}
		}
		load.close();
	}
	
	//Adds new categories to the program if they do not already exist
	//and returns the results in a user friendly format for printing
	public String addNewCategories(String input) throws IOException {
		String output = "";
		String addedCategories = "Added: ";
		String notAddedCategories = "Failed to add: ";
		String[] lexedinput = input.split(" ");
		for(String arrayElement: lexedinput) {
			try {
				Categories.valueOf(arrayElement.toUpperCase()); 
			}catch(IllegalArgumentException e) { 
				boolean success = categoryListSet.add(arrayElement);
				if(success)
					addedCategories += arrayElement + " ";
				else
					notAddedCategories += arrayElement + " ";
				continue;
			}
		}
		if(addedCategories.length() > 7) {
			output += addedCategories + "\n\n";
			save();
		}
		
		if(notAddedCategories.length() > 15)
			output += notAddedCategories + "\n\n";
		
		return output;
	}
	
	//removes categories from the program if they exist
	//and returns the results in a user friendly format for printing
	public String removeCategories(String input) throws IOException {
		String output = "";
		String removedCategories = "Removed: ";
		String notRemovedCategories = "Failed to remove: ";
		boolean removeSuccess = false;
		String[] lexedinput = input.split(" "); //byt från " " till " ," Gäller för övrigt hela programmet
		for(String arrayElement: lexedinput) {
			 removeSuccess = categoryListSet.remove(arrayElement);
			 
		if(removeSuccess) 
			removedCategories += arrayElement + " ";
		else
			notRemovedCategories += arrayElement + " ";
		}
		
		if(removedCategories.length() > 9) {
			output += removedCategories + "\n\n";
			save();
		}
		if(notRemovedCategories.length() > 18) {
			output += notRemovedCategories + "\n\n";
		}
		return output;
	}
	//Saves the contents of the set of categories into the "internal" text file
	public void save() throws IOException {
		
		FileOutputStream save = new FileOutputStream(categoryListFile);
		String temp = "";
		
		for(String str : categoryListSet) {
			temp+=str+" ";
		}
		
		byte[] bytesArr = temp.getBytes();
		save.write(bytesArr);	
		save.close();
		
	}

}
