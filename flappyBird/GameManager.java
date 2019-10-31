package flappyBird;

import java.util.HashMap;
import java.util.PriorityQueue;

public class GameManager
{
	public static int gameId;
	public static int numberOfPlayers;
	public static PriorityQueue<Integer> scores;
	public static HashMap<Integer, String> results;
	
	public GameManager()
	{
		gameId = 0;
		scores = new PriorityQueue<Integer>();
		results = new HashMap<Integer, String>();
	}
}