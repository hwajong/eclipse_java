import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class StringShape {
	
	String str;
	int x;
	int y;
	
	public StringShape(String s, int x, int y) {
		str = s;
		this.x = x;
		this.y = y;
	}
	
	// 해당 모양을 g에 출력해주는 메소드
	public void draw(Graphics g) {
		g.setFont(new Font("TimesRoman", Font.BOLD, 20));
	    g.setColor(Color.black);
		g.drawString(str, x, y);
	}
	
	public void setString(String s) {
		str = s;
	}

}
