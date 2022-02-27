package flappyBird;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class FlappyBird implements ActionListener, MouseListener
{
	public static FlappyBird flappyBird;
	public JFrame jframe;
	public final int WIDTH = 800, HEIGHT = 800;
	public Renderer renderer;
	public Rectangle bird;
	public int ticks, yMotion, speed, score;
	public ArrayList<Rectangle> columns;
	public Random rand;
	public boolean gameOver, started, leaderboard, restartAfterLeaderboard;
	public String PlayerName;
	public Timer timer;
	
	
	public FlappyBird()
	{
		jframe = new JFrame();
		timer = new Timer(15, this);
		
		renderer = new Renderer();
		rand = new Random();
		
		jframe.add(renderer);
		jframe.addMouseListener(this);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.setTitle("Flappy Bird");
		jframe.setResizable(false);
		jframe.setVisible(true);
		
		// Create list of tubes, bird starting (x, y), the position of bird is WIDTH/3 and HEIGHT/2, respectively.
		columns = new ArrayList<Rectangle>();
		bird = new Rectangle(WIDTH/3, HEIGHT/2 - 10, 20, 20);

		// create 4 green tubes		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		timer.start();
	}
	
	public void addColumn(boolean initialise)
	{
		// column/tube attributes
		int space = 250 + rand.nextInt(150);
		int width = 100;
		int height = 50 + rand.nextInt(300);
		
		// this is to create the columns/tubes before the bird has started moving (they're not visible at spawn)
		if(initialise)
		{
			// add each generated column to the "columns" ArrayList for later..
			// bottom column, note that the 150 is the height of the ground..
			columns.add(new Rectangle(WIDTH + width + (columns.size() * space) + 300, HEIGHT - 150 - height, width, height));

			//top column
			columns.add(new Rectangle(WIDTH + width + (columns.size() * space), 0, width, HEIGHT - height - space));
		}
		else
		{
			columns.add(new Rectangle(columns.get(columns.size()-1).x + 600, HEIGHT - height - 150, width, height));
			columns.add(new Rectangle(columns.get(columns.size()-1).x, 0, width, HEIGHT - height - space));
		}
	}
	
	public void paintColumn(Graphics g, Rectangle column)
	{
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{		
		ticks++;
		speed = 10;
		
		if (started && !gameOver)
		{

			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);
				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15)
			{
				yMotion++;
			}

			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				// the below is true for 2 (top + bottom) columns, so it removes 2 columns...
				if (column.x + column.width < 0)
				{
					columns.remove(column);

					// therefore make sure to only generate 2 more columns ONCE !!!
					// so since only one of the columns is a top column, then gen 2 more cols
					if(column.y == 0)
					{
						addColumn(false);
					}
				}
			}

			bird.y += yMotion;

			// checking for collisions + scoring mechanism
			for (Rectangle column : columns)
			{
				if(column.y == 0 && bird.x + bird.width/2 > column.x + column.width/2 - 10 && bird.x + bird.width/2 < column.x + column.width + 10)
				{
					if(ticks % 7 == 0)	//The score used to increase in multiples of 7, if you know why this line fixes the issue... please let me know!
					{
						score++;
					}
				}
				
				if (column.intersects(bird))
				{
					gameOver = true;
					bird.x = column.x - bird.width;
				}
			}
			
			if(bird.y + bird.height >= HEIGHT - 150)
			{
				gameOver = true;
			}
		}
		renderer.repaint();
	}
	
	public void jump()
	{
		if(gameOver)
		{
			//add name + score to scoreManager
			GameManager.results.put(this.score, this.PlayerName);
			
			//add score to PriorityQueue, times -1 to arrange in descending number...
			GameManager.scores.add(this.score * -1);
			
			//if all players played a game...
			if(GameManager.gameId == GameManager.numberOfPlayers)
			{
				leaderboard = true;
			}
			
			if(!leaderboard)
			{
				this.jframe.dispose();
				this.score = 0;
				new NewPlayer();
				
				columns.clear();
				yMotion = 0;
			
				bird = new Rectangle(WIDTH/3, HEIGHT/2 - 10, 20, 20);
				addColumn(true);
				addColumn(true);
				addColumn(true);
			}
		}
		
		if(!started)
		{
			started = true;
		}
		else if(!gameOver)
		{
			if(yMotion > 0)
			{
				yMotion = 0;
			}
			
			yMotion -= 9;
		}
		
		if(restartAfterLeaderboard)
		{
			this.jframe.dispose();
			new GameManager();
			new NewPlayer();
		}
	}
	
	public void repaint(Graphics g)
	{
		//sky
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//ground
		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT-150, WIDTH, 150);
		
		//grass on top of ground
		g.setColor(Color.green);
		g.fillRect(0, HEIGHT-150, WIDTH, 25);
		
		//bird
		g.setColor(Color.red);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);

		//Paint all columns green
		for(Rectangle column : columns)
		{
			paintColumn(g, column);
		}
		
		//Game font
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 100));
		
		if(!started)
		{
			g.setColor(Color.white);
			g.drawString("Click to start!", 75, HEIGHT/2);
		}
		
		if(gameOver)
		{
			g.setColor(Color.white);
			g.drawString("Game Over!", WIDTH/7, HEIGHT/2 - 50);
			
			if(leaderboard)
			{
				int temp = 0;
				timer.stop();
				g.setFont(new Font("serif", Font.BOLD, 35));
				
				while(!GameManager.scores.isEmpty())
				{
					temp++;
					int currentHighestScore = GameManager.scores.remove() * -1;
					String correspondingName = new String(GameManager.results.get(currentHighestScore));
					
					
					System.out.println(correspondingName + " - " + currentHighestScore);
					g.drawString(correspondingName + " - " + currentHighestScore, WIDTH/3, HEIGHT/2 + 50*temp);
				}
				//(note to self) - try to implement leaderboard pages displaying at most 5 players...
				
				g.drawString("Click To Restart", WIDTH/3, HEIGHT/2 + 250);
				restartAfterLeaderboard = true;
			}
		}
		
		//Display score on top of the screen
		g.drawString(String.valueOf(score), WIDTH/2 - 25, 100);
		
		//Display player name top left corner
		Font nameFont = new Font("serif", Font.BOLD, 35);
		g.setFont(nameFont);
		g.drawString(PlayerName, 20, 45);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		jump();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
	public static void main(String[] args)
	{
		new GameManager();
		new NewPlayer();
	}
}