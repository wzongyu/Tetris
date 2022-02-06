package view;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;
 

public class TetrisGameServer { 
	public TetrisGameServer(ServerSocket serverSocket, TetrisGamePanel tetris) {
		try {
			tetris.initGameView();
			tetris.timer.stop();
			serverSocket = new ServerSocket(8765); 
			Socket socket = serverSocket.accept(); 	//连接socket
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());			
			tetris.timer.start();
			while (true) {
				//服务端先读后写
				int[][] blockMap = (int[][])objectInputStream.readObject();  
				int score = (int) objectInputStream.readInt();
				boolean ifGameEndWithLose = (boolean) objectInputStream.readBoolean();
				tetris.changeOtherPlayserWholeBlockMap(blockMap, score);	
				 
				int[][] gameMap = new int[TetrisGamePanel.BLOCK_HEIGHT][TetrisGamePanel.BLOCK_WIDTH];
				for (int i = 0; i < gameMap.length; i++) {
					for (int j = 0; j < gameMap[i].length; j++) {
						gameMap[i][j] = tetris.getWholeBlockMap()[i][j];
					}
				}  
				objectOutputStream.writeObject(gameMap); 
				objectOutputStream.writeInt(tetris.getScore());
				objectOutputStream.writeBoolean(tetris.getGameState());
				objectOutputStream.flush();

				if (ifGameEndWithLose == true) {
					tetris.timer.stop();
					JOptionPane.showMessageDialog(tetris, "你赢得了比赛！");
					tetris.initGameView();
					serverSocket.close();
					return;
				}
				if (tetris.getGameState() == true) {
					tetris.timer.stop(); 
					tetris.initGameView();
					serverSocket.close();
					return;
				}
				
				Thread.sleep(1000);
			}
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
}
