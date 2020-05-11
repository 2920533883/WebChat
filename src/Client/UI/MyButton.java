package Client.UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;


public class MyButton extends JButton{
    private Shape shape = null;

    private Color click = new Color(250, 210, 170);// 按下时的颜色

    private Color quit = new Color(237, 234, 228);// 离开时颜色

    public MyButton(String s) {
        super(s);
        setContentAreaFilled(false);// 是否显示外围矩形区域 选否
    }


    public void paintComponent(Graphics g) {
        //如果按下则为ＣＬＩＣＫ色 否则为 ＱＵＩＴ色
        if (getModel().isArmed()) {
            g.setColor(click);
        } else {
            g.setColor(quit);
        }
        //填充圆角矩形区域 也可以为其它的图形
        g.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                        20, 20);
        //必须放在最后 否则画不出来
        super.paintComponent(g);
    }

    public void paintBorder(Graphics g) {
        //画边界区域
        g.setColor(Color.WHITE);
        g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                        20, 20);
    }

    public boolean contains(int x, int y) {
        //判断点（x,y）是否在按钮内
        if (shape == null || !(shape.getBounds().equals(getBounds()))) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    20, 20);
        }
        return shape.contains(x, y);
    }
}
