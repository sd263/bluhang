package com.ticklesoft.bluhang;

import java.util.Random;


/**
 * Contains all the avilable words in the single player and the category attached to them                  
 */
public class WordBank {
	
	private int word;
	private int category;
	
	private String listOfWords[] = { "tennis", "coca cola", "eiffle tower",
			"harry potter", "california", "orange", "emma stone", "london",	
			"coldplay", "elephant", "hockey", "microsoft", "stone henge",
			"animal farm", "texas", "carrot", "brad pitt", "berlin",
			"micheal jackson", "lion", "football", "mcdonalds", "coliseum",
			"pride and prejudice", "florida", "mango", "mila kunis", "tokoyo",
			"the beatles", "cat", "cricket", "walmart", "taj mahal",
			"wind in the willows", "washingon", "pear", "jonny depp", "paris",
			"pink floyd", "dog", "golf", "google", "victoria falls", "bible",
			"oregon", "tommato", "jenifer aniston", "warsaw", "elvis prezly",
			"turtle", "basketball", "shell", "london eye",
			"the lion witch and the wardrobe", "neverda", "apple",
			"orlando bloom", "washington dc", "muse", "dolphin", "baseball",
			"visa", "grand canyon", "whinne the pooh", "alaska", "avocado",
			"ellen page", "ottawa", "bob marley", "fish", "handball", "bp",
			"great wall of china", "robin hood", "hawaii", "blackberry",
			"harrison ford", "mexico city", "guns n roses", "rabbit",
			"cycling", "dell", "statue of liberty", "norther lights",
			"new mexico", "brocolli", "leonardo dicaprio", "madrid",
			"justin bieber", "fox", "trampolining", "samsung", "mount everest",
			"treasure island", "colarado", "cabbage", "kate winslet", "rome",
			"jay z", "squirrel", "swimming", "htc", "big ben", "peter pan",
			"utah", "cucumber", "bruce willis", "lisbon", "katy perry", "fox",
			"sailing", "intel", "pyramids of giza", "stormbreaker", "arizona",
			"garlic", "scarlett johansson", "athens", "jimmi hendrix",
			"panther", "running", "toyota", "golden gate bridge", "matilda",
			"kansas", "grape", "daniel radcliffe", "prauge", "nirvana",
			"camel", "badminton", "volkswagen", "sydney opera house",
			"the lord of the rings", "oklahoma", "lettece", "natalie portman",
			"moscow", "backstreet boys", "robin", "squash,", "ford",
			"kilimanjaro", "tracy beaker", "louisana", "lycee", "will smith",
			"seoul", "britney spears", "whale", "volleyball", "subway",
			"empire state building", "the hunger games", "missisippi", "melon",
			"emma watson", "bejing", "metalica", "deer", "sking", "starbucks",
			"louvre", "fifty shades of grey", "alabama", "mushroom",
			"keanu reeves", "bangkok", "the who", "buffalo", "rugby", "kfc",
			"angel falls", "the hobbit", "georgia", "onion", "nicole kidman",
			"canberra", "beyonce", "walrus", "surfing", "burger king",
			"tower bridge", "the da vinci code", "south carolina",
			"passion fruit", "simon pegg", "wellington", "emninem", "donkey",
			"skateboarding", "nintendo", "easter island", "watership down",
			"north carolina", "parsnip", "julia robers", "cape town",
			"bob dylan", "horse", "snooker", "ebay", "christo redemptor",
			"charlottes webb", "virgina", "pepper", "christian bale", "cario",
			"red hot chilli peppers", "beaver", "archery", "amazon",
			"niagara falls", "to kill a mockingbird", "new york",
			"pomegranate", "judi dench", "jerusalum", "radiohead", "cow",
			"curling", "budwieser", "mecca", "the very hungry catarpillar",
			"kentucky", "potato", "robert de niro", "brasilia", "iron maiden",
			"chicken", "la crosse", "sony", "mount rushmore",
			"charlie and the chocolate factory", "indiana", "pumpkin",
			"angelina jolie", "kingston", "arctic monkeys", "dinosaur", "judo",
			"nike", "st peters", "the kite runner", "idaho", "raspberry",
			"tom cruise", "hanoi", "aerosmith", "goat", "hockey", "red bull",
			"the white house", "the cat in the hat", "north dakota", "rhubarb",
			"cameron diaz", "nairobi", "rihanna", "hedgehog", "gymnastics",
			"ikea", "berlin wall", "james bond", "south dakota", "strawberry",
			"nicholas cage", "ankara", "foo fighters", "jellyfish", "darts",
			"pixar", "tower of london", "wheres wally", "iowa", "spinach",
			"drew barrymore", "stockholm", "lady gaga", "lobster", "boxing",
			"disney", "amazon rainforest", "tarzan", "pennsylvania", "turnip",
			"jim carey", "copenhagen", "oasis", "shark", "bowling", "lego",
			"river nile", "bridget jones", "new jersy", "watermelon",
			"halle berry", "oslo", "paramore", "tiger", "fencing", "facebook",
			"the bermuda triangle", "frakenstein", "rhode island", "fig",
			"will ferell", "amsterdam", "queen", "zebra", "kayaking",
			"twitter", "arc de triomphe", "jayne ayre", "vermount", "leek",
			"renee zellweger", "brussels", "linkin park", "wolf", "shotput",
			"pepsi", "notre dame", "alice in wonderland", "maschuttes",
			"mustard", "matt damon", "helsinki", "the spice girls", "sheep" };
	
	String categories[] = { "Sports", "Companies", "Landmarks", "Books",
			"USA States", "Fruit and Veg", "Actors",
			"Capital Cities", "Musicians", "Animals" };
	
	
	/**
	 * Returns a random word.
	 */
	public String chooseRandomWord(){
		Random r = new Random();
		int num = r.nextInt(sizeOfList());
		return setWord(num);
	}
	
	public String getCategory(){
		return categories[category];
	}

	public String getWord(){
		return listOfWords[word];
	}
	
	public String setWord(int i){
		word = i;
		setCategory(i);
		return getWord();
	}
	
	public int sizeOfList(){
		return listOfWords.length;
	}
	
	private void setCategory(int n) {
		for (int i = 0; i <= 9; i++) {
			if (n % 10 == i || n == i)
				category = i;
		}
	}
	
	
}
