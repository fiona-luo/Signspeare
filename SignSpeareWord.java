/* Fiona Luo
 * 5/4/18
 * Period 4
 * SignSpeareWord.java
 * 
 * SignSpeareWord.java runs word mode of the game. In word mode, a series of signs which
 * spells a word appears on the screen. In a JTextField below, the user inputs what the 
 * word the signs spell, and the answer is checked. With each five questions the user 
 * answers, the word length will increase. 
 * 
 * If the user is correct, they move on to the next question. A compliment is displayed in 
 * the pink JTextField in the middle of the screen. Their score will also increase by 10. 
 * If the user is incorrect, a feedback/answer panel will pop up
 * to show the signs and tell the user the correct word answer. Each time the user answers
 * a question incorrectly they receive a strike. Once the user receives three strikes, 
 * the game ends, and their score is displayed.
 * Note: Word Mode of the game is designed for signers who are more familiar with ASL. For 
 * beginners, it is better to try alphabet mode first, as in alphabet the user only has to 
 * answer one letter at a time, making it much simpler.  
 * 
 * Format of SignSpeareWord: Here are the classes in SignSpeareWord in order:
 * SignSpeareWord, AnswerPanel, GameOverPanel, MainPanel, WordButtonsPanel, 
 * SignWordPanel, InstructionPanel, InputPanel
 */
import java.awt.event.ActionListener; // Action events and listeners
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel; // components used throughout the program
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color; //graphical imports
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image; 

import java.awt.FlowLayout; // layout imports
import java.awt.CardLayout;

import java.io.File; // File imports
import javax.imageio.ImageIO;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

// SignSpeareWord is the main panel with cardLayout which holds the mainPanel (which displays the 
// game), answerPanel (displays feedback if the user enters a question wrong), and GameOverPanel. 
public class SignSpeareWord extends JPanel
{
	private MainPanel mainPanel; //main content of the panel; displays the game
	private AnswerPanel answerPanel; // panel with answer, appears after user gets question wrong
	private GameOverPanel gameOverPanel; // displays "Game Over", score. Shown after user gets 3 strikes
	
	private CardLayout cards; // layout of the SignSpeareGame
	
	public SignSpeareWord()
	{
		cards = new CardLayout(); // sets the layout
		setLayout(cards);
		//passes SignSpeareWord into GameVariables to call its methods later
		GameVariables.setSignSpeareWord(this); 
	}
	// adds all the panels to the cardLayout, and shows the mainPanel
	public void addGamePanels()
	{
		mainPanel = new MainPanel();
		GameVariables.setMainPanel(mainPanel);
		mainPanel.addPanelsToMain();
		
		answerPanel = new AnswerPanel();
		//adds the components of feedbackPanel, checks to see if the letter question was updated
		answerPanel.refreshAnswerPanel(); 
		
		gameOverPanel = new GameOverPanel();
		
		add(mainPanel, "MainPanel");
		add(answerPanel, "AnswerPanel");
		add(gameOverPanel, "GameOverPanel");
		cards.show(this, "MainPanel");
	}
	// this method displays the answer to a question after the user answers it incorrectly
	public void showAnswerPanel()
	{
		answerPanel.refreshAnswerPanel();
		cards.show(this, "AnswerPanel");
	}
	// this method displays the game over screen once the user receives three
	// strikes and loses the game
	public void showGameOverPanel()
	{
		gameOverPanel.refreshGameOverPanel();
		cards.show(this, "GameOverPanel");
	}
	// showMainPanel() displays the mainPanel, which contains the gameplay
	public void showMainPanel() 
	{
		cards.show(this, "MainPanel");
		mainPanel.refreshMain();
	}
}
// AnswerPanel displays the answer to the question if the user inputs an incorrect
// answer. It will display the word the user got incorrect, and the signs to 
// go along with the word. AnswerPanel also contains a next question button. 
class AnswerPanel extends JPanel implements ActionListener
{
	private MainPanel mPanel; //used to create a new question after the user exits AnsewrPanel
	private SignSpeareWord signSpeareWord; // used to change cards to mainPanel
	
	private JTextArea answerTitle; // the title which displays "Sorry!"
	private JTextArea answerArea; // Tells the user which word they got wrong
	private JButton nextButton; // button to go to next question
	
	private Border answerTitleBorder; //border around the entire panel
	private String questionWord; // the word the question was about
	private JLabel[] wordLabels; // the array which contains the five randomized signs
	private JPanel wordLabelsPanel; // flowLayout, holds the wordLabels with the signs
	
	public AnswerPanel()
	{
		mPanel = GameVariables.getMainPanel();
		signSpeareWord = GameVariables.getSignSpeareWord();
		
		setPreferredSize(new Dimension(1440, 800));
		setLayout(new FlowLayout(FlowLayout.CENTER, 1440, 30));
		setBackground(GameVariables.getDarkGray());
		
		// answerTitle is initialized, attributes determined
		answerTitle = new JTextArea(" Sorry! ", 1, 0);
		answerTitle.setFont(new Font("Courier", Font.PLAIN, 120));
		answerTitle.setOpaque(true);
		answerTitle.setEditable(false);
		answerTitle.setBackground(GameVariables.getDarkGray());
		answerTitle.setForeground(GameVariables.getBrightPink());
		//adds a border to the answerTitle box
		answerTitleBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 40);
		answerTitle.setBorder(answerTitleBorder);
		
		// answerArea is initialized and customized
		answerArea = new JTextArea();
		answerArea.setPreferredSize(new Dimension(1000, 100));
		answerArea.setFont(new Font("Courier", Font.PLAIN, 50));
		answerArea.setOpaque(true);
		answerArea.setEditable(false);
		answerArea.setBackground(GameVariables.getDarkGray());
		answerArea.setForeground(Color.WHITE);
		answerArea.setLineWrap(true);
		answerArea.setWrapStyleWord(true);
			
		// nextButton is initialized and customized
		nextButton = new JButton("Next Question");
		nextButton.setBackground(GameVariables.getTurquoise());
		nextButton.setOpaque(true);
		nextButton.setBorderPainted(false);
		nextButton.setForeground(Color.WHITE);
		nextButton.setFont(new Font("Courier", Font.PLAIN, 20));
		nextButton.setPreferredSize(new Dimension(400, 50));
		nextButton.addActionListener(this);
		
		// characteristics of the panel containing the signs to the question are assigned
		wordLabelsPanel = new JPanel();
		wordLabelsPanel.setPreferredSize(new Dimension(1440, 250));
		wordLabelsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		wordLabelsPanel.setBackground(GameVariables.getDarkGray());
	}
	// called in order to update the information on the feedbackPanel, such as 
	// the correct letter and its corresponding sign
	public void refreshAnswerPanel()
	{
		// first removes all the panels 
		this.removeAll();
		// gets the word which the question which was about
		questionWord = GameVariables.getChosenWord();
		answerArea.setText("   The correct word is \"" + questionWord + "\"");
		
		wordLabels = GameVariables.getWordLabelArray2();
		wordLabelsPanel.removeAll();
		for (int i = 0; i<5; i++)
		{
			if (wordLabels[i] != null)
			{
				wordLabelsPanel.add(wordLabels[i]);
			}
		}
		wordLabelsPanel.revalidate();
		wordLabelsPanel.repaint();
		
		// adds the different components of the feedbackPanel
		add(answerTitle);
		add(answerArea);
		add(wordLabelsPanel);
		add(nextButton);
		
		// because all the components were previously removed, the container
		// is marked as "invalid", meaning that the component should be relaid out. 
		// revalidate() also lays out the component again.
		this.revalidate(); 
		this.repaint();
	}
	// if the "next question" button is clicked, mPanel displays the next question
	public void actionPerformed(ActionEvent e)
	{
		signSpeareWord.showMainPanel();
		mPanel.refreshMain();
	}
}

// GameOverPanel is displayed after the user enters three questions incorrectly, and 
// receives three strikes. It congratulates the user and informs them of their score. 
// It also contains a back to menu button
class GameOverPanel extends JPanel implements ActionListener
{
	private PanHolder panHolder2; //used to call changeCards() to go back to menu
	private Border gameOverPanelBorder; // Border of entire panel
	
	private JTextField gameOverTitle; // title which reads "Game Over"
	private JTextArea gameOverInfo; // contains the user's score
	private JButton backToMenuButton; // button to go back to menu
	
	private String gameOverString; // string which contains a compliment and the score
	
	public GameOverPanel()
	{
		setBackground(GameVariables.getDarkGray());
		setLayout(new FlowLayout(FlowLayout.CENTER, 1440, 20));
		
		// adds border to the entire panel
		gameOverPanelBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 50);
		setBorder(gameOverPanelBorder);
		// gets the PanHolder object which was stored in GameVariables
		panHolder2 = GameVariables.getPanHolder();
		
		// initializes/sets attributes of gameOverTitle
		gameOverTitle = new JTextField(" Game Over ");
		gameOverTitle.setFont(new Font("Courier", Font.BOLD, 200));
		gameOverTitle.setOpaque(true);
		gameOverTitle.setEditable(false);
		gameOverTitle.setBackground(GameVariables.getDarkGray());
		gameOverTitle.setForeground(GameVariables.getBrightPink());
		gameOverTitle.setBorder(null);
		
		// gameOverInfo is initialized/attributes set
		gameOverInfo = new JTextArea();
		gameOverInfo.setPreferredSize(new Dimension(1000, 300));
		gameOverInfo.setFont(new Font("Courier", Font.PLAIN, 75));
		gameOverInfo.setOpaque(true);
		gameOverInfo.setEditable(false);
		gameOverInfo.setBackground(GameVariables.getDarkGray());
		gameOverInfo.setForeground(Color.WHITE);
		gameOverInfo.setLineWrap(true);
		gameOverInfo.setWrapStyleWord(true);
		
		// the back to menu button is initialized, customized
		backToMenuButton = new JButton("Back to Menu");
		backToMenuButton.setBackground(GameVariables.getTurquoise());
		backToMenuButton.setOpaque(true);
		backToMenuButton.setBorderPainted(false);
		backToMenuButton.setForeground(Color.WHITE);
		backToMenuButton.setFont(new Font("Courier", Font.PLAIN, 20));
		backToMenuButton.setPreferredSize(new Dimension(400, 50));
		backToMenuButton.addActionListener(this);
	}
	// refreshGameOverPanel() updates the game over panel; it gets the user's 
	// current score, in case it changed within the course of the game. 
	public void refreshGameOverPanel()
	{
		// string which displays "Congratulations" and the user's final score
		gameOverString = new String("       Congratulations! \nYour score is " + GameVariables.getScore());
		gameOverInfo.setText(gameOverString);
		
		// adds the components to EndPanel()
		add(gameOverTitle); 
		add(gameOverInfo);
		add(backToMenuButton);
	}
	// if the user clicks menu button, the user is taken back to the menu
	public void actionPerformed(ActionEvent e)
	{
		GameVariables.setGameBackToMenu(true);
		panHolder2.changeCards();
		GameVariables.setGameBackToMenu(false);
	}
}

// MainPanel is where the main gameplay happens. In MainPanel, the user is presented
// with a series of signs which spell a word, and is prompted to enter the word into a 
// JTextField. MainPanel holds WordButtonsPanel, SignWordPanel, InstructionPanel, 
// TimerPanel, and InputPanel. 
class MainPanel extends JPanel
{
	private WordButtonsPanel wordButtonsPanel; //Contains exit button and scoreboard
	private SignWordPanel signWordPanel; // Contains the series of signs which spell a word
	private InstructionPanel instructionPanel; //displays instructions/compliments/help
	private InputPanel inputPanel; // Has JTextField so the user can enter what they think the word is
	private Border mainPanelBorder; // the border of the entire GamePanel
	
	public MainPanel()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		setBorder(mainPanelBorder);
		setBackground(GameVariables.getDarkGray());
	}
	
	// addPanelsToMain() adds various panels to the mainPanel, and initializes
	// the different panels. 
	public void addPanelsToMain() 
	{
		this.removeAll();
		
		wordButtonsPanel = new WordButtonsPanel();
		signWordPanel = new SignWordPanel();
		instructionPanel = new InstructionPanel();
		inputPanel = new InputPanel();
		mainPanelBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 20);
		
		//add the different panels in up to down order
		add(wordButtonsPanel);
		add(signWordPanel);
		add(instructionPanel);
		add(inputPanel);
		inputPanel.requestFocusToInputField();
		
		this.revalidate(); 
		this.repaint();
	}
	// refreshMain() updates mainPanel in order to generate a new randomized word or question. 
	// It does this by first removing all the panels, reinitializing them, and adding them
	// back to mainPanel again
	public void refreshMain()
	{
		// first removes all the previous panels
		this.removeAll();
		// creates new objects for each panel, so that a new letter and sign answers 
		// will be randomized
		wordButtonsPanel = new WordButtonsPanel();
		signWordPanel = new SignWordPanel();
		instructionPanel = new InstructionPanel();
		instructionPanel.updateInstructionField();
		inputPanel = new InputPanel();
		mainPanelBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 20);
		
		//add the different panels in up to down order
		add(wordButtonsPanel);
		add(signWordPanel);
		add(instructionPanel);
		add(inputPanel);
		inputPanel.requestFocusToInputField();
		// as removeAll() got rid of all the components, the container was marked as "invalid". 
		// revalidate() solves this by doing the layout of the panel again
		this.revalidate(); 
		this.repaint();
	}
	// if the user gets the questionCorrect, their score is updated, and a new question is shown
	public void answerCorrect()
	{
		GameVariables.increaseScore();
		refreshMain();
	}
}

// WordButtonsPanel contains a back to menu button, scoreboard, and strikes board. 
class WordButtonsPanel extends JPanel implements ActionListener
{
	private JButton backMenuButton; // the back to menu button
	private Dimension backMenuButtonDimension; //dimensions of the back to menu button
	private JTextField scoreBox; // the scoreboard which is updated
	private JTextField strikeBox; // JTextField to display number of strikes
	private Font backMenuButtonFont; // the font which is used for the menuButton
	
	private PanHolder pHold; //stored in GameVariables
	
	private int scoreInt; // the current score
	private String strikeString; //like "Strikes: XX" which changes when the user gets a strike
	
	public WordButtonsPanel()
	{
		// initializes field variables
		pHold = GameVariables.getPanHolder();
		backMenuButtonFont = null;
		GameVariables.setWordButtonsPanel(this);
		
		setPreferredSize(new Dimension(1440, 75));
		setBackground(GameVariables.getDarkGray());
		setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0)); 
		
		// set characteristics of the menu button; eg turquoise background, 
		// font and color of foreground, etc. 
		backMenuButton = new JButton("Back to Menu");
		backMenuButton.setBackground(GameVariables.getTurquoise());
		backMenuButton.setOpaque(true);
		backMenuButton.setBorderPainted(false);
		backMenuButton.setForeground(Color.WHITE);
		backMenuButtonFont = new Font("Courier", Font.PLAIN, 20);
		backMenuButton.setFont(backMenuButtonFont);
		backMenuButtonDimension = new Dimension(400, 50);
		backMenuButton.setPreferredSize(backMenuButtonDimension);
		backMenuButton.addActionListener(this);
		
		// sets the attributes of the strikeBox
		strikeBox = new JTextField(" Strikes: ", 20);
		strikeBox.setBackground(GameVariables.getBrightPink());
		strikeBox.setForeground(Color.WHITE);
		strikeBox.setFont(new Font("Courier", Font.BOLD, 25));
		strikeBox.setEditable(false);
		strikeBox.setPreferredSize(backMenuButtonDimension);
		
		// sets the color length of the scoreBox
		scoreBox = new JTextField(" Score: 0", 20);
		scoreBox.setBackground(GameVariables.getTurquoise());
		scoreBox.setForeground(Color.WHITE);
		scoreBox.setFont(new Font("Courier", Font.BOLD, 25));
		scoreBox.setEditable(false);
		scoreBox.setPreferredSize(backMenuButtonDimension);
		
		// adds the different components to the ButtonPanel
		add(backMenuButton);
		if (GameVariables.getStrikes() == null)
			GameVariables.setStrikes("");
		updateStrikes();
		add(strikeBox);
		updateScore();
		add(scoreBox);
	}
	// if the user clicks the back to menu button, pHold changes cards and 
	// the user goes back to the menu
	public void actionPerformed(ActionEvent e)
	{
		 GameVariables.setGameBackToMenu(true);
		 pHold.changeCards();
		 GameVariables.setGameBackToMenu(false);
	}
	// updates the score JTextField
	public void updateScore()
	{
		scoreInt = GameVariables.getScore();
		scoreBox.setText(" Score: " + scoreInt);
	}
	// updates the strikes JTextField
	public void updateStrikes()
	{
		strikeString = " Strikes: " + GameVariables.getStrikes();
		strikeBox.setText(strikeString);
	}
}

// SignWordPanel randomly chooses a word from Vocab.txt, "translates" it into 
// signs, and displays the signs. The words progressively get harder; for the 
// first 8 question, they are three letter words. After that, the next 8 words are 
// 4 letter words. Finally, the user receives 5 letter words until the end of the game. 
class SignWordPanel extends JPanel
{
	private String vocabFileName; //name of the file which contains a list of words
	private Scanner vocabScanner; // A Scanner which reads the vocabFile
	
	private MainPanel mainPan; //GamePanel which is stored in GameVariables
	private WordButtonsPanel wordButtonsPan; // gameButtonsPanel stored in GameVariables
	private SignSpeareGame signSpeareGame; // also stored in GameVariables
	
	private Image[] signImagesArray; // array of all 26 sign images
	private JLabel[] fullSignLabelsArray; // has 26 sign labels which has sign pictures
	private JLabel[] fullSignLabelsArray2; // a copy of all 26 sign Labels to store in GameVariables
	private JLabel[] wordLabelsArray; // contains all the signs used in the chosen word
	private JLabel[] wordLabelsArray2; // copy of the signs used in the chosen word
	private boolean[] wordUsageArray; // contains 300 booleans; if a word is used, its corresponding boolean
	// is set to true. This way, no words are repeated. 
	private int intervalToIncreaseLength; // # questions before word length increases
	
	private int questionNum; // the number of the question the user is on
	private String chosenWord; // the CURRENT chosen word, which changes each new question
	
	// The alphabet Images from a-z
	private Image aImage, bImage, cImage, dImage, eImage, fImage, gImage, 
		hImage, iImage, jImage, kImage, lImage, mImage, nImage, oImage, 
		pImage, qImage, rImage, sImage, tImage, uImage, vImage, wImage, 
		xImage, yImage, zImage;
	
	public SignWordPanel()
	{
		// gets variables stored in GameVariables
		signSpeareGame = GameVariables.getSignSpeareGame();
		mainPan = GameVariables.getMainPanel();	
		wordButtonsPan = GameVariables.getWordButtonsPanel();
		// Vocab.txt contains a list of vocabulary words
		vocabFileName = "Vocab.txt";
		vocabScanner = null;
		
		// initializes field variables
		fullSignLabelsArray = new JLabel[26];
		fullSignLabelsArray2 = new JLabel[26];
		wordLabelsArray = new JLabel[5];
		wordLabelsArray2 = new JLabel[5];
		wordUsageArray = new boolean[300];
		questionNum = GameVariables.getQuestionNum();
		chosenWord = new String("");
		
		signImagesArray = new Image[]{aImage, bImage, cImage, dImage, eImage, fImage, 
			gImage, hImage, iImage, jImage, kImage, lImage, mImage, nImage, 
			oImage, pImage, qImage, rImage, sImage, tImage, uImage, vImage, 
			wImage, xImage, yImage, zImage};
		
		setPreferredSize(new Dimension(1440, 300));
		setBackground(GameVariables.getDarkGray());
		setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
		
		// uses a for loop to get each image using the method getSignImages. 
		// getSignImages contains a generic try catch block, demonstrating 
		// polymorphism
		for (int i = 0; i<26; i++)
		{
			String firstLetter;
			firstLetter = (char)(65+i) + "";
			signImagesArray[i] = getSignImage(firstLetter + "Image.jpg");
		}
		createVocabScanner(); // creates a scanner for Vocab.txt
		getFullSignLabelsArray();
		getChosenWord();
		addWordLabelsArray();
		displayWordSigns();
	}
	// this method creates a scanner to scan Vocab.txt, so that a random word can
	// be chosen if the user is in word mode. It uses a try catch block
	public void createVocabScanner()
	{
		File vocab = new File(vocabFileName);
		try
		{
			vocabScanner = new Scanner(vocab);
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Cannot find Vocab.txt file.");
			System.exit(1);
		}
	}
	// This method is a generic try- catch block which uses
	// polymorphism, as it can be applied to get any image. It is 
	// called in a for loop to get all the images. 
	public Image getSignImage(String imageNamein)
	{
		File signImageFile = new File(imageNamein);
		try
		{
			//imageIn = ImageIO.read(signImageFile);
			return ImageIO.read(signImageFile);
		}
		catch(IOException e)
		{
			System.err.println("\n\n\n" + imageNamein + 
				" can't be found.\n\n\n");
			e.printStackTrace();
			return null;
		}
	}
	// getFullSignLabelsArray() gets all the JLabels (which contain the sign images) for the 
	// entire alphabet. They are used later to translate a random word into signs. 
	public void getFullSignLabelsArray()
	{
		int imageIdx; //the index of the images which are chosen from 0-25
		Image scaledSign; //a scaled image which is smaller than the raw image
		ImageIcon tempImageIcon; // contains the resizedSign
		
		// initialize the variables
		imageIdx = -1;
		scaledSign = null; 
		tempImageIcon = null;
		
		// adds JLabels with images to the getFullSignLabelsArray and its copy, 
		// fullSignLabelsArray2
		for (int i = 0; i<26; i++)
		{
			scaledSign = signImagesArray[i].getScaledInstance(225, 225, java.awt.Image.SCALE_SMOOTH);
			tempImageIcon = new ImageIcon(scaledSign);
			
			fullSignLabelsArray[i] = new JLabel(tempImageIcon);
			fullSignLabelsArray2[i] = new JLabel(tempImageIcon);
		}
		GameVariables.setFullSignLabelsArray(fullSignLabelsArray);
	}
	// getChosenWord() randomly generates a word from Vocab.txt. For the first 8 questions, 
	// the user receives 3 letter words; the next 8, 4 letter words. After that, the user
	// continues to receive 5 letter words. 
	public void getChosenWord()
	{
		int randomLineNum;
		int lineSection;
		GameVariables.increaseQuestionNum();
		questionNum = GameVariables.getQuestionNum();
		intervalToIncreaseLength = 7;
		
		if (questionNum <= intervalToIncreaseLength)
			lineSection = 0;
		else if (questionNum>intervalToIncreaseLength && 
			questionNum <= 2* intervalToIncreaseLength)
			lineSection = 100;
		else
			lineSection = 200;
		
		// This do-while checks if the word has been used previously in the game. 
		// If so, a new word is generates. 
		do
		{
			randomLineNum = (int)(Math.random()*100) + lineSection;
		}
		while (wordUsageArray[randomLineNum] == true);
		wordUsageArray[randomLineNum] = true;
		
		// this for loop reads a randomly assigned line in Vocab.txt
		for (int i = 1; i<= randomLineNum; i++)
		{
			chosenWord = vocabScanner.nextLine();
		}
		GameVariables.setChosenWord(chosenWord);
	}
	// addWordLabelsArray() translates the word into signs, and adds the 
	// corresponding JLabel signs into an array
	public void addWordLabelsArray()
	{
		int signIndex;
		// this for loop converts every sign in the chosen word into a signJLabel which 
		// are stored in fullSignLabelsArray. These chosen signs are stored in 
		// wordLabelsArray
		for (int i=0; i<5; i++)
		{
			if (i<chosenWord.length())
			{
				signIndex = (int)(chosenWord.charAt(i));
				wordLabelsArray[i] = fullSignLabelsArray[signIndex-97];
				wordLabelsArray2[i] = fullSignLabelsArray2[signIndex-97];
			}
		}
		GameVariables.setWordLabelArray(wordLabelsArray);
		GameVariables.setWordLabelArray2(wordLabelsArray2);
	}
	// displayWordSigns() adds the JLabels which contain the signs to the SignWordPanel. 
	public void displayWordSigns()
	{
		for (int i = 0; i<5; i++)
		{
			if (wordLabelsArray[i] != null)
			{
				add(wordLabelsArray[i]);
			}
		}
	}
}

// InstructionPanel provides brief instructions on how the user should 
// play the game: by inputting their answer in the JTextArea below
class InstructionPanel extends JPanel
{
	private JTextField instructionField; // instructions on how to play
	private Font instructionFont; // font of messageField
	private String[] complimentsArray; //contains list of compliments if user gets question correct
	private int randomComplimentNumber; //index of the random compliment displayed
	
	public InstructionPanel()
	{
		setPreferredSize(new Dimension(1440, 50));
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		setBackground(GameVariables.getDarkGray());
		
		complimentsArray = new String[] {"Great!", "Good job!", "Great job!", "Wonderful", "Wonderful job!",
				"Awesome job!", "Awesome!", "Amazing!", "Amazing job!", "Cool!", "Nice skills!", 
				"Keep it up!", "Nice!", "Nice job!", "Superb!"};
		
		// initializes the JTextField with instructions to the panel
		instructionField = new JTextField("Type the word shown above and press enter", 75);
		addMessageField();
	}
	// adds the JTextField for input to the panel
	public void addMessageField()
	{
		// sets attributes of instructionField, adds it 
		instructionField.setBackground(GameVariables.getBrightPink());
		instructionField.setForeground(Color.WHITE);
		instructionFont = new Font("Courier", Font.PLAIN, 30);
		instructionField.setFont(instructionFont);
		instructionField.setHorizontalAlignment(JTextField.CENTER);
		instructionField.setPreferredSize(new Dimension(1400, 50));
		instructionField.setEditable(false);
		
		add(instructionField);
	}
	public void updateInstructionField()
	{
		randomComplimentNumber = (int)(Math.random()*complimentsArray.length);
		instructionField.setText(complimentsArray[randomComplimentNumber]);
	}
}

// InputPanel contains a JTextField which accepts the user's input. In this class, 
// the input is also checked to see if it matches the chosen word. If so, the game
// moves on to the next question/word. If it is wrong, feedback is displayed. If the 
// user is wrong but also has three strikes, the gameOverPanel is displayed
class InputPanel extends JPanel implements KeyListener
{
	private JTextField inputField; //Accepts the user's input word
	private Border inputFieldBorder; // the border to inputField
	private String userInput; // the word the user inputs
	
	private MainPanel mPanel;// used to generate a new question if the user is correct
	private SignSpeareWord ssWord;// used to display answers/game over if the user is incorrect
	private WordButtonsPanel wbPanel; // used to update the score and strikes
	
	public InputPanel()
	{
		setPreferredSize(new Dimension(1440, 250));
		setBackground(GameVariables.getDarkGray());
		
		// field variables are accessed through GameVariables and initialized
		mPanel = GameVariables.getMainPanel();
		ssWord = GameVariables.getSignSpeareWord();
		wbPanel = GameVariables.getWordButtonsPanel();
		userInput = new String("");
		
		// the attributes of inputField are determined
		inputField = new JTextField("");
		inputField.setBackground(Color.WHITE);
		inputField.setForeground(GameVariables.getDarkGray());
		inputField.setFont(new Font("Courier", Font.PLAIN, 150));
		inputField.setPreferredSize(new Dimension(1350, 225));
		inputFieldBorder = BorderFactory.createLineBorder(Color.WHITE, 40);
		inputField.setBorder(inputFieldBorder);
		inputField.addKeyListener(this);
		
		add(inputField);
	}
	// This method requests focus to input field. It is called in 
	// the class MainPanel
	public void requestFocusToInputField()
	{
		inputField.requestFocus();
	}

	public void keyTyped(KeyEvent e) {}
	// Once the user presses enter, their inputted answer is checked through this
	// method
	public void keyPressed(KeyEvent e) 
	{
		// if the user pressed enter, their input is checked
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			userInput = inputField.getText();
			// if the user's input matches the chosen word, a new question is displayed
			if (userInput.equalsIgnoreCase(GameVariables.getChosenWord()))
			{
				mPanel.answerCorrect();
			}
			// if the user is incorrect...
			else
			{
				if (GameVariables.getStrikes() == null)
					GameVariables.setStrikes("");
				// the user gets a strike. If they have less than three strikes, 
				// feedback is displayed on why they got the problem incorrect
				GameVariables.increaseStrikes();
				if (GameVariables.getStrikes().length() < 3)
				{
					ssWord.showAnswerPanel();
					wbPanel.updateStrikes();
				}
				// if the user has three or more strikes, the game ends, and the 
				// gameOverPanel is shown
				else
				{
					ssWord.showGameOverPanel();
				}
			}
		}
	}
	public void keyReleased(KeyEvent e) {}
}

