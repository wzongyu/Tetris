package view;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame; 
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel; 

public class TetrisGameFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	public TetrisGamePanel tetrisPanel = new TetrisGamePanel(); 
	private JButton netGameButton;		
	public String serverInfo;		
	//服务器端对象
	ServerSocket serverSocket;			
	//客户端对象
	Socket socket;						
	//控制游戏执行的线程
	private Thread gameControllerThread;			
	 
	public TetrisGameFrame(String name) { 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
		setSize(390, 360); 
		setTitle(name); 
		JMenuBar menu = new JMenuBar();
		JMenu gameMenu = new JMenu("暂停or继续");
		setJMenuBar(menu);
		JMenuItem pauseItem = gameMenu.add("暂停");
		pauseItem.addActionListener(this.PauseAction);
		JMenuItem continueItem = gameMenu.add("开始");
		continueItem.addActionListener(this.ContinueAction);
		menu.add(gameMenu); 
		
		netGameButton = new JButton("联机对战"); 
		JPanel panel = new JPanel();
		
		GridLayout gridLayout = new GridLayout(10, 1);
		gridLayout.setVgap(10);
		panel.setLayout(gridLayout);
		panel.add(netGameButton);
		add(panel, BorderLayout.EAST); 
		 
		add(tetrisPanel); 
		setResizable(false);
		addListener();
	} 
//  最初编写程序时测试用	
//	public static void main(String[] args) {
//		TetrisGameFrame tetrisFrame = new TetrisGameFrame("Tetris");
//		tetrisFrame.setVisible(true);
//	}
	
	public void addListener() {
		netGameButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) { 	//联网游戏
				Object[] possibleValues = { "启动服务端", "启动客户端" }; 
				serverInfo = (String)JOptionPane.showInputDialog(null, "Choose one", "选择窗口", 
						JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
				if (serverInfo == null) 
					return;
				gameControllerThread = new Thread(new Runnable() 
				{
					@Override
					public void run() {
						if (serverInfo.equals("启动服务端"))
							new TetrisGameServer(serverSocket, tetrisPanel);
						else 
							new TetrisGameClient(socket, tetrisPanel); 
					}
				});
				gameControllerThread.start();  
			}
		});
	}
	  
	//暂停游戏
	ActionListener PauseAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			tetrisPanel.makeGamePause(true);
		}
	};
	//继续游戏
	ActionListener ContinueAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			tetrisPanel.makeGamePause(false);
		}
	};
}
