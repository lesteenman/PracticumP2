package eindopdracht.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import eindopdracht.client.Game;
import eindopdracht.client.model.player.AIPlayer;
import eindopdracht.client.model.player.HumanPlayer;
import eindopdracht.client.model.player.NetworkPlayer;
import eindopdracht.client.model.player.Player;
import eindopdracht.client.network.Network;
import eindopdracht.model.Board;
import eindopdracht.model.Command;
import eindopdracht.util.Protocol;

import javax.swing.JButton;
import eindopdracht.client.gui.gameboard.FancyBordPanel;
import java.awt.Insets;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class PentagoXLWindow extends JFrame implements WindowListener, ActionListener, Observer{

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PentagoXLWindow frame = new PentagoXLWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JMenuItem connectMenuItem;
	private JMenuItem joinMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenuItem exitMenuItem;
	JFrame connectFrame;
	JFrame newGameFrame;
	Network network;
	private JTextPane chatWindow;
	private JMenuItem hintButton;
	private Player localPlayer;
	private JMenuItem statusLabel;
	private Game game;
	private JPanel panel;
	private FancyBordPanel bord;
	private JTextField chatField;
	private JButton chatButton;
	private PlayerList playerList;
	
	/**
	 * Create the frame.
	 */
	public PentagoXLWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 483);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);
		
		connectMenuItem = new JMenuItem("Connect");
		connectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		connectMenuItem.addActionListener(this);
		mnGame.add(connectMenuItem);
		
		joinMenuItem = new JMenuItem("Join");
		joinMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		joinMenuItem.setEnabled(false);
		joinMenuItem.addActionListener(this);
		mnGame.add(joinMenuItem);
		
		disconnectMenuItem = new JMenuItem("Disconnect");
		disconnectMenuItem.setEnabled(false);
		disconnectMenuItem.addActionListener(this);
		mnGame.add(disconnectMenuItem);
		
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(this);
		mnGame.add(exitMenuItem);
		
		hintButton = new JMenuItem("Give Hint");
		hintButton.addActionListener(this);
		hintButton.setHorizontalAlignment(SwingConstants.RIGHT);
		menuBar.add(hintButton);
		
		statusLabel = new JMenuItem("Not Connected");
		statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		statusLabel.setEnabled(false);
		menuBar.add(statusLabel);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{7, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{293, 20, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.5, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.1, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.ipady = 5;
		gbc_panel.ipadx = 5;
		gbc_panel.weightx = 0.8;
		gbc_panel.insets = new Insets(5, 5, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(panel, gbc_panel);
		//panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.setLayout(null);
		
		bord = new FancyBordPanel();
		panel.addComponentListener(bord);
		GridLayout gridLayout = (GridLayout) bord.getLayout();
		gridLayout.setVgap(8);
		gridLayout.setHgap(8);
		panel.add(bord);
		
		chatWindow = new JTextPane();
		chatWindow.setEditable(false);
		GridBagConstraints gbc_chatWindow = new GridBagConstraints();
		gbc_chatWindow.gridwidth = 2;
		gbc_chatWindow.weighty = 0.8;
		gbc_chatWindow.weightx = 0.2;
		gbc_chatWindow.insets = new Insets(0, 0, 5, 0);
		gbc_chatWindow.fill = GridBagConstraints.BOTH;
		gbc_chatWindow.gridx = 1;
		gbc_chatWindow.gridy = 0;
		contentPane.add(chatWindow, gbc_chatWindow);
		
		chatField = new JTextField();
		chatField.setEditable(false);
		chatField.addActionListener(this);
		
		playerList = new PlayerList();
		GridBagConstraints gbc_playerList = new GridBagConstraints();
		gbc_playerList.insets = new Insets(0, 0, 0, 5);
		gbc_playerList.fill = GridBagConstraints.BOTH;
		gbc_playerList.gridx = 0;
		gbc_playerList.gridy = 1;
		gbc_playerList.weighty = 0.1;
		contentPane.add(playerList, gbc_playerList);
		GridBagConstraints gbc_chatField = new GridBagConstraints();
		gbc_chatField.insets = new Insets(0, 0, 0, 5);
		gbc_chatField.fill = GridBagConstraints.HORIZONTAL;
		gbc_chatField.gridx = 1;
		gbc_chatField.gridy = 1;
		gbc_chatField.weighty = 0.1;
		contentPane.add(chatField, gbc_chatField);
		chatField.setColumns(10);
		
		chatButton = new JButton("Send");
		chatButton.setEnabled(false);
		chatButton.addActionListener(this);
		GridBagConstraints gbc_chatButton = new GridBagConstraints();
		gbc_chatButton.anchor = GridBagConstraints.WEST;
		gbc_chatButton.gridx = 2;
		gbc_chatButton.gridy = 1;
		gbc_chatButton.weighty = 0.1;
		contentPane.add(chatButton, gbc_chatButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(connectMenuItem)) {
			if (connectFrame == null)
				connectFrame = new Connect(this);
		} else if (e.getSource().equals(joinMenuItem)) {
			if (newGameFrame == null)
				newGameFrame = new NewGame(this);
		} else if (e.getSource().equals(disconnectMenuItem)) {
			disconnect();
		} else if (e.getSource().getClass().equals(JMenuItem.class) && ((JMenuItem) e.getSource()).getText().equals("Exit")) {
			if (network != null)
				network.quit();
			System.exit(0);
		} else if (e.getSource().equals(hintButton)) {
			((HumanPlayer)localPlayer).requestHint();
		} else if (e.getSource().equals(chatField)) {
			this.sendChat(chatField.getText());
			this.chatField.setText("");
		} else if (e.getSource().equals(chatButton)) {
			this.sendChat(chatField.getText());
			this.chatField.setText("");
		}
	}

	public void connect(String host, int port) {
		connectFrame = null;
		System.out.println("Connecting with " + host + " on port " + port);
		network = new Network();
		network.addObserver(this);
		if (network.connect(host, port)) {
			connectMenuItem.setEnabled(false);
			joinMenuItem.setEnabled(true);
			disconnectMenuItem.setEnabled(true);
			statusLabel.setText("Connected!");
		}
	}

	public void connectFrameDismissed() {
		if (connectFrame != null)
			connectFrame = null;
	}

	public void newGameFrameDismissed() {
		if (newGameFrame != null)
			newGameFrame = null;
	}

	public void join(String name, int players, boolean humanPlayer) {
		System.out.println("Joining as " + name + " in a lobby with " + players
				+ " players max");
		if (humanPlayer)
			localPlayer = new HumanPlayer();
		else
			localPlayer = new AIPlayer();
		localPlayer.setName(name);
		
		if (network != null) {
			network.join(name, players);
		}
	}

	public void disconnect() {
		if (network != null) {
			network.quit();
			connectMenuItem.setEnabled(true);
			joinMenuItem.setEnabled(false);
			disconnectMenuItem.setEnabled(false);
			statusLabel.setText("Not connected!");
		}
	}
	
	public void sendChat(String chat) {
		chat = chat.replaceAll("\\n","");
		chat = chat.replaceAll("\\t","");
		network.sendChat(chat);
		//chatWindow.setText(chatWindow.getText() + "\n" + chatField.getText());
	}
	
	public void receiveChat(String chat) {
		chatWindow.setText(chatWindow.getText() + chat + "\n");
	}

	@Override
	public void update(Observable sender, Object object) {
		if (object.getClass().equals(Game.class)) {
			this.game = ((Game) object);
			getContentPane().add(bord);
		} else if (object.getClass().equals(Command.class)) {
			Command command = (Command) object;
			System.out.println("Received command was " + command.getCommand());
			
			if (command.getCommand().equals(Protocol.START)) {
				this.playerList.setText("");
				String[] p = command.getArgs();
				this.startGame(p);
			} 
			
			else if (command.getCommand().equals(Protocol.CONNECTED)) {
				localPlayer.setName(command.getArg(0));
				System.out.println("Joined, now has the name "
						+ command.getArg(0));
//				this.playerList.setText(localPlayer.getName());
			}
			
			else if (command.getCommand().equals(Protocol.CHAT_SERVER)) {
				System.out.println("Displaying chat: " + command.getArgString());
				this.receiveChat(command.getArgString());
			}
		}
	}

	public void startGame(String[] p) {
		System.out.println("Starting the game!");
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < p.length; i++) {
			if (p[i].equals(localPlayer.getName())) {
				// was the local player
				System.out.println("Found the local player");
				;
				players.add(localPlayer);
			} else {
				NetworkPlayer newPlayer = new NetworkPlayer();
				System.out.println("Adding a networkplayer");
				network.addNetworkPlayer(newPlayer);
				newPlayer.setName(p[i]);
				players.add(newPlayer);
			}
		}
		this.game = new Game(players);
		game.setLocalPlayer(localPlayer);
		this.bord.setGame(this.game);
		game.addObserver(network);
		game.addObserver(bord);
		
		this.fillPlayerList(players);
		
		//The hint button should only be enabled for a human player
		if (localPlayer.getClass().equals(HumanPlayer.class)) {
			hintButton.setEnabled(true);
			((HumanPlayer)localPlayer).createHintAI();
			((HumanPlayer)localPlayer).setBordPanel(bord);
		} 
		//And if it's an AI player, its AI should be initialized
		else if (localPlayer.getClass().equals(AIPlayer.class)) {
			((AIPlayer)localPlayer).initializeAI();
		}
		game.start();
		
		chatField.setEditable(true);
		chatButton.setEnabled(true);
	}
	
	public void fillPlayerList(ArrayList<Player> players) {
		for (Player player:players) {
			playerList.addPlayer(player);
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		e.getWindow().dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}
}
