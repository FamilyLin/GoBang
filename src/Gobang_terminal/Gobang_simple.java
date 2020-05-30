package Gobang_terminal;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Gobang_simple {
    //定义棋盘、黑棋、白棋
    BufferedImage table;
    BufferedImage black; 
    BufferedImage white;
    //定义选择框
    BufferedImage selected;
    //定义棋盘大小
    private final int BOARD_SIZE = 15;
    //定义棋盘宽、高像素
    private final int TABLE_WIDTH = 535;
    private final int TABLE_HETGHT = 536;
    //定义棋盘坐标的像素值和棋盘数组之间的比例
    private final int RATE = TABLE_WIDTH / BOARD_SIZE;
    //定义棋盘坐标的像素值和棋盘数组之间的偏移距离
    private final int X_OFFSET = 5;
    private final int Y_OFFSET = 6;
    //定义一个二维数组充当棋盘
    private String[][] board = new String[BOARD_SIZE][BOARD_SIZE];
    //游戏窗口
    JFrame f = new JFrame("五子棋游戏");
    //棋盘的Canvas组件
    ChessBoard chessBoard = new ChessBoard();
    //当前选中点的坐标
    private int selectedX = -1;
    private int selectedY = -1;
    public void init() throws IOException {
        table = ImageIO.read(new File("src/image/board.jpg"));
        black = ImageIO.read(new File("src/image/black.gif"));
        white = ImageIO.read(new File("src/image/white.gif"));
        selected = ImageIO.read(new File("src/image/selected.gif"));
        //将所有元素都赋值为"╋"，"╋"代表没有棋子
        for(var i = 0; i < BOARD_SIZE; i++){
            for(var j = 0; j < BOARD_SIZE; j++){
                board[i][j] = "╋";
            }
        }
        chessBoard.setPreferredSize(new Dimension(TABLE_WIDTH,TABLE_HETGHT));
        chessBoard.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                var xPos = (int)((e.getX() - X_OFFSET) / RATE);
                var yPos = (int)((e.getY() - Y_OFFSET) / RATE);
                board[xPos][yPos] = "●";
                //添加白棋自动下子
                var cxPos = (int)((Math.random()*BOARD_SIZE-X_OFFSET) / RATE);
                var cyPos = (int)((Math.random()*BOARD_SIZE-Y_OFFSET) / RATE);
                board[cxPos][cyPos] = "○";

                chessBoard.repaint();
            }
            public void mouseExited(MouseEvent e){
                selectedX = -1;
                selectedY = -1;
                chessBoard.repaint();
            }
        });
        chessBoard.addMouseMotionListener(new MouseMotionAdapter(){
            //当鼠标移动时，改变选中点的坐标
            public void mouseMoved(MouseEvent e){
                selectedX = (e.getX() - X_OFFSET) / RATE;
                selectedY = (e.getY() - Y_OFFSET) / RATE;
                chessBoard.repaint();
            }
        });
        f.add(chessBoard);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) throws IOException {
        var gb = new Gobang_simple();
        gb.init();
    }
    class ChessBoard extends JPanel{
        public void paint(Graphics g){
            //绘制五子棋棋盘
            g.drawImage(table, 0, 0, null);
            if(selectedX >= 0 && selectedY >= 0)
                g.drawImage(selected, selectedX * RATE + X_OFFSET, selectedY * RATE + Y_OFFSET, null);
            //遍历数组，绘制棋子
            for(var i = 0; i < BOARD_SIZE; i++){
                for(var j = 0; j < BOARD_SIZE; j++){
                    //绘制黑棋
                    if(board[i][j].equals("●")){
                        g.drawImage(black, i * RATE + X_OFFSET, j * RATE + Y_OFFSET, null);
                    }
                    //绘制白棋
                    if(board[i][j].equals("○")){
                        g.drawImage(white, i * RATE + X_OFFSET, j * RATE + Y_OFFSET, null);
                    }
                }
            }
        }
    }

}
