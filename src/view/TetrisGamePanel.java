package view;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics; 
import java.awt.Point; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener; 
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
 

public class TetrisGamePanel extends JPanel {
	public static final int BLOCK_WIDTH = 12;
	public static final int BLOCK_HEIGHT = 22;
	private static final int TIME_DELAY = 500;
	private static final long serialVersionUID = 1L;
	private static int blockSize = 12;
	private boolean ifGameEndWithLose; 
	
	//俄罗斯方块的颜色
	private static final Color[] COLOR_GROUP = new Color[] {Color.blue, Color.gray, 
			 Color.orange, Color.pink, Color.red, Color.yellow};
	private int currentBlockColor, nextBlockColor;
	
	//声音
	private AudioClip soundMove;
	// 存放已经固定的方块
	private int[][] wholeBlockMap = new int[BLOCK_HEIGHT][BLOCK_WIDTH];
	private int[][] otherWholeBlockMap;
	private boolean setOtherFinished = false;
	
	// 分数
	private int score = 0;
	private int otherScore = 0;
		
	//是否暂停
	private boolean ifPause = false;

	// 7种形状
	static int[][][] shapeGroup = TetrisGameViewBlock.SHAPE;
	
	// 下落方块的位置,左上角坐标
	private Point currentBlockPosition;

	// 当前方块矩阵和  下一个方块矩阵
	private int[][] currentBlockMap;
	private int[][] nextBlockMap;
	/**
	 * 范围[0,28) 7种，每种有4种旋转状态，共4*7=28 %4获取旋转状态 /4获取形状
	 */
	private int currentBlockState;
	private int nextBlockState;
	
	//计时器
	public Timer timer;

	public TetrisGamePanel() {
		initGameView();
		timer = new Timer(TetrisGamePanel.TIME_DELAY, timerListener);
		setBackground(Color.LIGHT_GRAY);
		addKeyListener(keyListener);
		try {
			@SuppressWarnings("deprecation")
			URL urlMove = new File("sound/SOUND_MOVE.WAV").toURL();
			soundMove = Applet.newAudioClip(urlMove);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	} 

	private int[][] changeBlockStyle(int[][] currentBlockMap, double angel){
		int height = currentBlockMap.length;
		int width = currentBlockMap[0].length;
		int[][] resultBlockMap = new int[height][width];
		// 计算旋转中心
		float centerX = (width - 1) / 2f;
		float centerY = (height - 1) / 2f;
		// 逐点计算变换后的位置
		for (int i = 0; i < currentBlockMap.length; i++) {
			for (int j = 0; j < currentBlockMap[i].length; j++) {
				//计算相对于旋转中心的坐标
				float relativeX = j - centerX;
				float relativeY = i - centerY;
				float resultX = (float) (Math.cos(angel) * relativeX - Math.sin(angel) * relativeY);
				float resultY = (float) (Math.cos(angel) * relativeY + Math.sin(angel) * relativeX);
				//将结果坐标还原
				Point orginPoint = new Point(Math.round(centerX + resultX), Math.round(centerY + resultY));
				if (currentBlockMap[i][j] == -1) 
					resultBlockMap[orginPoint.y][orginPoint.x] = -1;
				else 
					resultBlockMap[orginPoint.y][orginPoint.x] = currentBlockColor;
			}
		}
		return resultBlockMap;
	}
	
	public void makeGamePause(boolean value) {
		ifPause = value;
		if (ifPause) timer.stop();
		else timer.restart();
		
		repaint();
	}
	
	private int[][] getGameBlockMap(int blockState) {
		int blockShape = blockState / 4;
		int blockArc = blockShape % 4; 
		return changeBlockStyle(shapeGroup[blockShape], Math.PI / 2 * blockArc);
	}
	
	public void initGameView() {
		//清空Map
		for (int i = 0; i < wholeBlockMap.length; i++) {
			for (int j = 0; j < wholeBlockMap[i].length; j++) {
				wholeBlockMap[i][j] = -1;
			}
		}
		score = 0;
		// 初始化第一次生成的方块和下一次生成的方块
		currentBlockState = (int)(Math.random() * 1000) % (shapeGroup.length * 4);
		nextBlockState = (int)(Math.random() * 1000) % (shapeGroup.length * 4);
		currentBlockMap = getGameBlockMap(currentBlockState);
		nextBlockMap = getGameBlockMap(nextBlockState);
		// 初始化方块的颜色
		currentBlockColor = (int)(Math.random() * 1000) % COLOR_GROUP.length;
		nextBlockColor = (int)(Math.random() * 1000) % COLOR_GROUP.length;
		// 计算方块位置
		currentBlockPosition = new Point(BLOCK_WIDTH / 2 - currentBlockMap[0].length, -currentBlockMap.length);
		
		ifGameEndWithLose = false; 
		repaint();
	}
	
	  
	//绘制游戏画面
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//画墙
		g.setColor(Color.black);
		for (int i = 0; i < BLOCK_HEIGHT + 1; i++) {
			g.drawRect(0 * blockSize, i * blockSize + 10, blockSize, blockSize);	
			g.drawRect((BLOCK_WIDTH + 1) * blockSize, i * blockSize + 10, blockSize, blockSize);
		}
		for (int i = 0; i < BLOCK_WIDTH; i++) {
			g.drawRect((i + 1) * blockSize, BLOCK_HEIGHT * blockSize + 10, blockSize, blockSize);
		}
		//画当前方块 
		for (int i = 0; i < currentBlockMap.length; i++) {
			for (int j = 0; j < currentBlockMap[i].length; j++) {
				if (currentBlockMap[i][j] != -1) {
					g.setColor(Color.black);
					g.fillRect((1 + currentBlockPosition.x + j) * blockSize, 
							(currentBlockPosition.y + i) * blockSize + 10, blockSize, blockSize);

					g.setColor(COLOR_GROUP[currentBlockColor]);
					g.fillRect((1 + currentBlockPosition.x + j) * blockSize + 1, 
							(currentBlockPosition.y + i) * blockSize + 1 + 10, blockSize - 1, blockSize - 1);
				} 
			}
		}
		// 画已经固定的方块
		for (int i = 0; i < BLOCK_HEIGHT; i++) {
			for (int j = 0; j < BLOCK_WIDTH; j++) {
				if (wholeBlockMap[i][j] != -1) {
					g.setColor(Color.black);
					g.drawRect(blockSize + j * blockSize, i * blockSize + 10, blockSize, blockSize);
					g.setColor(COLOR_GROUP[wholeBlockMap[i][j]]);
					g.fillRect(blockSize + j * blockSize + 1, 
							i * blockSize + 1 + 10, blockSize - 1, blockSize - 1); 
				}
			}
		}
		//绘制下一个方块
		for (int i = 0; i < nextBlockMap.length; i++) {
			for (int j = 0; j < nextBlockMap[i].length; j++) {
				if (nextBlockMap[i][j] != -1) {
					g.setColor(Color.black);
					g.fillRect(190 + j * blockSize, 30 + i * blockSize + 10, blockSize, blockSize);
					g.setColor(COLOR_GROUP[nextBlockColor]);
					g.fillRect(190 + j * blockSize + 1, 30 + i * blockSize + 1 + 10, 
							blockSize - 1, blockSize - 1);
				}
			}
		}
		// 绘制其他信息
		g.setColor(Color.black);
		g.drawString("游戏分数:" + score, 190, 20);
		
		//绘制暂停
		if (ifPause){
			g.setColor(Color.white);
			g.fillRect(70, 100, 50, 20);
			g.setColor(Color.black);
			g.drawRect(70, 100, 50, 20);
			g.drawString("PAUSE", 75, 113);
		} 
		//画对手的俄罗斯方块地图
		if (setOtherFinished) {
			drawOtherBlockMap(g);
		} 
	}

	//画对手的俄罗斯方块地图
	private void drawOtherBlockMap(Graphics g) {
		int temp = blockSize;
		blockSize = 7;
		//画墙
		g.setColor(Color.black);
		for (int i = 0; i < BLOCK_HEIGHT + 1; i++) {
			g.drawRect(0 * blockSize + 185, i * blockSize + 110, blockSize, blockSize);	
			g.drawRect((BLOCK_WIDTH + 1) * blockSize + 185, i * blockSize + 110, blockSize, blockSize);
		}
		for (int i = 0; i < BLOCK_WIDTH; i++) {
			g.drawRect((i + 1) * blockSize + 185, BLOCK_HEIGHT * blockSize + 110, blockSize, blockSize);
		} 
		
		// 画已经固定的方块
		for (int i = 0; i < BLOCK_HEIGHT; i++) {
			for (int j = 0; j < BLOCK_WIDTH; j++) {
				if (otherWholeBlockMap[i][j] != -1) {
					g.setColor(Color.black);
					g.drawRect(blockSize + j * blockSize + 185, i * blockSize + 110, blockSize, blockSize);
					g.setColor(COLOR_GROUP[otherWholeBlockMap[i][j]]);
					g.fillRect(blockSize + j * blockSize + 1 + 185, i * blockSize + 1 + 110, blockSize - 1, blockSize - 1);
				}
			}
		}
		
		g.setColor(Color.black);
		g.drawString("对手分数:" + otherScore, 190, 288);
		blockSize = temp;
	}
	
	public int[][] getWholeBlockMap(){
		return wholeBlockMap;
	}
	public int getScore() {
		return score;
	}
	public void changeOtherPlayserWholeBlockMap(int[][] blockMap, int oScore) {
		otherWholeBlockMap = blockMap;
		otherScore = oScore;
		setOtherFinished = true;
	}
	
	//判断正在下落的方块和墙
	private boolean isTouch(int[][] srcBlockMap, Point srcBlockPos) {
		for (int i = 0; i < srcBlockMap.length; i++) {
			for (int j = 0; j < srcBlockMap[i].length; j++) {
				if (srcBlockMap[i][j] != -1) {
					if(srcBlockPos.y + i >= BLOCK_HEIGHT // 碰到底端
							|| srcBlockPos.x + j < 0	// 碰到左边的墙
							|| srcBlockPos.x + j >= BLOCK_WIDTH) { //碰到右边的墙
						return true;
					}
					else {
						if (srcBlockPos.y + i < 0) //当前方块某些部分在顶端上方
							continue;
						else if (wholeBlockMap[srcBlockPos.y + i][srcBlockPos.x + j] != -1)
							return true;	//碰到之前的方块
					}
				}
			}
		}
		return false;
	}
	
	//固定方块到地图
	private boolean makeBlockToMap() {
		for (int i = 0; i < currentBlockMap.length; i++) {
			for (int j = 0; j < currentBlockMap[i].length; j++) {
				if (currentBlockMap[i][j] != -1) {
					if (currentBlockPosition.y + i < 0) 
						return false;
					else {
						wholeBlockMap[currentBlockPosition.y + i][currentBlockPosition.x + j] = currentBlockColor; 
					}
				}
			}
		}
		return true;
	}
	//清除形成一行的方块
	private int deleteBottomBlocks() {
		int lines = 0;
		for (int i = 0; i < wholeBlockMap.length; i++) {
			boolean isLine = true;
			for (int j = 0; j < wholeBlockMap[i].length; j++) {
				if (wholeBlockMap[i][j] == -1) {
					isLine = false;
					break;
				}
			}
			if (isLine) {
				for (int k = i; k > 0; k--)
					wholeBlockMap[k] = wholeBlockMap[k - 1];
				for (int k = 0; k < BLOCK_WIDTH; k++)
					wholeBlockMap[0][k] = -1;
				lines++;
			}
		}
		return lines;
	}
	//新的方块落下时的初始化
	private void getNextBlock() {
		// 将已经生成好的下一次方块赋给当前方块
		currentBlockState = nextBlockState;
		currentBlockMap = nextBlockMap;
		currentBlockColor = nextBlockColor;
		// 再次生成下一次方块
		nextBlockState =  (int)(Math.random() * 1000) % (shapeGroup.length * 4);
		nextBlockMap = getGameBlockMap(nextBlockState);
		nextBlockColor = (int)(Math.random() * 1000) % COLOR_GROUP.length;
		// 计算方块位置
		currentBlockPosition =  new Point(BLOCK_WIDTH / 2 - currentBlockMap[0].length, -currentBlockMap.length + 1);
	}
	
	
	ActionListener timerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (isTouch(currentBlockMap, new Point(currentBlockPosition.x, currentBlockPosition.y + 1))) {
				if (makeBlockToMap()) {
					int c = deleteBottomBlocks();
					score += c * 10;
					getNextBlock();
				}
				else {
					//游戏结束
					ifGameEndWithLose = true;
					JOptionPane.showMessageDialog(TetrisGamePanel.this.getParent(), "游戏结束，你输掉了比赛");
					initGameView();
					timer.stop();  
					repaint();
					return;
				}
			}
			else currentBlockPosition.y++;
			
			if (!TetrisGamePanel.this.isFocusable())
				TetrisGamePanel.this.setFocusable(true);
			if (!TetrisGamePanel.this.isFocusOwner())
				TetrisGamePanel.this.requestFocus();
			
			repaint();
		}
	};
	
	public boolean getGameState() {
		return ifGameEndWithLose;
	}
	KeyListener keyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (!ifPause) {
				Point desPoint;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					desPoint = new Point(currentBlockPosition.x, currentBlockPosition.y + 1);
					if (!isTouch(currentBlockMap, desPoint))
						currentBlockPosition = desPoint;
					break;
				case KeyEvent.VK_UP:
					int[][] tempBlock = changeBlockStyle(currentBlockMap, Math.PI / 2);
					if (!isTouch(tempBlock, currentBlockPosition)) {
						currentBlockMap = tempBlock;
						soundMove.play();
					}	
					break;
				case KeyEvent.VK_RIGHT:
					desPoint = new Point(currentBlockPosition.x + 1, currentBlockPosition.y);
					if (!isTouch(currentBlockMap, desPoint)) {
						currentBlockPosition = desPoint;
						soundMove.play();
					}
					break;
				case KeyEvent.VK_LEFT:
					desPoint = new Point(currentBlockPosition.x - 1, currentBlockPosition.y);
					if (!isTouch(currentBlockMap, desPoint)) {
						currentBlockPosition = desPoint;
						soundMove.play();
					}
					break;
				case KeyEvent.VK_SPACE:
					desPoint = new Point(currentBlockPosition.x, currentBlockPosition.y);
					while(true) {
						if (!isTouch(currentBlockMap, desPoint))
							desPoint.y += 1;
						else {
							desPoint.y -= 1;
							currentBlockPosition = desPoint;
							break;
						}
					}
					break;
				default:
					break;
				}
				repaint();
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		
	};
}
