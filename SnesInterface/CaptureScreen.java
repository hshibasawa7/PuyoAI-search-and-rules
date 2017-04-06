/******
表示画面 ==> 画像

********/
package SnesInterface;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import java.util.Timer;
import java.util.TimerTask;


public class CaptureScreen {
    private BufferedImage image;
    private Rectangle bounds;
    private JFrame frame = null;

    //display settings
    private int dw = 300;
    private int dh = 300;

    //capture settings
    private int cx = -1;
    private int cy = -1;
    private int cw = -1;
    private int ch = -1;

    //save file settings
    public String filename = "Capture";

    public static void main(String[] args) {
	int screenW = 512;
	int screenH = 478 + 22;//content and title bar

	CaptureScreen Screen = new CaptureScreen("puyo", screenW, screenH);

	RepeatTask task = new RepeatTask(Screen);
	Timer timer = new Timer();
	timer.schedule(task, 0, 5000);

    }

    //コンストラクタ
    public CaptureScreen() {}
    public CaptureScreen(int cw, int ch) {
	if(cw <= 0 || ch <= 0) {
	    System.out.println("invalid number( <= 0 )");
	    return;
	}
	this.cw = cw;
	this.ch = ch;
    }
    
    public CaptureScreen(int cx, int cy, int cw, int ch) {
	if(cx <= 0 || cy <= 0 || cw <= 0 || ch <= 0) {
	    System.out.println("invalid number( <= 0 )");
	    return;
	}
	this.cx = cx;
	this.cy = cy;
	this.cw = cw;
	this.ch = ch;
    }
    
    public CaptureScreen(String filename) {
	this.filename = filename;
    }
    public CaptureScreen(String filename, int cw, int ch) {
	if(cw <= 0 || ch <= 0) {
	    System.out.println("invalid number( <= 0 )");
	    return;
	}
	this.cw = cw;
	this.ch = ch;
	this.filename = filename;
    }
    
    public CaptureScreen(String filename, int cx, int cy, int cw, int ch) {
	if(cx <= 0 || cy <= 0 || cw <= 0 || ch <= 0) {
	    System.out.println("invalid number( <= 0 )");
	    return;
	}
	this.cx = cx;
	this.cy = cy;
	this.cw = cw;
	this.ch = ch;
	this.filename = filename;
    }

    //全画面,画面中央(W,H),全指定(x,y,W,H)
    public void getCaptureImage() {
	try {
	    if(cw > 0 || ch > 0) {
		if(cx > 0 || cy > 0) {
		    bounds = new Rectangle(cx, cy, cw, ch);
		} else {
		    bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();//作業領域
		    int x = ( bounds.width - cw ) / 2 + bounds.x;
		    int y = ( bounds.height - ch ) / 2 + bounds.y;
		    bounds = new Rectangle(x, y, cw, ch);
		}
	    } else {
		bounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	    }
	    image = new Robot().createScreenCapture(bounds);
	} catch(AWTException e) {
	    throw new RuntimeException(e);
	}
    }


    //画像ファイルへの書き出し（デフォルトのフォーマットはpng）
    //todo: ファイルオープンは外か中か検討すべし
    public void writeImage(File f) throws IOException {
	if(!ImageIO.write(image, "PNG", f)) {
	    throw new IOException("フォーマットが対象外");
	}
    }

    public void writeImage(File f, String format) throws IOException {
	if(!ImageIO.write(image, format, f)) {
	    throw new IOException("フォーマットが対象外");
	}
    }


    public void display() {
	BufferedImage thumb = new BufferedImage(dw, dh, image.getType());
	thumb.getGraphics().drawImage(image, 0, 0, dw, dh, null);

	if(frame == null) {
	    frame = new JFrame("captured image");
	    frame.setBounds(1100, 600, dw, dh+22);//todo: magic number
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	JLabel label = new JLabel(new ImageIcon(thumb));
	frame.getContentPane().removeAll();
	frame.getContentPane().add(label);

	frame.setVisible(true);
    }

    public BufferedImage getImage() {
	return image;
    }
    public BufferedImage getImage(int[] c) {
	if(c.length < 4) {
	    System.out.println("invalid array. (length < 4)");
	    return image.getSubimage(0,0,0,0);
	}
	return image.getSubimage(c[0], c[1], c[2], c[3]);
    }
    public BufferedImage getImage(int x, int y, int w, int h) {
	return image.getSubimage(x, y, w, h);
    }

}

class RepeatTask extends TimerTask {
    private CaptureScreen cs;
    private Times times;

    public RepeatTask(CaptureScreen cs) {
	this.cs = cs;
	times = new Times();
    }

    public void run() {
	times.tick();
	cs.getCaptureImage();
	cs.display();

	String filename = cs.filename + times.getCountString() + ".png";
	File f = new File("./", filename);
	try {
	    cs.writeImage(f);
	} catch(IOException e) {
	    throw new RuntimeException(e);
	}
    }
}

class Times {
    private int count = 0;
    
    public void tick() {
	count++;
    }
    public int getCount() {
	return count;
    }
    public String getCountString() {
	return String.format("%1$03d", count);//000-999
    }

}

