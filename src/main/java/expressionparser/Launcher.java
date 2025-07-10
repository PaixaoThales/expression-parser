package expressionparser;

import expressionparser.model.Interpreter;

import java.util.Scanner;

public class Launcher {

    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        Scanner scanner = new Scanner(System.in);
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
