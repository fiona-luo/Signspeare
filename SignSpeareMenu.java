/* Fiona Luo
 * 4/11/18
 * Period 4
 * SignSpeareMenu.java
 * 
 * SignSpeareMenu.java displays the main menu for the game. The buttons on the menu
 * include "How to Play", "Dictionary", "Play Alphabet Mode", "Play Word Mode", and 
 * "Quit". SignSpeareMenu.java does not include any of the actual gameplay. 
 * 
 * How to play displays instructions on the rules of the game and information
 * about the game modes. The Dictionary contains a chart of each letter and its ASL sign, 
 * which the user can study if they need help. Play Alphabet Mode launches the alphabet mode
 * of the game; the code for this is in SignSpeareGame.java. Clicking Play Word Mode 
 * launches word mode of the game, which is run in SignSpeareWord.java. Clicking quit quits
 * the game. 
 * 
 * Order of classes by appearance in code:
 * SignSpeareMenu, PanHolder, MenuPanel, InfoPanel, DictPanel, DictImagePanel
 */

import java.awt.event.ActionListener; // for buttons in the main menu/panels
import java.awt.event.ActionEvent;

import javax.swing.JFrame;	// components used throughout the program
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image; // for background images/dictionary

import java.awt.FlowLayout;
import java.awt.CardLayout;

import java.io.File; 		// for background images/dictionary
import java.io.IOException;
import javax.imageio.ImageIO;

// SignSpeareMenu creates the main JFrame the program is in. It is 
// maximized to full screen on any monitor. It's content is an object 
// of the class PanHolder
public class SignSpeareMenu extends JFrame
{	
	public SignSpeareMenu()
	{
		// create the overall JFrame, set its attributes
		JFrame ssmFrame = new JFrame("SignSpeare");
		ssmFrame.setSize(1440, 800);
		ssmFrame.setLocation(0, 0);
		ssmFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ssmFrame.setResizable(true);
		
		PanHolder pHolder = new PanHolder(); // creates a PanHolder, adds it to the JFrame
		pHolder.addCards();
		ssmFrame.getContentPane().add(pHolder);
		ssmFrame.setVisible(true);
	}
	
	public static void main (String[] args)
	{
		SignSpeareMenu ssm = new SignSpeareMenu();
	}
}

//PanHolder is a JPanel class which has a CardLayout. Within the 
// CardLayout it contains the panels from MenuPanel, InfoPanel, and 
// the dictionary panel. Its method changeCards() is called from 
// different classes and through boolean specifiers shows different panels.
class PanHolder extends JPanel
{
	private CardLayout cards; //CardLayout for the JPanel
	private MenuPanel menuPanel; //Panel for the main menu screen 
	private InfoPanel infoPanel; // Panel which displays help/information
	private SignSpeareGame ssg; // Displays the alphabet game
	private SignSpeareWord ssw; // displays the word mode game
	private DictPanel dPanel; // displays the dictionary
	
	private boolean helpClickedIn; // true if user clicks the "How to play" button
	private boolean alphaGameClickedIn; // true if user clicks alphabet mode button
	private boolean wordGameClickedIn; // true if user clicks word mode button
	private boolean dictClickedIn; // true if user clicks dictionary button
	private boolean quitInfoClickedIn; // if user clicks quit button
	private boolean backToMenuIn; // if user clicks backToMenu from the dictionary/help panel
	private boolean gameBackToMenuIn; // if the user clicks back to menu from the game
	
	public PanHolder()
	{
		cards = new CardLayout(); //sets characteristics of the JPanel
		setLayout(cards);
		setBackground(new Color(46, 49, 61));
		
		setVisible(true);
	}
	
	public void addCards()
	{
		menuPanel = new MenuPanel(this); // adds various panels to the JPanel layout
		add(menuPanel, "MenuPanel");
		infoPanel = new InfoPanel(this); 
		add(infoPanel, "InfoPanel");
		ssg = new SignSpeareGame(this);
		add(ssg, "AlphaGamePanel");
		ssw = new SignSpeareWord();
		add(ssw, "WordGamePanel");
		dPanel = new DictPanel(this);
		add(dPanel, "DictPanel");
		GameVariables.setPanHolder(this); // stores panHolder in GameVariables to call changeCards() later
	}
	
	// changeCards() shows different cards through booleans from other classes. When a button
	// is clicked in another class, a public field boolean is set to true, and can be accessed
	// through the field variable panel objects in PanHolder. 
	public void changeCards()
	{
		// gets the values of all the booleans using getter methods
		helpClickedIn = menuPanel.getHelpClicked();
		alphaGameClickedIn = menuPanel.getAlphaGameClicked();
		wordGameClickedIn = menuPanel.getWordGameClicked();
		dictClickedIn = menuPanel.getDictClicked();
		quitInfoClickedIn = infoPanel.getQuitInfoClicked();
		backToMenuIn = dPanel.getBackToMenu();
		gameBackToMenuIn = GameVariables.getGameBackToMenu();
		
		// checks to see which button is clicked, displays the appropriate response
		if (helpClickedIn == true) // shows the information panel
		{
			cards.show(this, "InfoPanel");
		}
		else if (quitInfoClickedIn == true) // quits the information panel
		{
			cards.show(this, "MenuPanel");
		}
		else if (alphaGameClickedIn == true) // enters alphabet game mode
		{
			GameVariables.resetVariables();
			ssg.addPanels();
			cards.show(this, "AlphaGamePanel");
		}
		else if (wordGameClickedIn == true) // enters word game mode
		{
			GameVariables.resetVariables();
			ssw.addGamePanels();
			cards.show(this, "WordGamePanel");
		}
		else if (dictClickedIn == true) //takes you from menu to dictionary
		{
			cards.show(this, "DictPanel");
		}
		else if (gameBackToMenuIn == true) // takes you from game mode(s) to menu
		{
			cards.show(this, "MenuPanel");
		}
		else if (backToMenuIn == true)  // takes you from dictionary to menu
		{
			cards.show(this, "MenuPanel");
		}
	}
}

// MenuPanel displays the main menu screen, with the various buttons to access other panels. 
// It adds ActionListeners for each button, and in actionPerformed calls the method 
// cardsChanged in the class PanHolder to display the appropriate panel. 
class MenuPanel extends JPanel implements ActionListener
{
	private PanHolder panHolder; //used to call changeCards() to switch panels
	private JTextArea titleArea; //title box of the menu which reads "SignSpeare"
	private Font titleFont; // font of the titleArea
	private Font menuButtonFont; // font of menu buttons
	
	private JButton infoButton; //press to display information panel
	private JButton playAlphaButton; // press to play alphabet mode 
	private JButton playWordButton; // press to play word mode
	private JButton dictButton; // press to access dictionary
	private JButton quitButton; // press to quit game
	private Dimension buttonDim; // dimensions of each menu button
	private Color darkGray; // 3 colors below are commonly used colors 
	private Color brightPink;
	private Color turquoise;
	private Border menuPanelBorder; //border around the entire menu JPanel

	
	// these booleans are public so they can be accessed in the class PanHolder to know
	// when to change a panel
	private boolean helpClicked; // when the user clicks the information button labeled "Back to Menu"
	private boolean wordGameClicked; // if the user clicks on word mode, set to true
	private boolean alphaGameClicked; // if user clicks on alphabet mode, set to true
	private boolean dictClicked; // if dictionary button clicked, set to true
	
	// MenuPanel() sets the characteristics of the title, or "SignSpeare", as well as 
	// initialize and add ActionListeners to each JButton. 
	public MenuPanel(PanHolder panHolderIn)
	{	
		// initializes all field variables
		panHolder = panHolderIn;
		helpClicked = false;
		darkGray = new Color(46, 49, 61);
		brightPink = new Color(238, 73, 99);
		turquoise = new Color(83, 193, 192);
		menuPanelBorder = BorderFactory.createLineBorder(darkGray, 30);
		//alphaGameClicked = false;
		//wordGameClicked = false;
		dictClicked = false;
		
		// sets characteristics of menu panel
		setLayout(new FlowLayout(FlowLayout.CENTER, 999, 15));  
		setBackground(darkGray);
		setBorder(menuPanelBorder);
		
		// Customizes/Adds the title box
		titleArea = new JTextArea(" Signspeare ", 1, 0);
		titleFont = new Font("Courier", Font.PLAIN, 120);
		titleArea.setFont(titleFont);
		titleArea.setOpaque(true);
		titleArea.setEditable(false);
		titleArea.setBackground(brightPink);
		titleArea.setForeground(Color.WHITE);
		add(titleArea);
		//adds a border to the title box
		Border titleAreaBorder = BorderFactory.createLineBorder(brightPink, 40);
		titleArea.setBorder(titleAreaBorder);
		
		// initializes all menu buttons
		infoButton = new JButton("How to Play");
		playAlphaButton = new JButton("Play Alphabet Mode");
		playWordButton = new JButton("Play Word Mode");
		dictButton = new JButton("Dictionary");
		quitButton = new JButton("Quit");
		
		buttonDim = new Dimension(500, 80);
		// sets dimensions of buttons
		infoButton.setPreferredSize(buttonDim);
		playAlphaButton.setPreferredSize(buttonDim);
		playWordButton.setPreferredSize(buttonDim);
		dictButton.setPreferredSize(buttonDim);
		quitButton.setPreferredSize(buttonDim);
		
		// change button color, font of buttons
		//sets background color
		infoButton.setBackground(turquoise);
		playAlphaButton.setBackground(turquoise);
		playWordButton.setBackground(turquoise);
		dictButton.setBackground(turquoise);
		quitButton.setBackground(turquoise);
		
		// makes the button a solid color
		infoButton.setOpaque(true);
		playAlphaButton.setOpaque(true);
		playWordButton.setOpaque(true);
		dictButton.setOpaque(true);
		quitButton.setOpaque(true);
		infoButton.setBorderPainted(false);
		playAlphaButton.setBorderPainted(false);
		playWordButton.setBorderPainted(false);
		dictButton.setBorderPainted(false);
		quitButton.setBorderPainted(false);
		
		// sets foreground/font color of menu buttons
		infoButton.setForeground(Color.WHITE);
		playAlphaButton.setForeground(Color.WHITE);
		playWordButton.setForeground(Color.WHITE);
		dictButton.setForeground(Color.WHITE);
		quitButton.setForeground(Color.WHITE);
		
		// sets button font
		menuButtonFont = new Font("Courier", Font.PLAIN, 20);
		infoButton.setFont(menuButtonFont);
		playAlphaButton.setFont(menuButtonFont);
		playWordButton.setFont(menuButtonFont);
		dictButton.setFont(menuButtonFont);
		quitButton.setFont(menuButtonFont);
		
		// adds ActionListeners to buttons
		infoButton.addActionListener(this);
		playAlphaButton.addActionListener(this);
		playWordButton.addActionListener(this);
		dictButton.addActionListener(this);
		quitButton.addActionListener(this);
		
		// adds buttons to panel
		add(infoButton);
		add(dictButton);
		add(playAlphaButton);
		add(playWordButton);
		add(quitButton);
		
		setVisible(true);
	}
	
	// in actionPerformed(), based on the source of the click, i.e. the button clicked, 
	// the appropriate field booleans are changed to true and changeCards() is called
	// from the PanHolder class. 
	public void actionPerformed(ActionEvent e)
	{
		// these if statements check which button the user clicks
		if (e.getSource() == infoButton) // "How to Play" button
		{
			helpClicked = true;
			panHolder.changeCards();
			helpClicked = false;
		}
		else if (e.getSource() == playAlphaButton) // alphabet mode button
		{
			alphaGameClicked = true;
			panHolder.changeCards();
			alphaGameClicked = false;
		}
		else if (e.getSource() == playWordButton) // word mode button
		{
			wordGameClicked = true;
			panHolder.changeCards();
			wordGameClicked = false;
		}
		else if (e.getSource() == dictButton) // dictionary button
		{
			dictClicked = true;
			panHolder.changeCards();
			dictClicked = false;
		}
		else if (e.getSource() == quitButton) // quit button
		{
			System.exit(0);
		}
	}
	// getter methods to get the boolean variables if a button is clicked
	public boolean getHelpClicked()
	{
		return helpClicked;
	}
	public boolean getAlphaGameClicked()
	{
		return alphaGameClicked;
	}
	public boolean getWordGameClicked()
	{
		return wordGameClicked;
	}
	public boolean getDictClicked()
	{
		return dictClicked;
	}
}

// InfoPanel displays the help/information on how to play the game. It contains
// a title, body, and exit button labeled "Got it!". It has BorderLayout. 
class InfoPanel extends JPanel implements ActionListener
{
	private boolean quitInfoClicked; // if user clicks quit button labeled "Got it!"
	public PanHolder panHolder2; //used to access changeCards()
	
	private JTextArea infoTitleArea; // displays the title "How to Play"
	private JTextArea infoArea; // displays paragraph on how to play
	private JButton infoQuitButton; // press to exit back to main menu
	private String infoAreaString; // String inside JTextArea infoArea, contains information
	
	private Font infoTitleFont; // font of title
	private Font infoAreaFont; // font of body paragraph
	
	private Color darkGray; // three custom colors
	private Color brightPink;
	private Color turquoise;
	private Border infoPanelBorder; //border for overall panel
	private CompoundBorder infoAreaBorder; //border for body paragraph, ie JTextArea infoArea
	private Border whiteLineBorder; //added to information JTextArea
	private Border pinkLineBorder; //added to information JTextArea
	
	public InfoPanel(PanHolder panIn2)
	{
		// initialize field variables
		panHolder2 = panIn2;
		quitInfoClicked = false;
		darkGray = new Color(46, 49, 61);
		brightPink = new Color(238, 73, 99);
		turquoise = new Color(83, 193, 192);
		
		// sets characteristics of panel, adds border
		setBackground(darkGray);
		setLayout(new FlowLayout(FlowLayout.CENTER, 1500, 15));
		infoPanelBorder = BorderFactory.createLineBorder(darkGray, 20);
		setBorder(infoPanelBorder);
		
		// Customizes/Adds title TextArea
		infoTitleFont = new Font("Courier", Font.PLAIN, 100);
		infoTitleArea = new JTextArea("    How to Play    ", 1, 0);
		infoTitleArea.setEditable(false);
		infoTitleArea.setBackground(brightPink);
		infoTitleArea.setForeground(Color.WHITE);
		infoTitleArea.setOpaque(true);
		infoTitleArea.setFont(infoTitleFont);
		//adds border to title textArea
		Border titleAreaBorder = BorderFactory.createLineBorder(brightPink, 20);
		infoTitleArea.setBorder(titleAreaBorder);
		
		// Customizes/Adds infoArea, or the body paragraph
		infoAreaFont = new Font("Courier", Font.PLAIN, 21);
		infoAreaString = new String("\n\tWelcome to SignSpeare!\n\n\tTo play a game, click the buttons"
				+ "\"Play Alphabet Mode\" or \"Play Word Mode\". In Alphabet Mode, a random letter will "
				+ "appear on the screen. Just like a multiple choice test, click the sign language sign "
				+ "which spells the letter. In word mode, a random word will be generated in sign language. "
				+ "In the text field below, type the word the signs spell. "
				+ "\n\n\tWith each correct answer, your score will increase by 10! A compliment will also be "
				+ "displayed in the pink box in the middle of the screen. If you get an answer wrong, "
				+ "feedback will appear and tell you the correct answer. You will also receive "
				+ "a strike. Try not to make three mistakes in a row, because otherwise you will lose the game!"
				+ "\n\n\tIf you are having trouble, check out the Dictionary from the main menu. It has a chart of "
				+ "all the ASL signs and their corresponding letters. "
				+ "\n\n\t Have fun!");
		infoArea = new JTextArea(infoAreaString, 15, 75);
		infoArea.setFont(infoAreaFont);
		infoArea.setLineWrap(true);
		infoArea.setWrapStyleWord(true);///////////////////////////////////////
		infoArea.setEditable(false);
		infoArea.setBackground(Color.WHITE);
		whiteLineBorder = BorderFactory.createLineBorder(Color.WHITE, 30);
		pinkLineBorder = BorderFactory.createLineBorder(brightPink, 2);
		infoAreaBorder = new CompoundBorder(pinkLineBorder, whiteLineBorder);
		infoArea.setBorder(infoAreaBorder);
		
		// sets characteristics of the quit button, adds listener
		infoQuitButton = new JButton("Back to Menu");
		infoQuitButton.setFont(new Font("Courier", Font.PLAIN, 20));
		infoQuitButton.setBackground(turquoise);
		infoQuitButton.setOpaque(true);
		infoQuitButton.setBorderPainted(false);
		infoQuitButton.setForeground(Color.WHITE);
		infoQuitButton.setPreferredSize(new Dimension(500, 75));
		infoQuitButton.addActionListener(this);
		// add a border to the quit button
		Border quitBorder = BorderFactory.createLineBorder(turquoise, 15);
		infoQuitButton.setBorder(quitBorder);
		
		add(infoTitleArea);
		add(infoArea);
		add(infoQuitButton);
	}
	
	// if the quit button is clicked, changeCards() is called and the panel
	// switches back to the main menu
	public void actionPerformed(ActionEvent e) 
	{
		quitInfoClicked = true;
		panHolder2.changeCards();
		quitInfoClicked = false;
	}
	// getter method to get boolean on whether user has clicked the "back to menu" button
	public boolean getQuitInfoClicked()
	{
		return quitInfoClicked;
	}
	
}

// DictPanel contains a chart of all the letters and their corresponding signs. The user
// can refer to this in order to study, and perform better during the game
class DictPanel extends JPanel implements ActionListener
{
	private boolean backToMenu; // set to true if user clicks back to menu button
	private JTextArea dictTitle; //JTextArea for the panel title
	private JButton backMenuButton; //button to go back to menu
	private Border dictPanelBorder; //border around the entire panel
	private Border dictTitleBorder; //border around the title
	private DictImagePanel dictImagePanel; //panel which holds the actual dictionary image
	private PanHolder panHolder4; //used to call changeCards() to go back to menu screen
	
	private Color darkGray; // three custom colors
	private Color brightPink;
	private Color turquoise;
	
	public DictPanel(PanHolder phIn)
	{	
		darkGray = new Color(46, 49, 61);
		brightPink = new Color(238, 73, 99);
		turquoise = new Color(83, 193, 192);
		// sets characteristics of panel, initialize field variables
		panHolder4 = phIn;
		setBackground(darkGray);
		setLayout(new FlowLayout(FlowLayout.CENTER, 1440, 7));
		dictPanelBorder = BorderFactory.createLineBorder(darkGray, 17);
		setBorder(dictPanelBorder);
		backToMenu = false;
		
		// customizes the title JTextArea
		dictTitle = new JTextArea("   Dictionary   ");
		dictTitle.setBackground(brightPink);
		dictTitle.setForeground(Color.WHITE);
		dictTitle.setFont(new Font("Courier", Font.PLAIN, 80));
		dictTitle.setOpaque(true);
		dictTitleBorder = BorderFactory.createLineBorder(brightPink, 10);
		dictTitle.setBorder(dictTitleBorder);
		dictTitle.setEditable(false);
		
		//creates a panel which holds the dictionary image
		dictImagePanel = new DictImagePanel();
		
		//customizes the button which says back to menu, adds ActionListener
		backMenuButton = new JButton("Back to Menu");
		backMenuButton.setFont(new Font("Courier", Font.PLAIN, 20));
		backMenuButton.setBackground(turquoise);
		backMenuButton.setOpaque(true);
		backMenuButton.setBorderPainted(false);
		backMenuButton.setForeground(Color.WHITE);
		backMenuButton.setPreferredSize(new Dimension(500, 65));
		backMenuButton.addActionListener(this);
		
		//adds components (title, dictionary image, back to menu button) to the panel
		add(dictTitle);
		add(dictImagePanel);
		add(backMenuButton);
	}
	
	// if the back to menu button is clicked, calls changeCards() to go back to the menu
	public void actionPerformed(ActionEvent e)
	{
		backToMenu = true;
		panHolder4.changeCards();
		backToMenu = false;
	}
	
	public boolean getBackToMenu()
	{
		return backToMenu;
	}
}

// This panel class holds the actual dictionary image, and is added to DictPanel above
class DictImagePanel extends JPanel
{
	private Image dictPicture; //dictionary image
	private String dictPictureName; //name of the dictionary image
	
	private Color darkGray; // three custom colors
	
	public DictImagePanel()
	{
		darkGray = new Color(46, 49, 61);
		
		setBackground(darkGray);
		setPreferredSize(new Dimension(820, 550));

		dictPicture = null;
		dictPictureName = new String("Dictionary.jpg");	
		// getDictPicture() uses a try catch block to get the dictionary image
		getDictPicture();
	}
	
	// Uses try catch block, gets the dictionary image
	public void getDictPicture()
	{
		File dictPictureFile = new File(dictPictureName);
		try
		{
			dictPicture = ImageIO.read(dictPictureFile);
		}
		catch(IOException e)
		{
			System.err.println("\n\n\n" + dictPictureName + 
				" can't be found.\n\n\n");
			e.printStackTrace();
		}
	}
	
	// paints the dictionary image within the DictImagePanel.
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(dictPicture, 0, 0, 820, 550, 0, 0, 3327, 2577, this);
	}
}
