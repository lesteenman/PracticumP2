package eindopdracht.server;

import java.util.Observable;
import java.util.Observer;

import eindopdracht.server.model.Set;
import eindopdracht.server.model.Turn;
import eindopdracht.server.network.PlayerHandler;

public class ServerPlayer implements Observer {
	private String name;
    private int preferredNumberOfPlayers;
    private Lobby lobby;
    private ServerGame game;
	int state;
    private int color;
    private PlayerHandler handler;
    
    private int numberOfTiles;
	
	public static final int TURNING = 2;
	public static final int SETTING = 1;
	public static final int IDLE = 0;
    
	/**
	 * Create a network player with the given handler
	 * 
	 * @param handler
	 */
    public ServerPlayer(PlayerHandler handler) {
    	this.handler = handler;
    }
	
    /**
     * Set the preferred lobby type this player wants to join
     * @param number the amount of players in the game
     */
	public void setNumberOfPlayers(int number) {
		this.preferredNumberOfPlayers = number;
	}
	
	/**
     * The preferred lobby type this player wants to join
     * @return number the amount of players in the preferred game
     */
	public int preferredNumberOfPlayers() {
		return this.preferredNumberOfPlayers;
	}
	
	/**
	 * Send the given message to the player this ServerPlayer represents
	 * @param message to send
	 * @ensure the message is sent to the hooked player
	 */
	public void sendMessage(String message) {
		handler.sendMessage(message);
	}
	
	/*
	 * Handler methods; These methods are called by the network handler and the player will act like he's performing
	 * that specific task.
	 */
	
	/**
	 * Player wants to join a lobby
	 * Not necessary, the playerhandler handles this.
	 */
	@Deprecated
	public void join() {
		System.out.println("Now trying to join");
		//TODO write joining code
	}
	
	/**
	 * Player wants to set a tile
	 * @param block
	 * @param tile
	 * @require 0 <= (block, tile) <= 8
	 * @ensure the game is notified of the desired set
	 */
	public void setTile(int block, int tile) {
		Set set = new Set(this);
		set.setBlock(block);
		set.setTile(tile);
		game.set(set);
	}
	
	
	/**
	 * Players wants to rotate a block.
	 * @param block block to turn
	 * @param direction 1=CW, 2=CCW
	 * @require 0 <= block <= 8, direction == (1||2)
	 * @ensure the game is notified of the desired turn
	 */
	public void turnBlock(int block, int direction) {
		Turn turn = new Turn(this);
		turn.setBlock(block);
		turn.setRotation(direction);
		game.turn(turn);
	}
	
	/**
	 * Called if the player wants to quit the server
	 * @ensure the player is gracefully removed from the server
	 */
	public void quit() {
		System.out.println("Trying to quit. Could not escape!");
		//TODO implement graceful quitting
	}
	
	/**
	 * Player is chatting
	 * @param message the hooked player said
	 * @ensure the message will be passed on to game and broadcasted to all players in the game
	 */
	public void chat(String message) {
		game.chat(message, this);
	}

	/**
	 * @return the game
	 */
	public ServerGame getGame() {
		return this.game;
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(ServerGame game) {
		this.game = game;
	}

	/**
	 * @return the lobby
	 */
	public Lobby getLobby() {
		return lobby;
	}

	/**
	 * @param lobby the lobby to set
	 */
	public void setLobby(Lobby lobby) {
		this.lobby = lobby;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return the state of this current player
	 * @ensure 0 if idle
	 * @ensure 1 if setting
	 * @ensure 2 if turning
	 */
	public int getState()
	{
		return this.state;
	}
	
	/**
	 * 
	 * @param state the state of this current player
	 * @require 0 if idle
	 * @require 1 if setting
	 * @require 2 if turning
	 */
	public void setState(int state)
	{
		if (state >= 0 && state <= 2)
		{
			this.state = state;
		}
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 * @require 1 <= color <= 4
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * 
	 * @return The number of balls this player has left
	 */
	public int getNumberOfTiles() {
		return numberOfTiles;
	}

	/**
	 * 
	 * @param numberOfTiles the maximum number of balls this player can have
	 */
	public void setNumberOfTiles(int numberOfTiles) {
		this.numberOfTiles = numberOfTiles;
	}

	@Override
	public void update(Observable o, Object arg) {
		/*
		 * The player listens for sets and turns to determine its own current state.
		 */
		if (arg.getClass().equals(Set.class)) {
			Set set = (Set)arg;
//			System.out.println("    Player " + getName() + " with state " + getState() + " received set: " + set.toString());
			//Was a set for/by this player
			if (set.getPlayer().equals(this)) {
				if (set.getExecuted()) {
					//Set was executed, set to idle
					this.setState(IDLE);
//					System.out.println("    Was executed, setting to idle");
				} else {
					//Set has to be executed, set to Setting
					this.setState(SETTING);
//					System.out.println("    Was not executed, setting to setting");
				}
			}
		}
		
		else if (arg.getClass().equals(Turn.class)) {
			Turn turn = (Turn)arg;
//			System.out.println("    Player " + getName() + " with state " + getState() + " received turn: " + turn.toString());
			//was a turn for/by this player
			if (turn.getPlayer().equals(this)) {
				if (turn.getExecuted()) {
					//turn was executed, setting to idle
					this.setState(IDLE);
//					System.out.println("    Was executed, setting to idle");
				} else {
					//Turn still has to be executed, set to turning
					this.setState(TURNING);
//					System.out.println("    Was not executed, setting to turning");
				}
			}
		}
	}
}
