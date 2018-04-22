/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Replace <...> with your actual data.
 * Daniel Canterino
 * djc3323
 * 15640
 * Slip days used: <0>
 * Git URL:
 * Spring 2018
 */


package assignment3;
import java.util.*;
import java.io.*;

/**
 * 
 * @author Daniel Canterino
 * @version 1.0
 * This class contains both breadth first search and depth first search functions for word ladders between two strings.
 */
public class Main {

	/**
	 * @author Daniel
	 * @param none
	 * @return none
	 * Main calls both dfs and bfs word ladder functions for testing purposes.
	 */
	public static void main(String[] args) throws Exception {
		String S = "hello";
		String g = "hood";
		S = g;
		
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file, for student testing and grading only
		// If arguments are specified, read/write from/to files instead of Std IO.
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default input from Stdin
			ps = System.out;			// default output to Stdout
		}
		initialize();
		while (true) {
			ArrayList<String> words = parse(kb);
			if (!words.isEmpty()) {
				String word1 = words.get(0);
				String word2 = words.get(1);
				System.out.println("Word Ladder Search using DFS\n");
				words = getWordLadderDFS(word1, word2);	
				System.out.println("\nWord Ladder Search using BFS\n");
				words = getWordLadderBFS(word1, word2);
			}
			else {
				break;
			}
		}
		ps.println("/quit detected. The program is now ending.");
	}
	
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		ArrayList<String> words = new ArrayList<String>();
		String line = "";
		String begin = "";
		String end = "";
		String quit = "/QUIT";//string used to compare for the /quit command
		int j = 0;
		
		line = keyboard.nextLine();//puts the entire input line into a string to be read from
		line = removeLeadingWhitespace(line);
		for (int i = 0; i < line.length() && line.charAt(i) != ' '; i++) {
			begin = begin + line.charAt(i);
		}
		line = line.replaceFirst(begin, "");//removes the first string from the line
		line = removeLeadingWhitespace(line);
		for (int i = j; i < line.length()  && line.charAt(i) != ' '; i++) {
			end = end + line.charAt(i);
		}
		begin = begin.toUpperCase();//converts to upper case to be consistent with dictionary upper case
		end = end.toUpperCase();
		if (begin.equals(quit) || end.equals(quit)) {//checks for the quit command and returns an empty array list to indicate quitting
			return words;
		}else if(begin.length() != 5 || end.length() != 5) {//ensures that only 5 character long words are input into the game
			System.out.println("please enter two words which are both 5 characters long.");
			return parse(keyboard);//calls the function again for a second chance for the operator
		}else {
			words.add(begin);//returns both the words in an ordered array list
			words.add(end);
		}
		return words;
	}
	
	/**
	 * @author Daniel
	 * @param starting word and ending word
	 * @return array list of the path containing both the start and end words and the words in order between them connecting them
	 * Begins recursion for DFS by calling DFS helper which will ultimately edit the path to the solution whether it is actually a solution or just the start and end words indicating no solution was found
	 */
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		Set<String> dict = makeDictionary();
		HashSet<String> visited = new HashSet<String>();//a set of words in the dictionary that have already been visited
		Map<String, String> map = new HashMap<String, String>();//a map containing the word as the key and the word used as the predessocr word for it
		ArrayList<String> path = new ArrayList<String>();//the array list of the path taken from the start word to the end word
		String previous = "";
		
		visited.add(start);
		map.put("first word", start);//arbitrary string for the first word just to make it different from the actual keys and words
		String word = "";
		word = word.valueOf(start);//sets the current word to the starting word
		dfsHelper(start, end, dict, visited, path, map, word);//begins recursion
		
		if (path.size() <= 2) {//if the path does not have two or more words in it, then the search failed so this ensures that the two words are the start and end in order to indicate failure
			path.clear();
			path.add(start);
			path.add(end);
		}
		printLadder(path);//prints the ladder results
		return path;
	}
	
	/**
	 * @author Daniel
	 * @param starting word and ending word
	 * @return an array list of the found word ladder returning only the starting and ending word if none was found
	 * Uses the breadth first search method to find a word ladder between to passed strings.
	 */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
		Set<String> dict = makeDictionary();
		Queue<String> queue = new LinkedList<String>();//queue for all words that need to be checked
		HashSet<String> visited = new HashSet<String>();//set of all words that have already been checked
		ArrayList<String> path = new ArrayList<String>();//the pathway from the start to end word the method found
		Map <String, String> map = new HashMap<String, String>();//map where key is the new word, value to be accessed is the word the new word was derived from

		String newString = "";
		map.put("first word", start);//arbitrary key for starting word in the map to indicate the starting word has been reached
		queue.add(start);//initializes the top of the queue to the starting word
		visited.add(start);//adds the starting word to the visited map to ensure we do not redundantly check it
		
		while (!queue.isEmpty()) {//will continue searching as long as the queue is not empty
			String cur = queue.poll();//cur is the word from which we are checking
			for (int i = 0; i < cur.length(); i++) {
				for (char c = 'A'; c <= 'Z'; c++) {
					char temp[] = cur.toCharArray();
					temp[i] = c;
					newString = newString.valueOf(temp);
					if (newString.equals(end)) {//if the newString equals the ending word, we know we have reached the end of the ladder
						map.put(newString, cur);
						path.add(end);
						while (!newString.equals(start)) {//searches map backwards beginning from the ending word searching for the values of each key which indicate the word used to get to the key word
							path.add(0, map.get(newString));//adds it to the front of the arrayList since this method finds the path backwards
							newString = map.get(newString);
						}
						printLadder(path);//prints and returns the path
						return path;
					}else if (dict.contains(newString) && !visited.contains(newString)) {
						queue.add(newString);//adds the potential word to the queue if it is an actual word and has not already been checked
						visited.add(newString);
						map.put(newString, cur);//maps the new word as the key to the current word which is the word that was used to get to this point, or its predecessor in the path
					}
				}
			}
		}
		path.clear();//if a path was not returned in the loop, then we only return the start and end words indicated no path was found between the two
		path.add(start);
		path.add(end);
		printLadder(path);
		return path;
	}
    
	/**
	 * @author Daniel
	 * @param none
	 * @return none
	 * Prints the results of the word ladder.
	 */
	public static void printLadder(ArrayList<String> ladder) {
		if (ladder.size() == 2) {//will only trigger when the only contents of the array List are the start and end words indicating a failure of finding a ladder
			System.out.println("no word ladder can be found between " + ladder.get(0).toLowerCase() + " and " + ladder.get(1).toLowerCase() + ".");
		}else {
			System.out.println("a " + (ladder.size() - 2) + "-rung word ladder exists between " + ladder.get(0).toLowerCase() + " and " + ladder.get(ladder.size() - 1).toLowerCase() + ".");//the number of rungs is the number of words in the path minus the start and end words
			for (int i = 0; i < ladder.size(); i++) {
			System.out.println(ladder.get(i).toLowerCase());//prints only to lower case as per instruction
			}
		}
	}

	/**
	 * @author Daniel
	 * @param starting word, ending word, dictionary, the already checked words, the pathway, a map with the same functionality used in the BFS function (to keep track of the words used to get to the current word), and the word to be checked
	 * @return none
	 * Recursively calls on itself to find a DFS solution to the word ladder between the start and end words.
	 */
	private static void dfsHelper(String start, String end, Set<String> dict, HashSet<String> visited, ArrayList<String> path, Map<String, String> map, String word){
		if (path.contains(end)) {//base case for if the path has already been determined so instead of checking more words, it just returns back to quickly end
			return;
		}else if (word.equals(end)) {//base case for if the word passed is equal to the end word, indicating a path has been found
			path.add(end);
			while (!word.equals(start)) {//finds the word which lead to the current word in the map and adds that to the path exactly like the BFS function implementation of creating the ArrayList path
				path.add(0, map.get(word));
				word = map.get(word);
			}
			return;
		}else if (map.size() > 2000) {//if more than 2000 words have been searched, dfs terminates since there is most likely not a word ladder between the 2 words. This prevents a stack overflow.
			return;
		}else {
			for (int i = 0; i < word.length(); i++) {
					for (char c = 'A'; c <= 'Z'; c++) {
						char temp[] = word.toCharArray();
						temp[i] = c;
						String newString = "";
						newString = newString.valueOf(temp);
							if(dict.contains(newString) && !visited.contains(newString) && (degreeOfSeperation(end, newString) <= degreeOfSeperation(word, end))) {//will only check the word if it is at least equal to in closeness to the end word or closer to minimize redundancy and improve result
								visited.add(newString);
								map.put(newString, word);//maps the key of the newString made to the word it originally came from in the map so the path can be determined later
								dfsHelper(start, end, dict, visited, path, map, newString);//recursively calls itself
							}
					}
			}
			return;
		}
	}

	/**
	 * @author Daniel
	 * @param word1 and the word2 to be compared its seperation from
	 * @return an integer detailing how many of the same index characters are different from each other in each word
	 * This function is used to determine how close a word is to another with a low degree of seperation indicated higher similarity
	 */
	private static int degreeOfSeperation(String word1, String word2) {
		int degree = 0;
		char word1Arr[] = word1.toCharArray();
		char word2Arr[] = word2.toCharArray();
		for (int i = 0; i < word1Arr.length; i++) {
			if (word1Arr[i] != word2Arr[i]) {//if the characters differ, then the degree of seperation increases
				degree++;
			}
		}
		return degree;
	}
	
	/**
	 * @author Daniel
	 * @param a string
	 * @return the string without any leading whitespace
	 * This function effectively removes any leading whitespace that a string might have
	 */
	private static String removeLeadingWhitespace(String word) {
		String newWord = "";
		int i = 0;
		if (word.length() > 0) {//ensures that no fault will happen if a string of length 0 is passed
			while(word.charAt(i) == ' ' && i < word.length()) {
				i++;
			}
			for (int j = i; j < word.length(); j++) {
				newWord = newWord + word.charAt(j);//creates a new string without any whitespace by starting to copy characters after all the leading whitespace has been removed
			}
			return newWord;
		}else {
			return word;
		}
	}
	
	/* Do not modify makeDictionary */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}
}
