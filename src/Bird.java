import java.awt.Color;
import java.awt.Graphics;

public class Bird {
    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = -15;
    private static final int BIRD_SIZE = 30;
    private static final Color BIRD_COLOR = Color.YELLOW;

    private int x, y;
    private int velocity;

    public Bird(int x, int y) {
        this.x = x;
        this.y = y;
        velocity = 0;
    }

    public void jump() {
        velocity = JUMP_STRENGTH;
    }

    public void update() {
        velocity += GRAVITY;
        y += velocity;
    }

    public void paint(Graphics g) {
        g.setColor(BIRD_COLOR);
        g.fillRect(x, y, BIRD_SIZE, BIRD_SIZE);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return BIRD_SIZE;
    }
}




