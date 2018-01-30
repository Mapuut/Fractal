public class Renderer implements Runnable{

    private final int currentThreadNum;
    private final int totalThreads;

    public Renderer(int currentThreadNum, int totalThreads) {
        this.currentThreadNum = currentThreadNum;
        this.totalThreads = totalThreads;
    }


    @Override
    public void run() {

        if (Main.mandelbrot) {

            for (double y = (Main.height / totalThreads) * (currentThreadNum - 1); y < (Main.height / totalThreads) * currentThreadNum; y++) {
                for (double x = 0; x < Main.width; x++) {
                    double z_x = 0;
                    double z_y = 0;

                    double c_x = ((x / (Main.width - 1)) * Main.total_x) + Main.min_x;
                    double c_y = ((y / (Main.height - 1)) * Main.total_y) + Main.min_y;

                    double total = (c_x * c_x) + (c_y * c_y);

                    for (int j = 0; j < Main.iteration_nr; j++) {
                        if (total > 4) {

                            if (Main.colored) {
                                Main.pixels[(int)(y * Main.width + x)] = j * 100;
                            } else {
                                Main.pixels[(int)(y * Main.width + x)] = (((j << 8) + j) << 8) + j;
                            }
                            break;

                        }
                        double x_temporary = z_x;
                        z_x = (z_x * z_x) - (z_y * z_y) + c_x;
                        z_y = (2 * x_temporary * z_y) + c_y;
                        total = (z_x * z_x) + (z_y * z_y);
                    }
                }
            }

        } else {

            for (double y = (Main.height / totalThreads) * (currentThreadNum - 1); y < (Main.height / totalThreads) * currentThreadNum; y++) {
                for(double x = 0; x < Main.width; x++){

                    double z_x = ((x / (Main.width - 1)) * Main.total_x) + Main.min_x;
                    double z_y = ((y / (Main.height - 1)) * Main.total_y) + Main.min_y;

                    double c_x = Main.julia_c_x_value;
                    double c_y = 0.651;


                    double total = (c_x * c_x) + (c_y * c_y);

                    for(int j = 0; j < Main.iteration_nr; j++){
                        if (total > 4){

                            if (Main.colored) {
                                Main.pixels[(int)(y * Main.width + x)] = j * 50;
                            } else {
                                Main.pixels[(int)(y * Main.width + x)] = (((j << 8) + j) << 8) + j;
                            }
                            break;

                        }
                        double x_temporary = z_x;
                        z_x = (z_x * z_x) - (z_y * z_y) + c_x;
                        z_y = (2 * x_temporary * z_y) + c_y;
                        total = (z_x * z_x) + (z_y * z_y);

                    }
                }
            }
        }

        synchronized (Main.lock) {
            Main.renderStatus--;
            if (Main.renderStatus == 0) Main.lock.notify();
        }

    }
}
