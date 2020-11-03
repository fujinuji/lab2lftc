package ubb.scs.lftc.lab4;

import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        AF af = new AF();
        /*af.read("resources/files/secvente.txt");
        af.printAFDetails();
        af.checkSequence1(Collections.singletonList("cbaababcbbbaaca"));
        System.out.println(af.checkSequence("abcabbbcabc"));
        System.out.println(af.theBiggestSequence("cbaababcb"));*/


        af.read("resources/files/".concat("secvente.txt"));
        label:
        while (true) {
            System.out.println("1. Introduceti elemenetele automatului manual");
            System.out.println("2. Introdcueti fisierul de intrare al automatului");
            System.out.println("3. Printati elementele automatului: ");
            System.out.println("4. Verificati daca o secvenata apartine automatului");
            System.out.println("5. Verificati cel mai lung prefix");
            System.out.println("6. Iesire");

            Scanner scanner = new Scanner(System.in);
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    af = new AF();
                    System.out.println("Intorudceti starile auromatului separate prin virgula: ");
                    String states = scanner.nextLine();
                    System.out.println("Intorduceti alfabetul automatului separat prin virgula: ");
                    String alphabet = scanner.nextLine();
                    System.out.println("Intoridceti tranzitiile automatului de forma (destinatie)-(sursa),(litera), separate prin ; :");
                    String transitions = scanner.nextLine();
                    System.out.println("Intorduceti starea initiala: ");
                    String initialState = scanner.nextLine();
                    System.out.println("Introduceti starile finale: ");
                    String finalStates = scanner.nextLine();

                    states = "Q=".concat(states);
                    alphabet = "A=".concat(alphabet);
                    transitions = "T=".concat(transitions);
                    initialState = "I=".concat(initialState);
                    finalStates = "F=".concat(finalStates);

                    af.setAFFromKeyboard(Arrays.asList(states, alphabet, transitions, initialState, finalStates));
                    break;
                case "2":
                    af = new AF();
                    System.out.println("Intoroducti fisierul de intrare: ");
                    String filename = scanner.nextLine();
                    af.read("resources/files/".concat(filename));
                    break;
                case "3":
                    af.printAFDetails();
                    af.drawFuckingTable();
                    break;
                case "4": {
                    System.out.println("Introduceti secventa de verificat: ");
                    String sequence = scanner.nextLine();
                    System.out.println(af.checkSequence(sequence) ? "Este acceptata" : "Nu este acceptata");
                    break;
                }
                case "5": {
                    System.out.println("Intoroduceti secventa inifiala: ");
                    String sequence = scanner.nextLine();
                    String prefix = af.theBiggestSequence(sequence);
                    System.out.println("Cel mai lung prefix este: " + prefix);
                    break;
                }
                case "6":
                    break label;
                default:
                    System.out.println("Optiune invalida");
                    break;
            }
        }
    }
}
