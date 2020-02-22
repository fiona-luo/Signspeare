/* Fiona Luo
 * 4/25/18
 * Period 4
 * SignSpeareGame.java
 * 
 * 		SignSpeareGame.java runs the alphabet mode game. It is called when the user
 * clicks "Play alphabet Mode" from the menu. In alphabet mode, a random english
 * letter will be generated and displayed on the screen. Near the bottom of the screen, 
 * five signs will appear, one of which matches the english letter. The user will then 
 * click the correct ASL sign, similar to a multiple choice quiz. 
 * 		Each time the user gets a question correct, a JTextField in the middle of the screen
 * displays a compliment, and the user's score will increase by 10. Then, the game will move on 
 * to the next question. 
 * 		If the user answers the question incorrectly, they will receive a strike. Additionally, 
 * a feedback panel will pop up telling the user the sign and the letter which they got 
 * wrong. This will help the user learn from their mistakes. They will not receive additional 
 * score if they get the question wrong. 
 * 		If the user receives three strikes, they lose the game, and their score is then displayed 
 * on the screen. 
 * 		Note: Alphabet mode was created as a game mode because many people do not have 
 * a lot of experience with ASL. This means that word mode, where the user types in entire words, 
 * is often too difficult. Alphabet mode provides a good starting ground and foundation for 
 * beginner signers. 
 * 
 * Code Format: Here are the classes, from top to bottom: 
 * SignSpeareGame, FeedbackPanel, EndGamePanel, GamePanel, GameButtonsPanel, ImagePanel, StringPanel, 
 * SignsPanel, GameVariables
 */

import java.awt.event.ActionListener; // Action events and listeners
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

import java.util.Scanner;

// Contains the main panel. Its content is a GamePanel, which controls
// the other panels
public class SignSpeareGame extends JPanel
{
	private GamePanel gp; //Added as content of the panel
	private FeedbackPanel feedbackPanel; // panel with feedback, appears after user gets question wrong
	private EndGamePanel endGamePanel; // displays "Game Over", score. Shown after user gets 3 strikes
	
	private CardLayout cardLayout; // layout of the SignSpeareGame
	
	public SignSpeareGame(PanHolder panHolderIn)
	{	
		cardLayout = new CardLayout(); // sets the layout
		setLayout(cardLayout);
		//passes SignSpeareGame into GameVariables to call its methods later
		GameVariables.setSignSpeareGame(this); 
	}
	
	// adds different panels to SignSpeareGame, shows the GamePanel
	public void addPanels()
	{
		gp = new GamePanel();
		GameVariables.setGamePanel(gp);
		gp.addPanelsToGame();
		
		feedbackPanel = new FeedbackPanel();
		//adds the components of feedbackPanel, checks to see if the letter question was updated
		feedbackPanel.refreshFeedbackPanel(); 
		
		endGamePanel = new EndGamePanel();
		
		add(gp, "GamePanel");
		add(feedbackPanel, "FeedbackPanel");
		add(endGamePanel, "EndGamePanel");
		cardLayout.show(this, "GamePanel");
	}	
	// this method shows the feedback Panel
	public void displayFeedback()
	{
		feedbackPanel.refreshFeedbackPanel();
		cardLayout.show(this, "FeedbackPanel");
	}
	// this method shows the endgame panel
	public void displayEndGame()
	{
		endGamePanel.updateEndGamePanel();
		cardLayout.show(this, "EndGamePanel");
	}
	// this method shows the regular game panel
	public void showGamePanel()
	{
		cardLayout.show(this, "GamePanel");
	}
}
// FeedbackPanel is shown after the user answers a question incorrectly. It tells the 
// user the letter they got wrong and displays the correct answer
class FeedbackPanel extends JPanel implements ActionListener
{	
	private GamePanel gPanel; //used to create a new question after the user exits FeedbackPanel
	private SignSpeareGame signSpeareGame; // used to change cards to gPanel
	
	private JTextArea feedbackTitle; // the title which displays "Sorry!"
	private JTextArea feedbackArea; // Contains information on what letter the user got wrong
	private JLabel answerLabel; // has picture of the correct sign answer
	private JButton nextButton; // button to go to next question
	
	private Border feedbackTitleBorder; //border around the entire panel
	private String questionLetter; // the letter the question was about
	private JLabel[] signLabels; // the array which contains the five randomized signs
	private int correctPosition; // the correct position within the five randomized signs
	
	public FeedbackPanel()
	{
		// gets gPanel and signSpeareGame from where they were stored in signSpeareGame
		gPanel = GameVariables.getGamePanel();
		signSpeareGame = GameVariables.getSignSpeareGame();
		
		setPreferredSize(new Dimension(1440, 800));
		setLayout(new FlowLayout(FlowLayout.CENTER, 1440, 30));
		setBackground(GameVariables.getDarkGray());
		
		// feedbackTitle is initialized, attributes determined
		feedbackTitle = new JTextArea(" Sorry! ", 1, 0);
		feedbackTitle.setFont(new Font("Courier", Font.PLAIN, 120));
		feedbackTitle.setOpaque(true);
		feedbackTitle.setEditable(false);
		feedbackTitle.setBackground(GameVariables.getDarkGray());
		feedbackTitle.setForeground(GameVariables.getBrightPink());
		//adds a border to the title box
		feedbackTitleBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 40);
		feedbackTitle.setBorder(feedbackTitleBorder);
		
		// feedbackArea is initialized and customized
		feedbackArea = new JTextArea();
		feedbackArea.setPreferredSize(new Dimension(1000, 100));
		feedbackArea.setFont(new Font("Courier", Font.PLAIN, 50));
		feedbackArea.setOpaque(true);
		feedbackArea.setEditable(false);
		feedbackArea.setBackground(GameVariables.getDarkGray());
		feedbackArea.setForeground(Color.WHITE);
		feedbackArea.setLineWrap(true);
		feedbackArea.setWrapStyleWord(true);
			
		// nextButton is initialized and customized
		nextButton = new JButton("Next Question");
		nextButton.setBackground(GameVariables.getTurquoise());
		nextButton.setOpaque(true);
		nextButton.setBorderPainted(false);
		nextButton.setForeground(Color.WHITE);
		nextButton.setFont(new Font("Courier", Font.PLAIN, 20));
		nextButton.setPreferredSize(new Dimension(400, 50));
		nextButton.addActionListener(this);
	}
	// called in order to update the information on the feedbackPanel, such as 
	// the correct letter and its corresponding sign
	public void refreshFeedbackPanel()
	{
		// first removes all the panels 
		this.removeAll();
		// gets the letter which the question which was about
		questionLetter = GameVariables.getChosenLetter();
		feedbackArea.setText("   The correct sign for \"" + questionLetter + 
				"\" is ");
		// gets the picture which was the correct answer
		signLabels = new JLabel[26];
		signLabels = GameVariables.getSignLabelArray2();
		correctPosition = GameVariables.getAnswerPosition();
		answerLabel = signLabels[correctPosition - 1];
		
		// adds the different components of the feedbackPanel
		add(feedbackTitle);
		add(feedbackArea);
		add(answerLabel);
		add(nextButton);
		
		// because all the components were previously removed, the container
		// is marked as "invalid", meaning that the component should be relaid out. 
		// revalidate() also lays out the component again.
		this.revalidate(); 
		this.repaint();
	}
	// if the "next question" button is clicked, gPanel displays the next question
	public void actionPerformed(ActionEvent e)
	{
		signSpeareGame.showGamePanel();
		gPanel.refreshPanels();
	}
}

// EndGamePanel displays "Game Over" and tells the user their final score. 
// It contains a "play again" button and "back to menu" button
class EndGamePanel extends JPanel implements ActionListener
{
	private PanHolder panHolder1; //used to call changeCards() to go back to menu
	private Border endGamePanelBorder; // Border of entire panel
	
	private JTextField endGameTitle; // title which reads "Game Over"
	private JTextArea endGameInfo; // contains the user's score
	private JButton newGameButton; // button to begin a new game
	private JButton backToMenuButton; // button to go back to menu
	
	private String endGameString; // string which contains a compliment and the score
	
	public EndGamePanel()
	{
		setBackground(GameVariables.getDarkGray());
		setLayout(new FlowLayout(FlowLayout.CENTER, 1440, 20));
		
		// adds border to the entire panel
		endGamePanelBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 50);
		setBorder(endGamePanelBorder);
		// gets the PanHolder() object which was stored in GameVariables
		panHolder1 = GameVariables.getPanHolder();
		
		// initializes/sets attributes of endGameTitle
		endGameTitle = new JTextField(" Game Over ");
		endGameTitle.setFont(new Font("Courier", Font.BOLD, 200));
		endGameTitle.setOpaque(true);
		endGameTitle.setEditable(false);
		endGameTitle.setBackground(GameVariables.getDarkGray());
		endGameTitle.setForeground(GameVariables.getBrightPink());
		endGameTitle.setBorder(null);
		
		// endGameInfo is initialized/attributes set
		endGameInfo = new JTextArea();
		endGameInfo.setPreferredSize(new Dimension(1000, 300));
		endGameInfo.setFont(new Font("Courier", Font.PLAIN, 75));
		endGameInfo.setOpaque(true);
		endGameInfo.setEditable(false);
		endGameInfo.setBackground(GameVariables.getDarkGray());
		endGameInfo.setForeground(Color.WHITE);
		endGameInfo.setLineWrap(true);
		endGameInfo.setWrapStyleWord(true);
		
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
	public void updateEndGamePanel()
	{
		// string which displays "Congratulations" and the user's final score
		endGameString = new String("       Congratulations! \nYour score is " + GameVariables.getScore());
		endGameInfo.setText(endGameString);
		
		// adds the components to EndPanel()
		add(endGameTitle); 
		add(endGameInfo);
		add(backToMenuButton);
	}
	// if the user clicks menu or new game button, the corresponding action appears
	public void actionPerformed(ActionEvent e)
	{
		GameVariables.setGameBackToMenu(true);
		panHolder1.changeCards();
		GameVariables.setGameBackToMenu(false);
	}
}
// GamePanel contains FlowLayout. It controls the overall layout of the rest
// of the panels, (which panels go where on the screen)
class GamePanel extends JPanel
{
	private GameButtonsPanel gameButtonsPanel; //Contains exit button and scoreboard
	private ImagePanel imagePanel; //displays a large randomized letter for the user to see
	private StringPanel stringPanel; //displays instructions/compliments/help
	private SignsPanel signsPanel; //displays a group of signs which the user can choose from
	private Border gamePanelBorder; // the border of the entire GamePanel
		
	public GamePanel()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		setBorder(gamePanelBorder);
		setBackground(GameVariables.getDarkGray());
	}
	// adds the components/JPanels to GamePanel
	public void addPanelsToGame()
	{	
		gameButtonsPanel = new GameButtonsPanel();
		signsPanel = new SignsPanel();
		imagePanel = new ImagePanel();
		stringPanel = new StringPanel();
		gamePanelBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 20);
		
		//add the different panels in up to down order
		add(gameButtonsPanel);
		add(imagePanel);
		add(stringPanel);
		add(signsPanel);
	}
	// refreshes the game question; creates a new question
	public void refreshPanels()
	{
		// first removes all the previous panels
		this.removeAll();
		// creates new objects for each panel, so that a new letter and sign answers 
		// will be randomized
		gameButtonsPanel = new GameButtonsPanel();
		signsPanel = new SignsPanel();
		imagePanel = new ImagePanel();
		stringPanel = new StringPanel();
		stringPanel.updateMessageField();
		gamePanelBorder = BorderFactory.createLineBorder(GameVariables.getDarkGray(), 20);
		
		//add the different panels in up to down order
		add(gameButtonsPanel);
		add(imagePanel);
		add(stringPanel);
		add(signsPanel);
		// as removeAll() got rid of all the components, the container was marked as "invalid". 
		// revalidate() solves this by doing the layout of the panel again
		this.revalidate(); 
		this.repaint();
	}
	// if the user gets the questionCorrect, their score is updated, and a new question is shown
	public void questionCorrect()
	{
		GameVariables.increaseScore();
		refreshPanels();
	}
} 

// GameButtonsPanel contains the "Back to Menu" button, as well as the 
// scoreboard. 
class GameButtonsPanel extends JPanel implements ActionListener
{	
	private JButton menuButton; // the back to menu button
	private Dimension menuButtonDimension; //dimensions of the back to menu button
	private JTextField scoreField; // the scoreboard which is updated
	private JTextField strikeField; // JTextField to display number of strikes
	private Font buttonFont; // the font which is used for the menuButton
	
	private PanHolder panelHold; //stored in GameVariables
	
	private int scoreNum; // the current score
	private String strikeString; //like "Strikes: XX" which changes when the user gets a strike
	
	public GameButtonsPanel()
	{	
		// initializes field variables
		panelHold = GameVariables.getPanHolder();
		buttonFont = null;
		GameVariables.setGameButtonsPanel(this);
		
		setPreferredSize(new Dimension(1440, 75));
		setBackground(GameVariables.getDarkGray());
		setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0)); 
		
		// set characteristics of the menu button; eg turquoise background, 
		// font and color of foreground, etc. 
		menuButton = new JButton("Back to Menu");
		menuButton.setBackground(GameVariables.getTurquoise());
		menuButton.setOpaque(true);
		menuButton.setBorderPainted(false);
		menuButton.setForeground(Color.WHITE);
		buttonFont = new Font("Courier", Font.PLAIN, 20);
		menuButton.setFont(buttonFont);
		menuButtonDimension = new Dimension(400, 50);
		menuButton.setPreferredSize(menuButtonDimension);
		menuButton.addActionListener(this);
		
		// sets the attributes of the strikeBoard
		strikeField = new JTextField(" Strikes: ", 20);
		strikeField.setBackground(GameVariables.getBrightPink());
		strikeField.setForeground(Color.WHITE);
		strikeField.setFont(new Font("Courier", Font.BOLD, 25));
		strikeField.setEditable(false);
		strikeField.setPreferredSize(menuButtonDimension);
		
		// sets the color length of the scoreBoard
		scoreField = new JTextField(" Score: 0", 20);
		scoreField.setBackground(GameVariables.getTurquoise());
		scoreField.setForeground(Color.WHITE);
		scoreField.setFont(new Font("Courier", Font.BOLD, 25));
		scoreField.setEditable(false);
		scoreField.setPreferredSize(menuButtonDimension);
		
		// adds the different components to the ButtonPanel
		add(menuButton);
		if (GameVariables.getStrikes() == null)
			GameVariables.setStrikes("");
		updateStrikes();
		add(strikeField);
		updateScore();
		add(scoreField);
	}
	
	// If the menu button is clicked, changeCards() is called in 
	// order to go back to the menu
	public void actionPerformed(ActionEvent e)
	{
		 GameVariables.setGameBackToMenu(true);
		 panelHold.changeCards();
		 GameVariables.setGameBackToMenu(false);
	}
	// updates the score JTextField
	public void updateScore()
	{
		scoreNum = GameVariables.getScore();
		scoreField.setText(" Score: " + scoreNum);
	}
	// updates the strikes JTextField
	public void updateStrikes()
	{
		strikeString = " Strikes: " + GameVariables.getStrikes();
		strikeField.setText(strikeString);
	}
}

// ImagePanel will display either a large english letter, or several large
// letters which spell a word, depending on the game mode
class ImagePanel extends JPanel
{
	private JTextField letterField; // The JTextArea which will contain a letter
	private Border letterFieldBorder; //The Compound Border to the LetterArea
	private Border letterPinkBorder; // the outer pink border to the LetterArea
	private Border letterTurquoiseBorder; // the inner turquoise border to the LetterArea
	
	private int[] signPositions; // array with length 26; each index represents a sign, 
	// and the number stored in it represents its position out of the five random signs
	private int[] letterIndexes; // the indexes of the five signs which are chosen from 
	// signPositions 
	
	int randomOneOfFive; // a randomized number from 1-5
	int chosenIndex; // the index from 0-26 of the letter which is chosen
	String chosenLetter; // the letter which is chosen
	int chosenPosition; // the position of the letter which is chosen
	
	public ImagePanel()
	{	
		signPositions = null;
		letterIndexes = new int[5];
		
		setPreferredSize(new Dimension(1440, 300));
		setBackground(GameVariables.getDarkGray());
		
		getLetter(); // gets a random letter
		displayLetterOrWord(); // displays the letter
	}
	
	// This method randomly generates a CAPITAL letter using ascii values. 
	public void getLetter()
	{	
		int ind = 0;
		signPositions = GameVariables.getSignPositionIn();
		//The loop loops through signPositions to see which letters have been used. 
		// The indexes of the used letters are stores in letterIndexes, an array. 
		for (int i = 0; i<26; i++)
		{
			if (signPositions[i] != 0)
			{
				letterIndexes[ind] = i;
				ind++;
			}
		}
		randomOneOfFive = (int)(Math.random()*5); // random # from 1-5
		chosenIndex = letterIndexes[randomOneOfFive]; //chooses a random letter which was used/displayed
		chosenLetter = (char)(chosenIndex + 65) + ""; //uses chosenIndex to choose a letter
		GameVariables.setChosenLetter(chosenLetter); //stores the chosenLetter in GameVariables to be accessed elsewhere
		GameVariables.setAnswerPosition(signPositions[chosenIndex]); //stores the position in GameVariables
	}
	
	// this method displays the letter or word very largely on the screen so the 
	// user can see it better
	public void displayLetterOrWord()
	{
		// if the user is in alphabet mode, a large letter will be displayed
		letterField = new JTextField(GameVariables.getChosenLetter());
		letterField.setBackground(GameVariables.getBrightPink());
		letterField.setForeground(Color.WHITE);
		letterField.setFont(new Font("Courier", Font.PLAIN, 280));
		letterField.setHorizontalAlignment(JTextField.HORIZONTAL);
		
		// sets the borders of letterField, and its attributes
		letterPinkBorder = BorderFactory.createLineBorder(GameVariables.getBrightPink(), 4);
		letterTurquoiseBorder = BorderFactory.createLineBorder(GameVariables.getTurquoise(), 8);
		letterFieldBorder = BorderFactory.createCompoundBorder(letterPinkBorder, letterTurquoiseBorder);
		letterField.setBorder(letterFieldBorder);
		letterField.setEditable(false);
		letterField.setPreferredSize(new Dimension(300, 300));
		
		add(letterField);
	}
}

// StringPanel will display instructions, compliments, and helpful feedback. 
// It is not yet complete!
class StringPanel extends JPanel
{	
	private JTextField messageField; // instructions on how to play
	private Font messageFont; // font of messageField
	private String[] complimentArray; // contains compliments if user ansewrs questions correctly
	private int randomComplimentNum; //randomly generated num for the index of complimentArray chosen
	
	public StringPanel()
	{		
		setPreferredSize(new Dimension(1440, 50));
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		setBackground(GameVariables.getDarkGray());
		
		complimentArray = new String[] {"Great!", "Good job!", "Great job!", "Wonderful", "Wonderful job!",
				"Awesome job!", "Awesome!", "Amazing!", "Amazing job!", "Cool!", "Nice skills!", 
				"Keep it up!", "Nice!", "Nice job!", "Superb!"};
		
		// initializes the JTextField with instructions to the panel
		messageField = new JTextField("Click the sign that shows the letter above", 75);
		addMessageField();
	}
	//
	public void addMessageField()
	{
		// sets attributes of messageField, adds it 
		messageField.setBackground(GameVariables.getBrightPink());
		messageField.setForeground(Color.WHITE);
		messageFont = new Font("Courier", Font.PLAIN, 30);
		messageField.setFont(messageFont);
		messageField.setHorizontalAlignment(JTextField.CENTER);
		messageField.setPreferredSize(new Dimension(1400, 50));
		messageField.setEditable(false);
		
		add(messageField);
	}
	public void updateMessageField()
	{
		randomComplimentNum = (int)(Math.random()*complimentArray.length);
		messageField.setText(complimentArray[randomComplimentNum]);
	}
}

// SignsPanel randomly displays a set of signs which the user will choose from to 
// match a randomly generated letter. It gets the image for all 26 ASL alphabet letters
class SignsPanel extends JPanel implements MouseListener
{	
	private GamePanel gamePanel; //GamePanel which is stored in GameVariables
	private GameButtonsPanel gameButtonsPanel; // gameButtonsPanel stored in GameVariables
	private SignSpeareGame ssGame; // also stored in GameVariables
	
	private Image[] signImageArray; // array of all 26 sign images
	private int[] signPositionArray; // array with each sign and its corresponding position
	private JLabel[] signLabelArray; // has 26 sign labels which has sign pictures
	private JLabel[] signLabelArray2; // copy of signLabelArray, so that it can be accessed in other classes
	private int correctPosition; // the correct position of the answer (1 to 5)
	
	// The alphabet Images from a-z, stored in an array
	private Image aImage, bImage, cImage, dImage, eImage, fImage, gImage, 
		hImage, iImage, jImage, kImage, lImage, mImage, nImage, oImage, 
		pImage, qImage, rImage, sImage, tImage, uImage, vImage, wImage, 
		xImage, yImage, zImage;

	public SignsPanel()
	{
		// gets variables stored in GameVariables
		ssGame = GameVariables.getSignSpeareGame();
		gamePanel = GameVariables.getGamePanel();	
		gameButtonsPanel = GameVariables.getGameButtonsPanel();
		
		// initializes field variables
		signPositionArray = new int[26];
		signLabelArray = new JLabel[5];
		signLabelArray2 = new JLabel[5];
		signImageArray = new Image[]{aImage, bImage, cImage, dImage, eImage, fImage, 
			gImage, hImage, iImage, jImage, kImage, lImage, mImage, nImage, 
			oImage, pImage, qImage, rImage, sImage, tImage, uImage, vImage, 
			wImage, xImage, yImage, zImage};
		
		// uses a for loop to get each image using the method getSignImages. 
		// getSignImages contains a generic try catch block, demonstrating 
		// polymorphism
		for (int i = 0; i<26; i++)
		{
			String firstLetter;
			firstLetter = (char)(65+i) + "";
			signImageArray[i] = getSignImages(firstLetter + "Image.jpg");
		}
		
		setPreferredSize(new Dimension(1440, 300));
		setBackground(GameVariables.getDarkGray());
		setLayout(new GridLayout(1, 5, 1, 1));
		
		// generates 5 random signs
		generateRandomSigns();
	}
	
	// This method is a generic try- catch block which uses
	// polymorphism, as it can be applied to get any image. It is 
	// called in a for loop to get all the images. 
	public Image getSignImages(String imageNamein)
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
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	// this method generates five random signs, without repeats, to be 
	// displayed at the bottom of the panel. The user clicks the sign
	// which corresponds with the english letter above
	public void generateRandomSigns()
	{
		int imageIndex; //the index of the images which are chosen from 0-25
		Image resizedSign; //a scaled image which is smaller than the raw image
		ImageIcon tempImageIcon; // contains the resizedSign
		
		// initialize the variables
		imageIndex = -1;
		resizedSign = null; 
		tempImageIcon = null;
		
		// this loop generates 5 random sign images, without repeats, and adds them to 
		// an array. They are then added in order to the SignsPanel
		for (int i = 0; i<5; i++)
		{
			imageIndex = (int)(Math.random()*25);
			while(signPositionArray[imageIndex]!=0)
			{
				imageIndex = (int)(Math.random()*25);
			}
			signPositionArray[imageIndex] = i + 1;
			
			resizedSign = signImageArray[imageIndex].getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);
			tempImageIcon = new ImageIcon(resizedSign);
			
			signLabelArray[i] = new JLabel(tempImageIcon);
			signLabelArray[i].addMouseListener(this);
			signLabelArray2[i] = new JLabel (tempImageIcon);
			this.add(signLabelArray[i]);
		}
		GameVariables.setSignPositionIn(signPositionArray);
		GameVariables.setSignLabelArray(signLabelArray);
		GameVariables.setSignLabelArray2(signLabelArray2);
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e){}
	// If the user clicks one of the five signs. Based on whether their answer was correct 
	// or incorrect, the screen displays varying outputs detailed below
	public void mouseClicked(MouseEvent e)
	{
		correctPosition = GameVariables.getAnswerPosition();
		// if the correct sign is clicked, the score is increased and another question is displayed
		if (e.getSource() == signLabelArray[correctPosition - 1])
		{
			gamePanel.questionCorrect();
		}
		// if the incorrect sign is clicked
		else
		{
			if (GameVariables.getStrikes() == null)
				GameVariables.setStrikes("");
			
			// if the user gets a strike. If they have less than three strikes, 
			// feedback is displayed on why they got the problem incorrect
			GameVariables.increaseStrikes();
			if (GameVariables.getStrikes().length() < 3)
			{
				ssGame.displayFeedback();
				gameButtonsPanel.updateStrikes();
			}
			// if the user has three or more strikes, the game ends
			else
			{
				ssGame.displayEndGame();
			}
		}
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
}

// GameVariables is a static class which contains variables that will 
// be used repeatedly by different classes throughout the game, e.g. the 
// game mode, and commonly used colors like turquoise. 
class GameVariables
{
	private static boolean gameBackToMenu; // set true if user clicks back to menu button 
	
	private static String chosenLetter; // the letter of the question
	private static int[] signPositionIn; // array of 26 with corresponding sign position from 1-5
	private static JLabel[] signLabelsIn; // array of labels with sign images
	private static JLabel[] signLabelsIn2; // copy of array with sign image labels
	private static int answerPosition;// the position of the answer (1 to 5)
	
	private static Image[] signImagesArrayIn; //array of all 26 sign images
	private static JLabel[] fullSignLabelsArrayIn; //array of 26 sign images stored in JLabels
	private static JLabel[] wordLabelArrayIn; //JLabels with signs which spell the word currently displayed
	private static JLabel[] wordLabelArrayIn2; // A copy of wordLabelArrayIn, which will be added to the feedback panel
	
	private static String chosenWordIn; // the randomly generated word for each question
	private static int questionNumIn; // the number of the question; as the question number increases, so does the 
	// length of the word
	
	private static int score; // the user's current score
	private static String strikes; // the user's current strikes
	
	private static PanHolder panHolder; //Contains the cards for the game (with the feedback and endGamePanel)
	private static SignSpeareGame signSpeareGame; //The overall JPanel of the game. Holds panHolder (see above line)
	private static GamePanel gamePanel; // the panel which holds the rest of the game panels in FlowLayout
	private static GameButtonsPanel gameButtonsPanel; //contains the exit button, strikes, and scoreboard
	private static SignSpeareWord signSpeareWord; //The overall JPanel of word mode
	private static MainPanel mainPanel; // the game panel of word mode which holds the rest of the word mode panels
	private static WordButtonsPanel wordButtonsPanel; //contains the exit button, strikes, and score box
	
	// Commonly used custom colors
	private static Color brightPink;
	private static Color turquoise;
	private static Color darkGray;
	
	// getter methods to access colors
	public static Color getBrightPink()
	{ 
		brightPink = new Color(238, 73, 99);
		return brightPink;
	}
	public static Color getTurquoise()
	{
		turquoise = new Color(83, 193, 192);
		return turquoise;
	}
	public static Color getDarkGray()
	{
		darkGray = new Color(46, 49, 61);
		return darkGray;
	}
	
	// getter/setter methods for signSpeareGame
	public static void setSignSpeareGame(SignSpeareGame signSpeareGameIn)
	{
		signSpeareGame = signSpeareGameIn;
	}
	public static SignSpeareGame getSignSpeareGame()
	{
		return signSpeareGame;
	}
	// getter/setter methods for signSpeareWord
	public static void setSignSpeareWord(SignSpeareWord signSpeareWordIn)
	{
		signSpeareWord = signSpeareWordIn;
	}
	public static SignSpeareWord getSignSpeareWord()
	{
		return signSpeareWord;
	}
	// getter/setter for gamePanel
	public static void setGamePanel(GamePanel gamePanelIn)
	{
		gamePanel = gamePanelIn;
	}
	public static GamePanel getGamePanel()
	{
		return gamePanel;
	}
	// getter/setter for mainPanel
	public static void setMainPanel(MainPanel mainPanelIn)
	{
		mainPanel = mainPanelIn;
	}
	public static MainPanel getMainPanel()
	{
		return mainPanel;
	}
	// getter/setter for gameButtonsPanel
	public static void setGameButtonsPanel(GameButtonsPanel gameButtonsPanelIn)
	{
		gameButtonsPanel = gameButtonsPanelIn;
	}
	public static GameButtonsPanel getGameButtonsPanel()
	{
		return gameButtonsPanel;
	}
	// getter/setter for gameBackToMenu
	public static void setGameBackToMenu(boolean gameBackToMenuIn1)
	{
		gameBackToMenu = gameBackToMenuIn1;
	}
	public static boolean getGameBackToMenu()
	{
		return gameBackToMenu;
	}
	// getter/setter for wordButtonsPanel
	public static void setWordButtonsPanel(WordButtonsPanel wordButtonsPanelIn)
	{
		wordButtonsPanel = wordButtonsPanelIn;
	}
	public static WordButtonsPanel getWordButtonsPanel()
	{
		return wordButtonsPanel;
	}
	// getter setter method for PanHolder, in order to call changeCards(), for 
	// example to go back to the menu screen
	public static void setPanHolder(PanHolder panHolderIn)
	{
		panHolder = panHolderIn;
	}
	public static PanHolder getPanHolder()
	{
		return panHolder;
	}
	// getter setter method for the letter which is chosen
	public static void setChosenLetter(String chosenLetterIn)
	{
		chosenLetter = chosenLetterIn;
	}
	public static String getChosenLetter()
	{
		return chosenLetter;
	}
	// getter/setter for the positions of the signs stored in an array
	public static void setSignPositionIn(int[] signPositionArrayIn)
	{
		signPositionIn = signPositionArrayIn;
	}
	public static int[] getSignPositionIn()
	{
		return signPositionIn;
	}
	// get/set the position of the correct sign
	public static void setAnswerPosition(int answerPositionIn)
	{
		answerPosition = answerPositionIn;
	}
	public static int getAnswerPosition()
	{
		return answerPosition;
	}
	// called to increase the score by 10, and to get the score
	public static void increaseScore()
	{
		score += 10;
	}
	public static int getScore()
	{
		return score;
	}
	// getter/setter for the string of strikes, like "X" or "XX"
	public static void setStrikes(String strikesIn)
	{
		strikes = strikesIn;
	}
	public static void increaseStrikes()
	{
		strikes += "X";
	}
	public static String getStrikes()
	{
		return strikes;
	}
	// getter/setter for the array which contains JLabels of each sign image
	public static void setSignLabelArray(JLabel[] signLabelArrayIn)
	{
		signLabelsIn = signLabelArrayIn;
	}
	public static JLabel[] getSignLabelArray()
	{
		return signLabelsIn;
	}
	// getter/setter for the copy of the array with all the JLabels with sign images
	public static void setSignLabelArray2(JLabel[] signLabelArrayIn2)
	{
		signLabelsIn2 = signLabelArrayIn2;
	}
	public static JLabel[] getSignLabelArray2()
	{
		return signLabelsIn2;
	}
	// getter/setter for the array of sign images from alphabet mode
	public static void setSignImagesArray(Image[] signImageArrays)
	{
		signImagesArrayIn = signImageArrays;
	}
	public static Image[] getSignImagesArray()
	{
		return signImagesArrayIn;
	}
	// getter/setter for the array of sign iamges from word mode
	public static void setFullSignLabelsArray(JLabel[] signLabelsArrayIn1)
	{
		fullSignLabelsArrayIn = signLabelsArrayIn1;
	}
	public static JLabel[] getFullSignLabelsArray()
	{
		return fullSignLabelsArrayIn;
	}
	// getter/setter for the copy of the JLabels which spell the chosen word
	public static void setWordLabelArray2(JLabel[] wordLabelArrayInput2)
	{
		wordLabelArrayIn2 = wordLabelArrayInput2;
	}
	public static JLabel[] getWordLabelArray2()
	{
		return wordLabelArrayIn2;
	}
	// getter/setter for the JLabels which spell the chosen word (wordLabelArrayIn)
	public static void setWordLabelArray(JLabel[] wordLabelArrayInput)
	{
		wordLabelArrayIn = wordLabelArrayInput;
	}
	public static JLabel[] getWordLabelArray()
	{
		return wordLabelArrayIn;
	}
	// getter/setter for the word which is randomly chosen
	public static void setChosenWord(String chosenWordInput)
	{
		chosenWordIn = chosenWordInput;
	}
	public static String getChosenWord()
	{
		return chosenWordIn;
	}
	// getter/setter for the questionNum
	public static void setQuestionNum(int questionNumInput)
	{
		questionNumIn = questionNumInput;
	}
	// increases the questionNum by 1
	public static void increaseQuestionNum()
	{
		questionNumIn++;
	}
	public static int getQuestionNum()
	{
		return questionNumIn;
	}
	// Called when the user creates a new game, such as by clicking the "Play Alphabet Mode" or 
	// "Play Word Mode" buttons. This resets the variables from the old game, such as the number of 
	// strikes, and the score, allowing the user the start off on a fresh slate. 
	public static void resetVariables()
	{
		gameBackToMenu = false;
		chosenLetter = "";
		signPositionIn = new int[26];
		answerPosition = 0;
		score = 0;
		strikes = new String("");
		gamePanel = null;
		gameButtonsPanel = null;
		mainPanel = null;
		wordButtonsPanel = null;
		questionNumIn = 0;
	}
}