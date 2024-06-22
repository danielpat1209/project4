import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

///participants: Daniel Patachov, Khaled,Amjad

public class FlappyBird extends JPanel implements KeyListener, Runnable {
    private static final int WIDTH = 800, HEIGHT = 600;
    private static final int BIRD_SIZE = 20;
    private Bird bird;
    private ArrayList<Pipe> pipes;
    private boolean running, gameOver, gameStarted;
    private int score;
    private Thread gameThread, pipeSpawnerThread;
    private Image offScreenImage; // Double buffering

    public FlappyBird() {
        bird = new Bird(WIDTH / 4, HEIGHT / 2);
        pipes = new ArrayList<>();
        running = false;
        gameOver = false;
        gameStarted = false;
        score = 0;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);

        JButton instructionsButton = new JButton("Instructions");
        instructionsButton.setBounds(WIDTH / 2 - 75, HEIGHT / 2 + 50, 150, 50);
        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Instructions:\n\n" +
                                "1. Press Enter to start the game.\n" +
                                "2. Use Enter to make the bird jump.\n" +
                                "3. Avoid the pipes and don't let the bird fall.\n" +
                                "4. Score points by passing through the pipes.\n",
                        "Instructions",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setLayout(null);  // Set the layout to null to place components manually
        frame.add(instructionsButton);

        frame.setVisible(true);
    }

    private void startGame() {
        gameStarted = true;
        gameOver = false;
        running = true;
        score = 0;
        bird = new Bird(WIDTH / 4, HEIGHT / 2);
        pipes.clear();

        gameThread = new Thread(this);
        pipeSpawnerThread = new Thread(new PipeSpawner());
        gameThread.start();
        pipeSpawnerThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (offScreenImage == null) {
            offScreenImage = createImage(WIDTH, HEIGHT);
        }
        Graphics offScreenGraphics = offScreenImage.getGraphics();

        // Draw background
        offScreenGraphics.setColor(Color.cyan);
        offScreenGraphics.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw bird
        bird.paint(offScreenGraphics);

        // Draw pipes
        for (Pipe pipe : pipes) {
            pipe.paint(offScreenGraphics);
        }

        // Draw score
        offScreenGraphics.setColor(Color.white);
        offScreenGraphics.setFont(new Font("Arial", Font.BOLD, 20));
        offScreenGraphics.drawString("Score: " + score, 10, 20);

        // Draw game over screen
        if (!gameStarted) {
            offScreenGraphics.setColor(Color.black);
            offScreenGraphics.setFont(new Font("Arial", Font.BOLD, 50));
            offScreenGraphics.drawString("Flappy Bird", WIDTH / 2 - 150, HEIGHT / 2 - 200);

            offScreenGraphics.setFont(new Font("Arial", Font.BOLD, 30));
            offScreenGraphics.drawString("Press Enter to Start", WIDTH / 2 - 150, HEIGHT / 2);
        }

        if (gameOver) {
            offScreenGraphics.setColor(Color.red);
            offScreenGraphics.setFont(new Font("Arial", Font.BOLD, 50));
            offScreenGraphics.drawString("Game Over", WIDTH / 2 - 150, HEIGHT / 2);
            offScreenGraphics.setFont(new Font("Arial", Font.BOLD, 30));
            offScreenGraphics.drawString("Press Enter to Restart", WIDTH / 2 - 150, HEIGHT / 2 + 50);
        }

        // Draw the off-screen image onto the screen
        g.drawImage(offScreenImage, 0, 0, null);
    }

    public void update() {
        bird.update();

        for (Pipe pipe : pipes) {
            pipe.update();
            if (pipe.collidesWith(bird) || bird.getY() >= HEIGHT || bird.getY() <= 0) {
                gameOver = true;
                running = false;
                stopThreads();
            }
            if (!pipe.isPassed() && pipe.getX() + pipe.getWidth() < bird.getX()) {
                pipe.setPassed(true);
                score++;
            }
        }

        pipes.removeIf(pipe -> pipe.isOffScreen());

        repaint();
    }

    private void stopThreads() {
        if (gameThread != null) {
            gameThread.interrupt();
        }
        if (pipeSpawnerThread != null) {
            pipeSpawnerThread.interrupt();
        }
    }

    @Override
    public void run() {
        while (running) {
            update();
            try {
                Thread.sleep(20); // Increase the game speed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!gameStarted || gameOver) {
                startGame();
            } else {
                bird.jump();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private void resetGame() {
        bird = new Bird(WIDTH / 4, HEIGHT / 2);
        pipes.clear();
        gameOver = false;
        running = true;
        score = 0;

        gameThread = new Thread(this);
        pipeSpawnerThread = new Thread(new PipeSpawner());
        gameThread.start();
        pipeSpawnerThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FlappyBird();
            }
        });
    }

    private class Pipe {
        private int x, y, width, height;
        private boolean passed;

        public Pipe(int x, int y) {
            this.x = x;
            this.y = y;
            this.width = 60;
            this.height = 400;
            this.passed = false;
        }

        public void paint(Graphics g) {
            g.setColor(Color.green.darker());
            g.fillRect(x, 0, width, y);
            g.fillRect(x, y + 150, width, HEIGHT - (y + 150));
        }

        public void update() {
            x -= 5; // Increase the pipe movement speed
        }

        public boolean collidesWith(Bird bird) {
            if (bird.getX() + BIRD_SIZE > x && bird.getX() < x + width) {
                if (bird.getY() < y || bird.getY() + BIRD_SIZE > y + 150) {
                    return true;
                }
            }
            return false;
        }

        public boolean isOffScreen() {
            return x + width < 0;
        }

        public int getX() {
            return x;
        }

        public int getWidth() {
            return width;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }
    }

    private class PipeSpawner implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(2000); // Increase the time between pipes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                pipes.add(new Pipe(WIDTH, (int) (Math.random() * (HEIGHT - 300))));
            }
        }
    }

    private class Bird {
        private int x, y, yVel;
        private final int jumpStrength = -10;

        public Bird(int x, int y) {
            this.x = x;
            this.y = y;
            this.yVel = 0;
        }

        public void paint(Graphics g) {
            g.setColor(Color.yellow);
            g.fillRect(x, y, BIRD_SIZE, BIRD_SIZE);
        }

        public void update() {
            y += yVel;
            yVel += 1;

            if (yVel > 8) {
                yVel = 8;
            }
        }

        public void jump() {
            yVel = jumpStrength;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}






























