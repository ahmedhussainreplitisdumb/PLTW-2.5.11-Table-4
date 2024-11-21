import java.util.Random;
import java.util.Scanner;

class Game {
    private Player player1;
    static public Player player2;
    private Player currentPlayer;
    static public int pileSize;
    private boolean isGameOver;
    private boolean isSinglePlayer;

    public Game(String player1Name, boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;
        player1 = new Player(player1Name);
        if (isSinglePlayer) {
            player2 = new AIPlayer("AI");  // AI Player in single-player mode
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the name of Player 2: ");
            String player2Name = scanner.nextLine();
            player2 = new Player(player2Name);  // Human Player 2 in two-player mode
        }
        pileSize = GameLogic.generatePileSize();
        currentPlayer = GameLogic.chooseStartingPlayer(player1, player2);
        isGameOver = false;
    }

    public void startGame() {
        System.out.println("Welcome to the Game of Nim!");
        System.out.println("Initial pile size: " + pileSize);
        System.out.println(currentPlayer.getName() + " will start.");

        while (!isGameOver) {
            playTurn();
            checkWinner();
        }
    }

    private void playTurn() {
        System.out.println("\n" + currentPlayer.getName() + "'s turn.");
        System.out.println("Current pile size: " + pileSize);

        int move = currentPlayer.makeMove(pileSize);
        if (GameLogic.validateMove(move, pileSize)) {
            pileSize -= move;
            System.out.println(currentPlayer.getName() + " removed " + move + " pieces.");

            // Special case: If the AI has reduced the pile to 1 in single-player mode, player loses
            if (isSinglePlayer && currentPlayer instanceof AIPlayer && pileSize == 1) {
                System.out.println("You lose, the AI always wins!");
                isGameOver = true;
                return;
            }

            currentPlayer = (currentPlayer == player1) ? player2 : player1;
        } else {
            System.out.println("Invalid move. Try again.");
        }
    }

    private void checkWinner() {
        if (pileSize <= 1 && !isGameOver) {
            isGameOver = true;
            Player winner = (currentPlayer == player1) ? player2 : player1;
            winner.updateScore();
            System.out.println("\n" + winner.getName() + " wins!");
            System.out.println("Score: " + player1.getName() + " - " + player1.getScore() + ", " +
                               player2.getName() + " - " + player2.getScore());
            playAgain();
        }
    }

    private void playAgain() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWould you like to play again? (yes/no)");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            pileSize = GameLogic.generatePileSize();
            currentPlayer = GameLogic.chooseStartingPlayer(player1, player2);
            isGameOver = false;
            System.out.println("\nStarting a new game...");
            startGame();
        } else {
            System.out.println("Thanks for playing!");
        }
    }
}

class Player {
    private String name;
    private int score;

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void updateScore() {
        score++;
    }

    public int makeMove(int pileSize) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of pieces to remove (1 to " + pileSize / 2 + "): ");
        int move = scanner.nextInt();
        return move;
    }
}

class AIPlayer extends Player {
    public AIPlayer(String name) {
        super(name);
    }

    
    public int makeMove(int pileSize) {
        int optimalMove = calculateOptimalMove(pileSize);
        System.out.println(getName() + " removes " + optimalMove + " pieces.");
        return optimalMove;
    }

    // Calculate the optimal move to leave the pile in a winning state
    private int calculateOptimalMove(int pileSize) {
        int nimSum = pileSize;  // Since there's only one pile, nim-sum is just pileSize
        if (nimSum == 0) {
            return 1;  // Take at least one piece if no winning strategy is possible
        }

        // Optimal move to create a nim-sum of 0
        int targetSize = 0;  // Leave pile in a state of 0 nim-sum
        int move = pileSize - targetSize;
        return Math.min(move, pileSize / 2);  // Move must be no more than half of pileSize
    }
}

class GameLogic {
    public static int generatePileSize() {
        Random random = new Random();
        return random.nextInt(41) + 10; // generates a number between 10 and 50
    }

    public static boolean validateMove(int move, int pileSize) {
        return move >= 1 && move <= pileSize / 2;
    }

    public static Player chooseStartingPlayer(Player player1, Player player2) {
        Random random = new Random();
        return random.nextBoolean() ? player1 : player2;
    }
}

public class Runner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose game mode: Enter '1' for Single Player (AI) or '2' for Two Player: ");
        int gameModeChoice = scanner.nextInt();
        scanner.nextLine();  // consume newline character

        boolean isSinglePlayer = (gameModeChoice == 1);

        System.out.println("Enter the name of Player 1: ");
        String player1Name = scanner.nextLine();

        Game game = new Game(player1Name, isSinglePlayer);
        game.startGame();
    }
}
