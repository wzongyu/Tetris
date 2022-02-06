package view;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket; 

import javax.swing.JOptionPane;
 

public class TetrisGameClient { 
	public TetrisGameClient(Socket socket, TetrisGamePanel tetris) {
		try {
			tetris.initGameView();
			tetris.timer.stop();
			socket = new Socket("localhost", 8765);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			tetris.timer.start();
			while (true) {  
				int[][] gameMap = new int[TetrisGamePanel.BLOCK_HEIGHT][TetrisGamePanel.BLOCK_WIDTH];
				for (int i = 0; i < gameMap.length; i++) {
					for (int j = 0; j < gameMap[i].length; j++) {
						gameMap[i][j] = tetris.getWholeBlockMap()[i][j];
					}
				}  
				oos.writeObject(gameMap); 
				oos.writeInt(tetris.getScore());
				oos.writeBoolean(tetris.getGameState());
				oos.flush();
				
				int[][] blockMap = (int[][])ois.readObject(); 
				int score = (int) ois.readInt();
				boolean loseGame = (boolean)ois.readBoolean();
				tetris.changeOtherPlayserWholeBlockMap(blockMap, score);
				
				if (loseGame == true) 
				{
					tetris.timer.stop();
					JOptionPane.showMessageDialog(tetris, "你赢得了比赛！");
					tetris.initGameView();
					socket.close();
					return;
				}
				if (tetris.getGameState() == true) {
					tetris.timer.stop(); 
					tetris.initGameView();
					socket.close();
					return;
				}
				Thread.sleep(1000);
			}
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
	} 
}



