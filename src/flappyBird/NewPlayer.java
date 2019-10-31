package flappyBird;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class NewPlayer implements ActionListener
{
	public JFrame jframe;
	public JLabel label, playerNum;
	public JButton button;
	public JTextField textField;
	public SpinnerModel model;
	public JSpinner spinner;
	
	public NewPlayer()
	{
		GameManager.gameId++;
		
		//Player Name Screen
		jframe = new JFrame();
		int width = 800, height = 600;
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(width, height);
		jframe.setResizable(true);
		
		//Layout
		jframe.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		//Components
		switch (GameManager.gameId) 
		{
		case 1:
			label = new JLabel(GameManager.gameId + "st Player name", SwingConstants.CENTER);
			break;
		case 2:
			label = new JLabel(GameManager.gameId + "nd Player name", SwingConstants.CENTER);
			break;
		case 3:
			label = new JLabel(GameManager.gameId + "rd Player name", SwingConstants.CENTER);
			break;
		default:
			label = new JLabel(GameManager.gameId + "th Player name", SwingConstants.CENTER);
			break;
		}
		
		button = new JButton("Submit");
		textField = new JTextField(15);
		
	    
		//button clickable
		button.addActionListener(this);
		
		//adding components
		jframe.add(label, gbc);
		jframe.add(textField, gbc);
				
		//if first player, then show spinner
		if(GameManager.gameId == 1)
		{
			//num of players selection
			model = new SpinnerNumberModel(1, 1, 100, 1);
		    spinner = new JSpinner(model);
		    playerNum = new JLabel("How Many Players?", SwingConstants.CENTER);
		    
			jframe.add(playerNum, gbc);
			jframe.add(spinner, gbc);
		}
		
		jframe.add(button, gbc);
		
		jframe.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		
		if(GameManager.gameId == 1)
		{
			GameManager.numberOfPlayers = (Integer) spinner.getValue();
		}
		FlappyBird.flappyBird = new FlappyBird();
		FlappyBird.flappyBird.PlayerName = this.getName();
		this.jframe.dispose();
	}
	
	public String getName()
	{
		return this.textField.getText();
	}
}