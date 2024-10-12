import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnackGame {
    public static void main(String[] args) {
        new GameFrame(); // Initialize game frame
    }
}

class GameFrame extends JFrame {
    GameFrame() {
        this.add(new GamePanel()); // Add game panel to the frame
        this.setTitle("SNAKE"); // Set frame title
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit on close
        this.setResizable(false); // Prevent resizing
        this.pack(); // Pack the components
        this.setVisible(true); // Make the frame visible
        this.setLocationRelativeTo(null); // Center the frame
    }
}

class GamePanel extends JPanel implements ActionListener {
    // Constants for game settings
    static final int SCREEN_WIDTH = 600; // Width of the screen
    static final int SCREEN_HEIGHT = 600; // Height of the screen
    static final int UNIT_SIZE = 25; // Size of each unit (snake segment, apple, obstacle)
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; // Total units
    static final int INITIAL_DELAY = 150; // Initial speed delay
    int delay = INITIAL_DELAY; // Current speed delay
    final int x[] = new int[GAME_UNITS]; // X-coordinates of the snake
    final int y[] = new int[GAME_UNITS]; // Y-coordinates of the snake
    int bodyParts = 5; // Initial length of the snake
    int applesEaten; // Number of apples eaten
    int appleX; // X-coordinate of the apple
    int appleY; // Y-coordinate of the apple
    int[][] obstacles; // Array to hold obstacles
    int obstacleCount = 10; // Number of obstacles
    boolean running = false; // Game state
    Timer timer; // Timer for game loop
    Random random; // Random generator for apple and obstacle positions
    char direction = 'R'; // Initial direction of the snake
    int level = 1; // Level counter

    GamePanel() {
        random = new Random(); // Initialize random generator
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); // Set panel size
        this.setBackground(Color.black); // Set background color
        this.setFocusable(true); // Make panel focusable
        this.addKeyListener(new MyKeyAdapter()); // Add key listener for controls
        startGame(); // Start the game
    }

    public void startGame() {
        applesEaten = 0; // Reset apples eaten
        bodyParts = 5; // Reset snake length
        direction = 'R'; // Reset direction
        delay = INITIAL_DELAY; // Reset delay
        obstacleCount = 10; // Reset obstacle count
        obstacles = new int[obstacleCount][2]; // Initialize obstacle array
        newApple(); // Generate new apple
        newObstacles(); // Generate new obstacles
        running = true; // Set game to running
        level = 1; // Reset level to 1 on game start
        timer = new Timer(delay, this); // Create timer
        timer.start(); // Start the timer
    }

    public void newApple() {
        // Generate new apple position
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;

        // Ensure apple does not spawn on the snake or obstacles
        boolean onSnake;
        do {
            onSnake = false; // Reset flag
            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    onSnake = true; // Apple is on snake, regenerate
                    appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
                    appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
                    break; // Break the loop
                }
            }
            // Check against obstacles
            for (int i = 0; i < obstacles.length; i++) {
                if (obstacles[i][0] == appleX && obstacles[i][1] == appleY) {
                    onSnake = true; // Apple is on obstacle, regenerate
                    appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
                    appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
                    break; // Break the loop
                }
            }
        } while (onSnake); // Continue until apple is valid
    }

    public void newObstacles() {
        // Generate new obstacle positions
        for (int i = 0; i < obstacles.length; i++) {
            boolean tooClose;
            do {
                tooClose = false; // Reset flag
                obstacles[i][0] = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
                obstacles[i][1] = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;

                // Avoid obstacles spawning too close to the snake's head or each other
                for (int j = 0; j < i; j++) {
                    if (Math.abs(obstacles[i][0] - obstacles[j][0]) < UNIT_SIZE * 3 &&
                        Math.abs(obstacles[i][1] - obstacles[j][1]) < UNIT_SIZE * 3) {
                        tooClose = true; // Obstacles are too close, regenerate
                        break; // Break the loop
                    }
                }
                // Avoid spawning near snake's head
            } while (tooClose || (Math.abs(obstacles[i][0] - x[0]) < UNIT_SIZE * 4 &&
                                  Math.abs(obstacles[i][1] - y[0]) < UNIT_SIZE * 4));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call parent method
        draw(g); // Draw game components
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake with color change based on level
            Color snakeColor = getSnakeColor(); // Get color based on level
            for (int i = 0; i < bodyParts; i++) {
                g.setColor(snakeColor); // Apply the same color for the entire snake
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw obstacles
            g.setColor(Color.orange);
            for (int[] obstacle : obstacles) {
                g.fillRect(obstacle[0], obstacle[1], UNIT_SIZE, UNIT_SIZE);
            }

            // Display score and level
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 35));
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
            g.drawString("Level: " + level, 10, g.getFont().getSize());
        } else {
            gameOver(g); // Display game over screen
        }
    }

    public Color getSnakeColor() {
        // Change snake color based on level
        switch (level) {
            case 1: return Color.green; // Level 1 color
            case 2: return Color.blue; // Level 2 color
            case 3: return Color.yellow; // Level 3 color
            case 4: return Color.magenta; // Level 4 color
            case 5: return Color.cyan; // Level 5 color
            default: return Color.white; // Default color for higher levels
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1]; // Shift body segments
            y[i] = y[i - 1]; // Shift body segments
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE; // Move up
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE; // Move down
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE; // Move left
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE; // Move right
                break;
        }

        // Implement wall wrapping logic
        if (x[0] < 0) {
            x[0] = SCREEN_WIDTH - UNIT_SIZE; // Wrap to the right
        } else if (x[0] >= SCREEN_WIDTH) {
            x[0] = 0; // Wrap to the left
        }
        if (y[0] < 0) {
            y[0] = SCREEN_HEIGHT - UNIT_SIZE; // Wrap to the bottom
        } else if (y[0] >= SCREEN_HEIGHT) {
            y[0] = 0; // Wrap to the top
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++; // Increase body length
            applesEaten++; // Increment apples eaten
            newApple(); // Generate new apple
            if (applesEaten % 5 == 0) {
                level++; // Increase level every 5 apples
                delay = Math.max(50, delay - 10); // Increase speed
                timer.setDelay(delay); // Update timer delay
                newObstacles(); // Generate new obstacles on level up
            }
        }
    }

    public void checkCollisions() {
        // Check for collision with itself
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false; // Stop the game
            }
        }

        // Check for collision with obstacles
        for (int[] obstacle : obstacles) {
            if (x[0] == obstacle[0] && y[0] == obstacle[1]) {
                running = false; // Stop the game
            }
        }

        // Stop the game if the snake runs into itself or the obstacles
        if (!running) {
            timer.stop(); // Stop the timer
        }
    }

    public void gameOver(Graphics g) {
        // Show game over message
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        g.drawString("Game Over", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2 - 30);

        // Show final score
        g.setFont(new Font("Ink Free", Font.BOLD, 35));
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 + 30);

        // Restart prompt
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        g.drawString("Press Enter to Restart", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Press Enter to Restart")) / 2, SCREEN_HEIGHT / 2 + 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move(); // Move the snake
            checkApple(); // Check for apple collision
            checkCollisions(); // Check for collisions
        }
        repaint(); // Repaint the panel
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L'; // Change direction to left
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R'; // Change direction to right
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U'; // Change direction to up
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D'; // Change direction to down
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running) {
                        startGame(); // Restart the game
                    }
                    break;
            }
        }
    }
}
