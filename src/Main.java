import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

public class Main {
	
    public static final int width = 1300;
    public static final int height = 1300;
    
    private static Canvas canvas = new Canvas();
    private static JFrame frame = new JFrame();
    
    private static KeyBoard keyboard = new KeyBoard();
    private static Mouse mouse = new Mouse();
    
    private static BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    public static int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    private static final int numOfThreads = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);

    public static final Object lock = new Object();
    public static int renderStatus = numOfThreads;
    private static Renderer[] tasks = new Renderer[numOfThreads];

    static {
        for (int i = 0; i < numOfThreads; i++) {
            tasks[i] = new Renderer(i + 1, numOfThreads);
        }
    }

    // rendered image corner coordinates
    public static double min_x = -2;
    public static double min_y = -2;
    public static double max_x = 2;
    public static double max_y = 2;
    //

    // total with anf height of rendered image
    public static double total_x = max_x - min_x;
    public static double total_y = max_y - min_y;
    //

    private static int mouse_x = 0;
    private static int mouse_y = 0;
    private static boolean mouse_pressed = false;

    public static boolean mandelbrot = true;
    public static int iteration_nr = 255;
    public static boolean smooth = false;
    public static boolean colored = true;

    public static double julia_c_x_value = -0.1;
    public static double julia_c_x_value_adder = 0.001;




	public static void main(String[] args) {
		run();

	}
	
	public static void run(){
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setIgnoreRepaint(true);
		canvas.addKeyListener(keyboard);
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);
		canvas.addMouseWheelListener(mouse);
		
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);
		frame.add(canvas);
		frame.pack();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setAutoRequestFocus(true);
		frame.setVisible(true);

		while(true){
			update();

            clear(0xffffff);
			render();
		}
		
	}
	
	public static void smooth_pixels(){
		int[] pixels_smooth = new int[pixels.length];
		for(int i = 0; i < pixels.length; i++){
			pixels_smooth[i] = pixels[i];
		}
		
		for(int y = 1; y < height - 1; y++){
			for(int x = 1; x < width - 1; x++){
				int avarage = (pixels_smooth[(y - 1) * width + x - 1] & 0xff) + (pixels_smooth[(y - 1) * width + x] & 0xff)
                        + (pixels_smooth[(y - 1) * width + x + 1] & 0xff) + (pixels_smooth[(y) * width + x - 1] & 0xff)
                        + (pixels_smooth[(y) * width + x + 1] & 0xff) + (pixels_smooth[(y + 1) * width + x - 1] & 0xff)
                        + (pixels_smooth[(y + 1) * width + x] & 0xff) + (pixels_smooth[(y + 1) * width + x + 1] & 0xff);

				avarage = avarage >> 3;
				pixels[(y * width) + x] = (((avarage << 8) + avarage) << 8) + avarage;
			}
		}
	}

	
	public static void clear(int color){
		for(int i = 0; i < width * height; i ++){
			pixels[i] = color;
		}
	}


	
	public static void update(){
		
	    min_x += (Mouse.min_x - min_x) / 10;
	    min_y += (Mouse.min_y - min_y) / 10;

        max_x += (Mouse.max_x - max_x) / 10;
        max_y += (Mouse.max_y - max_y) / 10;
	    
	    total_x = max_x - min_x;
	    total_y = max_y - min_y;

        iteration_nr = (int)(200 / Math.pow(total_x, 0.05));

	    if (Mouse.getButton() == 1){
	    	mouse_pressed = true;
	    }
	    
	    
	    if(mouse_pressed){
	    	double shift_x = ((double)(mouse_x - Mouse.getX()) / (width - 1)) * total_x;
	    	double shift_y = ((double)(mouse_y - Mouse.getY()) / (height - 1)) * total_y;
	    	min_x += shift_x;
	    	max_x += shift_x;
	    	
	    	min_y += shift_y;
	    	max_y += shift_y;
	    	
	    	Mouse.min_x =  min_x;
		    Mouse.min_y = min_y;

		    Mouse.max_x = max_x;
		    Mouse.max_y = max_y;

		    Mouse.total_x = max_x - min_x;
		    Mouse.total_y = max_y - min_y;
		    
		    total_x = max_x - min_x;
		    total_y = max_y - min_y;
		    
		    if(Mouse.getButton() == -1){
		    	mouse_pressed = false;
		    }
	    }

	    mouse_x = Mouse.getX();
	    mouse_y = Mouse.getY();

        Main.julia_c_x_value += Main.julia_c_x_value_adder;
        if (Main.julia_c_x_value > 0.7 || Main.julia_c_x_value < -0.7){
            Main.julia_c_x_value_adder = -Main.julia_c_x_value_adder;
        }
	    
	}
	
	public static void render() {

        for (Renderer task : tasks) {executor.execute(task);}

        synchronized (lock) {
            if (renderStatus != 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            renderStatus = numOfThreads;
        }

        if (smooth){
            smooth_pixels();
        }

        BufferStrategy bs;
        Graphics g;

		bs = canvas.getBufferStrategy();
        if (bs == null) {
        	canvas.createBufferStrategy(2);
        	bs = canvas.getBufferStrategy();
        }
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        
        g.dispose();
        bs.show();
	}
}
