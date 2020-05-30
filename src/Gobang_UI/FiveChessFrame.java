package Gobang_UI;

import jdk.jfr.Category;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FiveChessFrame extends JFrame implements MouseListener, Runnable {
    //获取屏幕的宽、高
    int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    //背景图片
    BufferedImage bgImage = null;

    //保存棋子坐标
    int x = 0;
    int y = 0;
    //保存之前下过的全部棋子的坐标
    //其中数据内容为0，表示没有棋子；1为黑子；2为白子
    int[][] allChess = new int[19][19];
    //标识当前是黑棋还是白棋下一步
    boolean isBlack = true;
    //标识当前游戏是否可以继续
    boolean canPlay = true;
    //保存显示的提示信息
    String message = "黑方先行";
    //保存最多拥有多少时间(秒)
    int maxTime = 0;
    //做倒计时的线程类
    Thread t = new Thread(this);
    //保存黑方与白方的剩余时间
    int blackTime = 0;
    int whiteTime = 0;
    //保存双方剩余时间的显示信息
    String blackMessage = "无限制";
    String whiteMessage = "无限制";


    public FiveChessFrame() {
        this.setTitle("五子棋游戏");
        this.setSize(500, 500);
        this.setLocation((width - 500) / 2, (height - 500) / 2);
        //将窗口设置为大小不可改变
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //为窗体加入监听器
        this.addMouseListener(this);
        this.setVisible(true);
        //线程开始并挂起
        t.start();
        t.suspend();
        //刷新屏幕，防止开始游戏时，出现无法显示的情况
        //this.repaint();


        try {
            bgImage = ImageIO.read(new File("src/image/back.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        //双缓冲技术放防止屏幕闪烁
        BufferedImage bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = bi.createGraphics();
        //绘制背景
        g2.drawImage(bgImage, 0, 0, this);
        g2.setFont(new Font("黑体", Font.BOLD, 20));
        g2.drawString("游戏信息" + message, 120, 60);
        g2.setFont(new Font("宋体", 0, 14));
        g2.drawString("黑方时间:" + blackMessage, 30, 460);
        g2.drawString("白方时间: " + whiteMessage, 260, 460);

        //绘制棋盘:计算线间的间距：19*19的棋盘
        // 360/18=20
        for (int i = 0; i < 19; i++) {
            g2.drawLine(10, 70 + 20 * i, 370, 70 + 20 * i);
            g2.drawLine(10 + 20 * i, 70, 10 + 20 * i, 430);
        }

        //标注9个点位
        g2.fillOval(68, 128, 4, 4);
        g2.fillOval(308, 128, 4, 4);
        g2.fillOval(308, 368, 4, 4);
        g2.fillOval(68, 368, 4, 4);
        g2.fillOval(308, 248, 4, 4);
        g2.fillOval(188, 128, 4, 4);
        g2.fillOval(68, 248, 4, 4);
        g2.fillOval(188, 368, 4, 4);
        g2.fillOval(188, 248, 4, 4);

  /*      //绘制棋子
        x = (x - 10) / 20 * 20 + 10;
        y = (y - 70) / 20 * 20 + 70;
        //黑子
        g.fillOval(x,y,10,10);
        //白子
        g.setColor(Color.white);
        g.fillOval(x - 7, y - 7,14,14);
        g.setColor(Color.BLACK);
        g.drawOval(x - 7, y - 7,14, 14);*/

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                if (allChess[i][j] == 1) {
                    //黑子
                    int tempx = i * 20 + 10;
                    int tempy = j * 20 + 70;
                    g2.fillOval(tempx - 7, tempy - 7, 14, 14);
                }
                if (allChess[i][j] == 2) {
                    //白子
                    int tempx = i * 20 + 10;
                    int tempy = j * 20 + 70;
                    g2.setColor(Color.white);
                    g2.fillOval(tempx - 7, tempy - 7, 14, 14);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(tempx - 7, tempy - 7, 14, 14);
                }
            }
        }
        g.drawImage(bi, 0, 0, this);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        //System.out.println("X:" + mouseEvent.getX());
        //System.out.println("Y:" + mouseEvent.getY());
        if (canPlay == true) {
            x = mouseEvent.getX();
            y = mouseEvent.getY();
            if (x >= 10 && x <= 370 && y >= 70 && y <= 430) {
                x = (x - 10) / 20;
                y = (y - 70) / 20;
                if (allChess[x][y] == 0) {
                    //判断当前要下的是什么颜色的棋子
                    if (isBlack == true) {
                        allChess[x][y] = 1;
                        isBlack = false;
                        message = "轮到白方";
                    } else {
                        allChess[x][y] = 2;
                        isBlack = true;
                        message = "轮到黑方";
                    }
                    //判断当前棋子是否和其他棋子5连
                    boolean winFlag = this.checkWin();
                    if (winFlag == true) {
                        JOptionPane.showMessageDialog(this, "游戏结束" +
                                (allChess[x][y] == 1 ? "黑方" : "白方") + "获胜");
                        canPlay = false;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "当前位置已经有棋子，请重新落子");
                }
                this.repaint(); //表示重新执行一次paint()方法
            }
        }

            //System.out.println(mouseEvent.getX()+"--"+mouseEvent.getY());
            //进入开始游戏 按钮
            if (mouseEvent.getX() >= 400 && mouseEvent.getX() <= 470 && mouseEvent.getY() >= 70 && mouseEvent.getY() <= 100) {
                int result = JOptionPane.showConfirmDialog(this, "是否重建开始游戏");
                if (result == 0) {
                    //重新开始 1）清空棋盘 allChese全部归0  2）将游戏信息的显示改回到开始位置
                    // 3）将下一步下棋的人改为黑方
                    for (int i = 0; i < 19; i++) {
                        for (int j = 0; j < 19; j++) {
                            allChess[i][j] = 0;
                        }
                    }
                    message = "黑方先行";
                    isBlack = true;
                    blackTime = maxTime;
                    whiteTime = maxTime;
                    if (maxTime > 0) {
                        blackMessage = maxTime / 3600 + ":" +
                                (maxTime / 60 - maxTime / 3600 * 60) + ":" +
                                (maxTime - maxTime / 60 * 60);
                        whiteMessage = maxTime / 3600 + ":" +
                                (maxTime / 60 - maxTime / 3600 * 60) + ":" +
                                (maxTime - maxTime / 60 * 60);
                        t.resume();//重新启动线程
                    } else {
                        blackMessage = "无限制";
                        whiteMessage = "无限制";
                    }
                    this.repaint(); //没有这一步，不会自动清空，需要下一步棋才会清空
                }
            }
            //进入游戏设置 按钮
            if (mouseEvent.getX() >= 400 && mouseEvent.getX() <= 470 && mouseEvent.getY() >= 120 && mouseEvent.getY() <= 150) {
                String input = JOptionPane.showInputDialog("请输入游戏的最大时间（分钟）：(如果输入0，则没有时间限制)");
                //添加try-catch:Ctrl+Alt+t
                try {
                    maxTime = Integer.parseInt(input) * 60;//强制类型转换，String->int
                    if (maxTime < 0) {
                        JOptionPane.showMessageDialog(this, "请输入争取信息，不允许输入负值");
                    }
                    if (maxTime == 0) {
                        int result = JOptionPane.showConfirmDialog(this, "设置完成,是否重新开始游戏");
                        if (result == 0) {
                            //重新开始 1）清空棋盘 allChese全部归0  2）将游戏信息的显示改回到开始位置
                            // 3）将下一步下棋的人改为黑方
                            for (int i = 0; i < 19; i++) {
                                for (int j = 0; j < 19; j++) {
                                    allChess[i][j] = 0;
                                }
                            }
                            message = "黑方先行";
                            isBlack = true;
                            blackTime = maxTime;
                            whiteTime = maxTime;
                            blackMessage = "无限制";
                            whiteMessage = "无限制";
                            this.repaint(); //没有这一步，不会自动清空，需要下一步棋才会清空
                        }
                    }
                    if (maxTime > 0) {
                        int result = JOptionPane.showConfirmDialog(this, "设置完成,是否重新开始游戏");
                        if (result == 0) {
                            //重新开始 1）清空棋盘 allChese全部归0  2）将游戏信息的显示改回到开始位置
                            // 3）将下一步下棋的人改为黑方
                            for (int i = 0; i < 19; i++) {
                                for (int j = 0; j < 19; j++) {
                                    allChess[i][j] = 0;
                                }
                            }
                            message = "黑方先行";
                            isBlack = true;
                            blackTime = maxTime;
                            whiteTime = maxTime;
                            blackMessage = maxTime / 3600 + ":" +
                                    (maxTime / 60 - maxTime / 3600 * 60) + ":" +
                                    (maxTime - maxTime / 60 * 60);
                            whiteMessage = maxTime / 3600 + ":" +
                                    (maxTime / 60 - maxTime / 3600 * 60) + ":" +
                                    (maxTime - maxTime / 60 * 60);
                            t.resume();//重新启动线程
                            this.repaint(); //没有这一步，不会自动清空，需要下一步棋才会清空
                        }
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(this, "请正确输入信息");
                }
            }
            //进入游戏说明 按钮
            if (mouseEvent.getX() >= 400 && mouseEvent.getX() <= 470 && mouseEvent.getY() >= 170 && mouseEvent.getY() <= 200) {
                JOptionPane.showMessageDialog(this, "游戏说明");
            }
            //进入认输 按钮
            if (mouseEvent.getX() >= 400 && mouseEvent.getX() <= 470 && mouseEvent.getY() >= 270 && mouseEvent.getY() <= 300) {
                JOptionPane.showMessageDialog(this, "认输");
            }
            //进入关于 按钮
            if (mouseEvent.getX() >= 400 && mouseEvent.getX() <= 470 && mouseEvent.getY() >= 320 && mouseEvent.getY() <= 330) {
                int result = JOptionPane.showConfirmDialog(this, "是否确认认输");
                if (result == 0) {
                    if (isBlack) {
                        JOptionPane.showMessageDialog(this, "黑方已经认输，游戏结束");
                    } else {
                        JOptionPane.showMessageDialog(this, "白方已经认输，游戏结束");
                    }
                    canPlay = false;
                }
            }
            //进入退出 按钮
            if (mouseEvent.getX() >= 400 && mouseEvent.getX() <= 470 && mouseEvent.getY() >= 370 && mouseEvent.getY() <= 400) {
                JOptionPane.showMessageDialog(this, "退出");
                System.exit(0);
            }

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    private boolean checkWin() {
        boolean flag = false;


        //判断横向是否5连,特点：y坐标相同，即allChese[x][y]中y值相同
        int color = allChess[x][y];
     /*
       //保存共有相同颜色多少棋子相连
        int count = 1;
        if (color == allChess[x+1][y]){
            count++;
            if (color == allChess[x+2 ][y]){
                count++;
            }
        }*/
        //通过循环来做棋子相连判断
        /*int i = 1;
        while(color == allChess[x+i][y]){
            count++;
            i++;
        }
        i = 1;
        while (color == allChess[x-1][y]){
            count++;
            i++;
        }
        if (count >= 5){
            flag = true;
        }
        //纵向判断
        int i2 = 1;
        int count2 = 1;
        while(color == allChess[x][y+i2]){
            count2++;
            i2++;
        }
        i2 = 1;
        while (color == allChess[x][y-i2]){
            count2++;
            i2++;
        }
        if (count2 >= 5){
            flag = true;
        }

        //斜方向的判断（右上+左下）
        int i3 = 1;
        int count3 = 1;
        while(color == allChess[x+i3][y-i3]){
            count3++;
            i3++;
        }
        i3 = 1;
        while (color == allChess[x-i3][y+i3]){
            count3++;
            i3++;
        }
        if (count3 >= 5){
            flag = true;
        }
        //斜方向的判断（左上+右下）
        int i4 = 1;
        int count4 = 1;
        while(color == allChess[x+i4][y+i4]){
            count4++;
            i4++;
        }
        i4 = 1;
        while (color == allChess[x-i4][y-i4]){
            count4++;
            i4++;
        }
        if (count4 >= 5){
            flag = true;
        }*/
        //横向
        int count = this.checkCount(1, 0, color);
        if (count >= 5) {
            flag = true;
        } else {
            //纵向
            count = this.checkCount(0, 1, color);
            if (count >= 5) {
                flag = true;
            } else {
                //右上+左下
                count = this.checkCount(1, -1, color);
                if (count >= 5) {
                    flag = true;
                } else {
                    //右下+左上
                    count = this.checkCount(1, 1, color);
                    if (count >= 5) {
                        flag = true;
                    }
                }
            }
        }

        return flag;

    }

    //判断棋子连接的数量
    private int checkCount(int xChange, int yChange, int color) {
        int count = 1;
        int tempX = xChange;
        int tempY = yChange;
        while (x + xChange >= 0 && x + xChange <= 18 &&
                y + yChange >= 0 && y + yChange <= 18 &&
                color == allChess[x + xChange][y + yChange]) {
            count++;
            if (xChange != 0)
                xChange++;
            if (yChange != 0) {
                if (yChange > 0)
                    yChange++;
                else {
                    yChange--;
                }
            }
        }
        xChange = tempX;
        yChange = tempY;
        while (x - xChange >= 0 && x - xChange <= 18 &&
                y - yChange >= 0 && y - yChange <= 18 &&
                color == allChess[x - xChange][y - yChange]) {
            count++;
            if (xChange != 0)
                xChange++;
            if (yChange != 0){
                if (yChange > 0)
                    yChange++;
                else {
                    yChange--;
                }
            }
        }


        return count;
    }

    @Override
    public void run() {
        //判断是否有时间限制
        if (maxTime > 0) {
            while (true) {
                if (isBlack) {
                    blackTime--;
                    if (blackTime == 0) {
                        JOptionPane.showMessageDialog(this, "黑方超时，游戏结束");
                    }
                } else {
                    whiteTime--;
                    if (whiteTime == 0) {
                        JOptionPane.showMessageDialog(this, "白方超时，游戏结束");
                    }
                }
                blackMessage = blackTime / 3600 + ":" +
                        (blackTime / 60 - blackTime / 3600 * 60) + ":" +
                        (blackTime - blackTime / 60 * 60);
                whiteMessage = whiteTime / 3600 + ":" +
                        (whiteTime / 60 - whiteTime / 3600 * 60) + ":" +
                        (whiteTime - whiteTime / 60 * 60);
                this.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(blackTime + "--" + whiteTime);
            }
        }

    }

    /*public static void main(String[] args) {
        var gb = new Gobang_UI.FiveChessFrame();
    }
*/
}
