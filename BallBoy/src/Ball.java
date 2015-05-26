import java.net.URL;


@SuppressWarnings("serial")
public class Ball extends Shape {
	
	int steps_x;
	int steps_y = 30;
	
	public Ball(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		super(imgURL, margin, steps, xBoundary, yBoundary);
		
		steps_x = (int)(Math.random() * 3);
		if(steps_x == 0) steps_x = 1;
		
		steps_y = (int)(Math.random() * 30);
		if(steps_y < 5) steps_y = 5;
		if(steps_y >= steps) steps_y = steps;

	}
	
	public void move() {
		if (xDirection > 0 && x >= xBoundary) {
			xDirection = -1;
		}
		if (xDirection < 0 && x <= 0) {
			xDirection = 1;
		}
		
		x += (xDirection * steps_x);

		if (yDirection > 0 && y >= yBoundary) {
			yDirection = -1;
		}
		if (yDirection < 0 && y <= 200) {
			yDirection = 1;
		}

		// -- sin 곡선 적용 
		double radian = 0;
		double sinVal = 0;
		double factor = 0;
		double delta = 0;
		int yBound = yBoundary - 200;
		if(yDirection == -1) { // 올라갈때 가속도 적용 
//			factor = (yBound - y*1.0) / yBound;
//			radian = (3.14/2.0) + (3.14 / 2.0) * factor;
//			sinVal = Math.sin(radian);;
//			delta = yDirection * steps_y * sinVal; 
//			if(delta == 0) delta = -1;
//			y += delta;
			
			factor = (y*1.0 - 190) / yBound;
			radian = (3.14 / 2.0) * factor;
			sinVal = Math.sin(radian);
			delta = yDirection * steps_y * sinVal; 
			y += delta;			
		}
		else { // 떨어질때 가속도 적용
			factor = (y*1.0 - 190) / yBound;
			radian = (3.14 / 2.0) * factor;
			sinVal = Math.sin(radian);
			delta = yDirection * steps_y * sinVal; 
			if(delta < 0) delta = 0.1;
			y += delta;
		}
		
		//System.out.println("direction : " + yDirection + ", factor : " + factor);
		//System.out.println("y : " + y + ", yBound : " + yBound + ", rad : " + radian + ", sin : " + sinVal + ", delta : " + delta);

		
		/*
		// -- 지수함수 적용 
		double factor = 0;
		double delta = 0;
		int yBound = yBoundary + 5;
		if(yDirection == -1) { // 올라갈때 가속도 적용 
			factor = Math.pow(y*1.0 / yBound, 2);
			delta = yDirection * steps_y * factor; 
			delta *= 1.5;
			if(delta > -0.5) delta = -0.5;
			y += delta;
		}
		else { // 떨어질때 가속도 적용
			factor = Math.pow(y*1.0 / yBound, 2);
			delta = yDirection * steps_y * factor; 
			delta *= 1.5;
			if(delta < 0.5) delta = 0.5;
			y += delta;
		}
		*/

	}	
}
