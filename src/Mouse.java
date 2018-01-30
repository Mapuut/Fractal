import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener{
    private static int mouseX = -1;
    private static int mouseY = -1;
    private static int mouseB = -1;

    public static int getX() {
        return mouseX;
    }

    public static int getY() {
        return mouseY;
    }

    public static int getButton() {
        return mouseB;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseB = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseB = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static double min_x = -2;
    public static double min_y = -2;

    public static double max_x = 2;
    public static double max_y = 2;
    
    public static double total_x = max_x - min_x;
    public static double total_y = max_y - min_y;
    
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotation = e.getWheelRotation();
		total_x = max_x - min_x;
	    total_y = max_y - min_y;
		
		if (rotation < 0){
			min_x -= total_x / 10;
			min_y -= total_y / 10;
			max_x += total_x / 10;
			max_y += total_y / 10;
		}else{
			min_x += total_x / 10;
			min_y += total_y / 10;
			max_x -= total_x / 10;
			max_y -= total_y / 10;
		}
		
	}
}

