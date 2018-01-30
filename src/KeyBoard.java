import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoard implements KeyListener {

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        	Main.mandelbrot = !Main.mandelbrot;
        }
        else if (e.getKeyCode() == KeyEvent.VK_Q) {
            Main.colored = !Main.colored;
        }
        else if (e.getKeyCode() == KeyEvent.VK_W) {
        	Main.smooth = !Main.smooth;
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

