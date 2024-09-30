import java.util.Scanner;
import java.util.Arrays;

public class Yahtzee {
    private static final int NUM_CATEGORIES = 13;
    private static final int NUM_DICE = 5;
    private static final int MAX_ROLLS = 3;

    private int currentPlayer;
    private int[] dice;
    private int[] scores;
    private boolean[] categoriesUsed;

    private int yahtzeeBonus;
    private int largeStraightBonus;
    private int bonusScore;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of players (1-6): ");
        int numPlayers = scanner.nextInt();

        if (numPlayers < 1 || numPlayers > 6) {
            System.out.println("Invalid number of players. Please enter a number between 1 and 6.");
            return;
        }

        Yahtzee game = new Yahtzee(numPlayers);
        game.playGame();
    }

    public Yahtzee(int numPlayers) {
        currentPlayer = 0;
        dice = new int[NUM_DICE];
        scores = new int[numPlayers * NUM_CATEGORIES];
        categoriesUsed = new boolean[NUM_CATEGORIES];

        yahtzeeBonus = 0;
        largeStraightBonus = 0;
        bonusScore = 0;
    }

    public void playGame() {
        while (!allCategoriesUsed()) {
            System.out.println("\nPlayer " + (currentPlayer + 1) + "'s turn:");

            for (int roll = 1; roll <= MAX_ROLLS; roll++) {
                rollDice();
                System.out.println("Dice: " + Arrays.toString(dice));

                if (roll < MAX_ROLLS) {
                    System.out.println("Which dice to reroll? (Enter indices separated by spaces):");
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine();
                    String[] indices = input.split(" ");
                    for (String index : indices) {
                        int i = Integer.parseInt(index);
                        dice[i - 1] = (int) (Math.random() * 6) + 1;
                    }
                }
            }

            System.out.println("Choose a category:");
            printCategories();
            int category = chooseCategory();
            int score = calculateScore(category);
            scores[currentPlayer * NUM_CATEGORIES + category] = score;
            categoriesUsed[category] = true;

            System.out.println("Score: " + score);
            currentPlayer = (currentPlayer + 1) % scores.length / NUM_CATEGORIES;
        }

        // Calculate final scores and declare winner
        int[] finalScores = new int[scores.length / NUM_CATEGORIES];
        for (int i = 0; i < finalScores.length; i++) {
            for (int j = 0; j < NUM_CATEGORIES; j++) {
                finalScores[i] += scores[i * NUM_CATEGORIES + j];
            }
        }

        int maxScore = Integer.MIN_VALUE;
        int winningPlayer = -1;
        for (int i = 0; i < finalScores.length; i++) {
            if (finalScores[i] > maxScore) {
                maxScore = finalScores[i];
                winningPlayer = i;
            }
        }

        System.out.println("\nGame over! Player " + (winningPlayer + 1) + " wins with a score of " + maxScore);

        // Display special scores
        System.out.println("Bonus Score: " + bonusScore);
        System.out.println("Yahtzee Bonus: " + yahtzeeBonus);
        System.out.println("Large Straight Bonus: " + largeStraightBonus);
    }

    private void rollDice() {
        for (int i = 0; i < NUM_DICE; i++) {
            dice[i] = (int) (Math.random() * 6) + 1;
        }
    }

    private void printCategories() {
        for (int i = 0; i < NUM_CATEGORIES; i++) {
            System.out.println((i + 1) + ". " + getCategoryName(i));
        }
    }

    private int chooseCategory() {
        Scanner scanner = new Scanner(System.in);
        int category = scanner.nextInt();
        while (!isValidCategory(category)) {
            System.out.println("Invalid category. Please choose a valid category.");
            category = scanner.nextInt();
        }
        return category;
    }

    private int calculateScore(int category) {
        int score = 0;

        switch (category) {
            case 0: // Ones
            case 1: // Twos
            case 2: // Threes
            case 3: // Fours
            case 4: // Fives
            case 5: // Sixes
                for (int die : dice) {
                    if (die == category + 1) {
                        score += category + 1;
                    }
                }
                break;

            case 6: // Three-of-a-kind
                if (isThreeOfAKind()) {
                    for (int die : dice) {
                        score += die;
                    }
                }
                break;

            case 7: // Four-of-a-kind
                if (isFourOfAKind()) {
                    for (int die : dice) {
                        score += die;
                    }
                }
                break;

            case 8: // Full house
                if (isFullHouse()) {
                    score = 25;
                }
                break;

            case 9: // Small straight
                if (isSmallStraight()) {
                    score = 30;
                }
                break;

            case 10: // Large straight
                if (isLargeStraight()) {
                    score = 40;
                }
                break;

            case 11: // Chance
                for (int die : dice) {
                    score += die;
                }
                break;

            case 12: // Yahtzee
                if (isYahtzee()) {
                    score = 50;
                    yahtzeeBonus += 100;
                }
                break;

            default:
                score = 0;
                break;
        }

        return score;
    }

    private boolean allCategoriesUsed() {
        for (boolean used : categoriesUsed) {
            if (!used) {
                return false;
            }
        }
        return true;
    }

    private String getCategoryName(int category) {
        switch (category) {
            case 0:
                return "Ones";
            case 1:
                return "Twos";
            case 2:
                return "Threes";
            case 3:
                return "Fours";
            case 4:
                return "Fives";
            case 5:
                return "Sixes";
            case 6:
                return "Three-of-a-kind";
            case 7:
                return "Four-of-a-kind";
            case 8:
                return "Full house";
            case 9:
                return "Small straight";
            case 10:
                return "Large straight";
            case 11:
                return "Chance";
            case 12:
                return "Yahtzee";
            default:
                return "Invalid category";
        }
    }

    private boolean isValidCategory(int category) {
        return category >= 0 && category < NUM_CATEGORIES && !categoriesUsed[category];
    }

    // Additional methods for special categories
    private boolean isThreeOfAKind() {
        int[] counts = new int[6];
        for (int die : dice) {
            counts[die - 1]++;
        }

        for (int count : counts) {
            if (count >= 3) {
                return true;
            }
        }

        return false;
    }

    private boolean isFourOfAKind() {
        int[] counts = new int[6];
        for (int die : dice) {
            counts[die - 1]++;
        }

        for (int count : counts) {
            if (count >= 4) {
                return true;
            }
        }

        return false;
    }

    private boolean isFullHouse() {
        int[] counts = new int[6];
        for (int die : dice) {
            counts[die - 1]++;
        }

        boolean hasTwo = false;
        boolean hasThree = false;
        for (int count : counts) {
            if (count == 2) {
                hasTwo = true;
            } else if (count == 3) {
                hasThree = true;
            }
        }

        return hasTwo && hasThree;
    }

    private boolean isSmallStraight() {
        Arrays.sort(dice);
        for (int i = 0; i < dice.length - 4; i++) {
            if (dice[i + 1] - dice[i] != 1) {
                return false;
            }
        }
        return true;
    }

    private boolean isLargeStraight() {
        Arrays.sort(dice);
        for (int i = 0; i < dice.length - 4; i++) {
            if (dice[i + 1] - dice[i] != 1) {
                return false;
            }
        }
        return true;
    }

    private boolean isYahtzee() {
        int[] counts = new int[6];
        for (int die : dice) {
            counts[die - 1]++;
        }

        for (int count : counts) {
            if (count == 5) {
                return true;
            }
        }

        return false;
    }
}