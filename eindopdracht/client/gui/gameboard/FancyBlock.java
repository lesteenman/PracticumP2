package eindopdracht.client.gui.gameboard;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eindopdracht.model.Block;
import eindopdracht.model.Color;

public class FancyBlock extends JPanel implements MouseMotionListener,
		MouseListener, ComponentListener, ActionListener {
	private BufferedImage blockImage;
	private BufferedImage highlightImage;

	private BufferedImage redBall;
	private BufferedImage redBallHighlight;
	private BufferedImage blueBall;
	private BufferedImage blueBallHighlight;
	private BufferedImage greenBall;
	private BufferedImage greenBallHighlight;
	private BufferedImage yellowBall;
	private BufferedImage yellowBallHighlight;
	
	private ImageIcon cwImage;
	private ImageIcon cwImageHighlight;
	private ImageIcon ccwImage;
	private ImageIcon ccwImageHighlight;
	
	private JButton cwButton;
	private JButton ccwButton;

	private ArrayList<Point> ballPositions;
	private ArrayList<Integer> balls;
	private int highlightedBall; // -1 for none, 0-8 for ball
	private FancyBordPanel bord;

	private int blockIndex;
	private int state;

	public static int DISABLED = 0;
	public static int SETTING = 1;
	public static int TURNING = 2;

	public FancyBlock(FancyBordPanel bord, int blockIndex) {
		System.out.println("Created block with index " + blockIndex);
		
		this.loadImages();
		this.buildGUI();
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addComponentListener(this);
		highlightedBall = -1;
		this.blockIndex = blockIndex;
		this.bord = bord;

		balls = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			balls.add(i);
		}
	}

	public void loadImages() {
		try {
			blockImage = ImageIO.read(new File(
					"eindopdracht/resources/PentagoXL piece.png"));
			highlightImage = ImageIO.read(new File(
					"eindopdracht/resources/Hover.png"));
			redBall = ImageIO.read(new File(
					"eindopdracht/resources/Red Ball.png"));
			blueBall = ImageIO.read(new File(
					"eindopdracht/resources/Blue Ball.png"));
			greenBall = ImageIO.read(new File(
					"eindopdracht/resources/Green Ball.png"));
			yellowBall = ImageIO.read(new File(
					"eindopdracht/resources/Black Ball.png"));
			
			redBallHighlight = ImageIO.read(new File(
					"eindopdracht/resources/Red Ball Highlight.png"));
			blueBallHighlight = ImageIO.read(new File(
					"eindopdracht/resources/Blue Ball Highlight.png"));
			greenBallHighlight = ImageIO.read(new File(
					"eindopdracht/resources/Green Ball Highlight.png"));
			yellowBallHighlight = ImageIO.read(new File(
					"eindopdracht/resources/Black Ball Highlight.png"));
			
			cwImage = new ImageIcon("eindopdracht/resources/Turn Right.png");
			cwImageHighlight = new ImageIcon("eindopdracht/resources/Turn Right Highlight.png");
			ccwImage = new ImageIcon("eindopdracht/resources/Turn Left.png");
			ccwImageHighlight = new ImageIcon("eindopdracht/resources/Turn Left Highlight.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buildGUI() {
		this.setLayout(null);
		
		cwButton = new JButton();
		cwButton.setIcon(cwImage);
		cwButton.setOpaque(false);
		cwButton.setBorderPainted(false);
		cwButton.setContentAreaFilled(false);
		cwButton.addActionListener(this);
		cwButton.setRolloverEnabled(true);
		cwButton.setRolloverIcon(cwImageHighlight);
		
		ccwButton = new JButton();
		ccwButton.setIcon(ccwImage);
		ccwButton.setOpaque(false);
		ccwButton.setBorderPainted(false);
		ccwButton.setContentAreaFilled(false);
		ccwButton.addActionListener(this);
		ccwButton.setRolloverEnabled(true);
		ccwButton.setRolloverIcon(ccwImageHighlight);	
		
		this.repaint();
	}

	public void setTiles(ArrayList<Integer> balls) {
		this.balls = balls;
	}

	public ArrayList<Integer> getTiles() {
		return this.balls;
	}

	public void setState(int state) {
		if (state == TURNING) {
			this.showRotateButtons();
		} else if (this.state == TURNING) {
			this.hideRotateButtons();
		}
		this.state = state;
		this.repaint();
	}

	public int getState() {
		return this.state;
	}
	
	public void showRotateButtons() {
		cwButton.setBounds(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
		ccwButton.setBounds(0, 0, this.getWidth()/2, this.getHeight());
		this.add(cwButton);
		this.add(ccwButton);	
	}
	
	public void hideRotateButtons() {
		this.remove(cwButton);
		this.remove(ccwButton);
	}

	/**
	 * 
	 * @param ball
	 *            0-8
	 * @return position within this component
	 */
	public Point positionForBall(int ball) {
		double width = this.getWidth();

		// In het originele plaatje gelden deze formaten: blok: 236*236 bal:
		// 52*52 ruimte bal-bal: 13 ruimte bal-rand: 27

		// Eerst berekenen we de verhouding schermgrootte:origineel
		double proportion = width / (double) 236;
		// Het balletje linksboven zal nu op positie (27+(52/2)) * positie
		// zitten, en alle andere balletjes steeds (52+13) * proportion er
		// vanaf.
		int first = (int) (53 * proportion);
		Point point = new Point(first, first);
		int ballCol = ball % 3;
		int ballRow = (int) Math.floor(ball / 3);

		point.x += ballCol * (65 * proportion);
		point.y += ballRow * (65 * proportion);

		return point;
	}

	public int getSizeOfBall() {
		double width = this.getWidth();
		double proportion = width / (double) 236;
		return (int) (52 * proportion);
	}

	public void updateTiles(Block block) {
		ArrayList<Integer> newTiles = new ArrayList<Integer>();
		for (int t = 0; t < 9; t++) {
			newTiles.add(block.GetTile(t).getColor());
		}
		this.setTiles(newTiles);
	}

	/**
	 * Called when this component has to draw its contents
	 */
	public void paintComponent(Graphics g) {
		g.drawImage(blockImage, 0, 0, this.getSize().width,
				this.getSize().height, null);

		// Draw the highlights if setting
		if (highlightedBall >= 0 && state == SETTING) {
			Point highlightPos = this.positionForBall(highlightedBall);
			int hx = highlightPos.x;
			int hy = highlightPos.y;
			int size = getSizeOfBall();
			g.drawImage(highlightImage, hx - size / 2, hy - size / 2, size,
					size, null);
		}

		// Draw the rotation thingies if turning

		// Draw the balls
		for (int i = 0; i < balls.size(); i++) {
			if (balls.get(i) > 0) {
				// Tile was set
				Point p = this.positionForBall(i);
				int size = getSizeOfBall();
				BufferedImage ballImage;
				switch (balls.get(i)) {
				case Color.RED:
					if (highlightedBall == i)
						ballImage = redBall;
					else
						ballImage = redBallHighlight;
					break;
				case Color.BLUE:
					if (highlightedBall == i)
						ballImage = blueBall;
					else
						ballImage = blueBallHighlight;
					break;
				case Color.GREEN:
					if (highlightedBall == i)
						ballImage = greenBall;
					else
						ballImage = greenBallHighlight;
					break;
				case Color.YELLOW:
					if (highlightedBall == i)
						ballImage = yellowBall;
					else
						ballImage = yellowBallHighlight;
					break;
				default:
					ballImage = redBall;
					break;
				}
				g.drawImage(ballImage, p.x - size / 2, p.y - size / 2, size,
						size, null);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (highlightedBall >= 0) {
			// A ball was already highlighted, check if this is still the case
			Point hp = this.positionForBall(highlightedBall);
			double d = hp.distance(arg0.getPoint());
			if (d > getSizeOfBall() / 2) {
				highlightedBall = -1;
				this.repaint();
			}
			// System.out.println(hp.distance(arg0.getPoint()) + ", was " + ((d
			// > getSizeOfBall()/2)?"":"NOT") + " within range");
		}

		if (highlightedBall < 0) {
			// System.out.println("No ball was selected");
			// No ball selected. Can't be an else statement because it might
			// have changed in the first if.
			for (Point p : ballPositions) {
				double d = p.distance(arg0.getPoint());
				if (d < getSizeOfBall() / 2) {
					highlightedBall = ballPositions.indexOf(p);
					this.repaint();
				}
			}
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		ballPositions = new ArrayList<Point>();
		for (int i = 0; i <= 8; i++) {
			ballPositions.add(this.positionForBall(i));
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		ballPositions = new ArrayList<Point>();
		for (int i = 0; i <= 8; i++) {
			ballPositions.add(this.positionForBall(i));
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (state != DISABLED) {
			if (state == SETTING) {
				// Was setting
				if (highlightedBall >= 0 && balls.get(highlightedBall) == 0) {
					System.out.println("Selected " + highlightedBall);
					bord.set(blockIndex, highlightedBall);
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(cwButton)) {
			bord.turn(blockIndex, Block.CW);
		} else if (e.getSource().equals(ccwButton)) {
			bord.turn(blockIndex, Block.CCW);
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}
