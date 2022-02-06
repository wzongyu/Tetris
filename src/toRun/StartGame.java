package toRun;

import view.TetrisGameFrame;

public class StartGame 
{
	public static void main(String[] args) 
	{
		TetrisGameFrame tetrisFrame = new TetrisGameFrame("吴纵宇的俄罗斯方块");
		tetrisFrame.setVisible(true);
		tetrisFrame.setLocationRelativeTo(null);
	}

}
