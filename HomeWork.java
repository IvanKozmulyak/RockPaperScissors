package src;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public class HomeWork {

    public static void main(String[] args) {
        System.out.println();
        if (args.length < 3) {
            System.out.println("Please enter the number of parameters greater than three");
        } else if (args.length % 2 == 0) {
            System.out.println("Please enter an odd number of parameters");
        } else {
            Set<String> uniq = new HashSet<>(Arrays.asList(args));
            if (uniq.size() != args.length){
                System.out.println("Please do not use duplicate parameters");
                System.exit(-1);
            }
            HashMap<Integer, String> moves = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                moves.put(i, args[i]);
            }
            SecureRandom secureRandom = new SecureRandom();
            byte[] key = secureRandom.generateSeed(16);
            int computerMove = secureRandom.nextInt(args.length);
            String move = moves.get(computerMove);
            byte[] hmac = calcHmacSha256(key, move.getBytes(StandardCharsets.UTF_8));
            System.out.printf("HMAC: %032x%n", new BigInteger(1, hmac));
            System.out.println("Available moves:");
            while (true) {
                for (int i = 0; i < args.length; i++) {
                    System.out.println(i + 1 + " - " + moves.get(i));
                }
                System.out.println("0 - exit");
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter your move: ");
                int userMove = 0;
                try {
                    userMove = scanner.nextInt();
                } catch (Exception e) {
                    System.out.println("Incorrect input! Please enter number!");
                    continue;
                }
                if (userMove <= args.length && userMove >= 0) {
                    if (userMove == 0) {
                        break;
                    } else {
                        System.out.println("Your move: " + moves.get(userMove - 1));
                        System.out.println("Computer move: " + move);
                        if (userMove - 1 == computerMove) {
                            System.out.println("Draw!");
                        } else if (userMove - 1 <= args.length / 2) {
                            for (int i = userMove; i < userMove + args.length / 2; i++) {
                                if (computerMove == i) {
                                    System.out.println("You lose!");
                                    break;
                                }
                                if (i == (userMove + args.length / 2) - 1) {
                                    System.out.println("You win!");
                                }
                            }
                        } else if (userMove - 1 > args.length / 2) {
                            for (int i = userMove - 1 - args.length / 2; i < userMove - 1; i++) {
                                if (computerMove == i) {
                                    System.out.println("You win!");
                                    break;
                                }
                                if (i == (userMove - 1) - 1) {
                                    System.out.println("You lose!");
                                }
                            }
                        }
                        System.out.printf("HMAC key: %032x%n", new BigInteger(1, key));
                        break;
                    }
                } else {
                    System.out.println("Enter a number from 0 to " + args.length);
                }
            }
        }
    }

    static public byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }
}
