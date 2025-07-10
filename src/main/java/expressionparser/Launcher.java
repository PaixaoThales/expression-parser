package expressionparser;

import expressionparser.model.Interpreter;

import java.util.Scanner;

public class Launcher {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Interpreter interpreter = new Interpreter();
            while (true) {
                System.out.print(">> ");
                String input = scanner.nextLine();
                if (input.isBlank()) {
                    continue;
                }
                if (input.trim().equals("exit")) {
                    System.out.println("Exiting...");
                    break;
                }
                System.out.println(interpreter.execute(input).value());
            }
        }
    }
}
