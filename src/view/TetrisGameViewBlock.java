package view;

public class TetrisGameViewBlock {
	static final int[][][] SHAPE = 
		{ 
			{ { -1, -1, -1, -1 }, { 1, 1, 1, 1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 } },
			{ { 1, -1, -1 }, { 1, 1, 1 }, { -1, -1, -1 } }, { { -1, -1, 1 }, { 1, 1, 1 }, { -1, -1, -1 } },
			{ { 1, 1 }, { 1, 1 } }, { { -1, 1, 1 }, { 1, 1, -1 }, { -1, -1, -1 } },
			{ { -1, 1, -1 }, { 1, 1, 1 }, { -1, -1, -1 } }, { { 1, 1, -1 }, { -1, 1, 1 }, { -1, -1, -1 } } 
		};
}
