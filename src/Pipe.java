import java.awt.Color;
import java.awt.Graphics;

public class Pipe {
    private static final int PIPE_WIDTH = 50;
    private static final int PIPE_GAP = 200;
    private static final Color PIPE_COLOR = Color.GREEN;
    private static final int PIPE_SPEED = 3;

    private int x, height;
    private boolean passed;

    public Pipe(int x, int height) {
        this.x = x;
        this.height = height;
        passed = false;
    }

    public void update() {
        x -= PIPE_SPEED;
    }

    public void paint(Graphics g) {
        g.setColor(PIPE_COLOR);
        g.fillRect(x, 0, PIPE_WIDTH, height);
        g.fillRect(x, height + PIPE_GAP, PIPE_WIDTH, 600 - (height + PIPE_GAP));
    }

    public boolean isOffScreen() {
        return x + PIPE_WIDTH < 0;
    }

    public boolean collidesWith(Bird bird) {
        if (bird.getX() + bird.getSize() > x && bird.getX() < x + PIPE_WIDTH) {
            if (bird.getY() < height || bird.getY() + bird.getSize() > height + PIPE_GAP) {
                return true;
            }
        }
        return false;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}






