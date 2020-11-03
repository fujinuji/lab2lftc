package ubb.scs.lftc.lab4;

import ubb.scs.lftc.lab4.model.State;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AF {
    private static final String EGAL = "=";
    private static final String VIRGULA = ",";
    private static final String SECVENTE = "resources/files/secvente.txt";
    private List<String> alphabet;
    private Map<String, State> state;
    private State initialState;

    public AF() {
        alphabet = new ArrayList<>();
        state = new HashMap<>();
    }

    /**
     * Printeaza elementele automatului finit curent
     * Printeaza starile automatului, starea initiala, starea finala, alfabetul si tranzitiile
     */
    public void printAFDetails() {
        System.out.print("Alfabet: ");
        for (String s : alphabet) {
            System.out.print(s + " ");
        }
        System.out.println();

        System.out.print("Stari: ");
        for (String s : state.keySet()) {
            System.out.print(s + " ");
        }
        System.out.println();

        System.out.println("Tranzitii:");
        for (String key : state.keySet()) {
            for (String letter : state.get(key).getDestinations().keySet()) {
                for (State s : state.get(key).getDestinations().get(letter)) {
                    System.out.println(key + " -> "
                            + s.getDescription() + " cu " + letter);
                }
            }
        }

        System.out.print("Stari finale: ");
        for (String key : state.keySet()) {
            if (state.get(key).isFinalState()) {
                System.out.print(key + " ");
            }
        }

        System.out.println();

    }

    /**
     * Setaza automatul conform elementelor de la tastatura
     */
    public void setAFFromKeyboard(List<String> info) throws Exception {
        setAF(info);
    }

    /**
     * Seteaza automatul conform elementelor din fisier
     */
    public void read(String fileName) throws Exception {
        List<String> lines = Files.lines(Paths.get(fileName)).collect(Collectors.toList());
        setAF(lines);
    }

    /**
     * Seteaza elementele automatului, de la stari initile, la stari finale, etc
     */
    private void setAF(List<String> lines) throws Exception {
        int index = 0;

        for (String line : lines) {
            switch (index) {
                case 0:
                    createStates(line);
                    index += 1;
                    break;
                case 1:
                    createAlphabet(line);
                    index += 1;
                    break;
                case 2:
                    setInitialState(line);
                    index += 1;
                    break;
                case 3:
                    setFinalStates(line);
                    index += 1;
                    break;
                default:
                    setTransition(line);
            }

        }
    }

    /**
     * Setaeaza tranzitiile automatului, primid o linie de tranzitii
     * dupa facand graful intern
     */
    private void setTransition(String line) throws Exception {
        if (line.length() != 3)
            throw new Exception("Transition state should contain only 3 letters");

        if (!state.containsKey(String.valueOf(line.charAt(0)))) {
            throw new Exception("Start state should exits");
        }

        if (!state.containsKey(String.valueOf(line.charAt(2)))) {
            throw new Exception("End state should exits");
        }

        if (!alphabet.contains(String.valueOf(line.charAt(1)))) {
            throw new Exception("Transit element should be in alphabet");
        }

        String start = String.valueOf(line.charAt(0));
        String end = String.valueOf(line.charAt(2));
        String transit = String.valueOf(line.charAt(1));
        state.get(start).addDestination(transit, state.get(end));
    }

    private void createStates(String line) {
        for (Character stateName : line.toCharArray()) {
            if (!state.containsKey(stateName.toString())) {
                state.put(stateName.toString(), new State(stateName.toString()));
            }
        }
    }

    private void createAlphabet(String line) {
        for (Character letter : line.toCharArray()) {
            if (!alphabet.contains(letter.toString())) {
                alphabet.add(letter.toString());
            }
        }
    }

    private void setInitialState(String line) throws Exception {
        String initialStateName = line.trim();

        if (state.containsKey(initialStateName)) {
            initialState = state.get(initialStateName);
        } else {
            throw new Exception("Initial state is not valid!");
        }
    }

    private void setFinalStates(String line) throws Exception {
        for (Character stateName : line.toCharArray()) {
            boolean found = false;
            for (String s : state.keySet()) {
                if (s.equals(stateName.toString())) {
                    state.get(stateName.toString()).setFinalState(true);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new Exception("State " + stateName + " is not valid!");
            }
        }
    }

    public void drawFuckingTable() {
        TableGenerator tableGenerator = new TableGenerator();
        List<String> header = new ArrayList<>();
        header.add("");
        header.addAll(alphabet);
        header.add("");

        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, State> entry : state.entrySet()) {
            List<String> row = new ArrayList<>();

            row.add(entry.getKey());

            for (String letter : alphabet) {
                if (entry.getValue().getDestinations().containsKey(letter)) {
                    row.add(entry.getValue().getDestinations().get(letter).get(0).getDescription());
                } else {
                    row.add("");
                }
            }

            if (entry.getValue().isFinalState()) {
                row.add("1");
            } else {
                row.add("");
            }

            rows.add(row);
        }
        System.out.println(tableGenerator.generateTable(header, rows));
    }

    private List<State> getNextStatesByLetter(State currentStat, String next) {
        if (!currentStat.getDestinations().containsKey(next))
            return new ArrayList<>();
        return currentStat.getDestinations().get(next);
    }

    private boolean checkSequenceBool(String sequence) {
        for (char a : sequence.toCharArray()) {
            if (!state.containsKey(String.valueOf(a))) {
                return false;
            }
        }
        return true;
    }

    private boolean isRoad(State state, String letter){
        if(state.getDestinations().containsKey(letter)){
            return true;
        }
        return false;
    }

    public void checkSequence1(List<String> lines ) throws Exception {
        //List<String> lines = new IOActions(fileName).read();
        for (String sequence : lines) {
            //checkIfAlphabetContainsLetters(sequence);

            int i = 0;
            StringBuilder result = new StringBuilder();
            StringBuilder buffer = new StringBuilder();
            State s = initialState;
            while (i < sequence.length()
                    && isRoad(s, Character.toString(sequence.charAt(i)))) {
                s = s.getDestinations()
                        .get(Character.toString(sequence.charAt(i))).get(0);
                buffer.append(sequence.charAt(i));
                if (s.isFinalState()) {
                    result.delete(0, result.length());
                    result.append(buffer);
                }
                i++;
            }

            printResult(sequence, result, s);
        }
    }

    private void printResult(String sequence, StringBuilder result, State s) {
        if (result.length() == sequence.length() && s.isFinalState()) {
            System.out.println("Secventa " + sequence
                    + " este acceptata de automat finit");
        } else {
            if (result.length() != 0) {
                System.out
                        .println("Secventa "
                                + sequence
                                + " nu este acceptata de automat finit, iar cel mai lung prefix acceptat este "
                                + result.toString());
            } else {
                System.out
                        .println("Secventa "
                                + sequence
                                + " nu este acceptata de automat finit si niciun prefix nu reprezinta o secventa acceptata");
            }
        }
    }

    public void printStates() {
        System.out.print("Stari: ");

        for (String state : state.keySet()) {
            System.out.print(state + " ");
        }
        System.out.println("");
    }

    public void printAlphabet() {
        System.out.print("Alfabet: ");

        for (String letter : alphabet) {
            System.out.print(letter + " ");
        }

        System.out.println("");
    }

    public void printInitialState() {
        System.out.println("Starea initiala: " + initialState.getDescription());
    }


    public void printFinalStates() {
        System.out.print("Stari finale: ");

        for (State state : state.values()) {
            if (state.isFinalState())
                System.out.print(state.getDescription() + " ");
        }

        System.out.println("");
    }
}



















































































class TableGenerator {

    private int PADDING_SIZE = 2;
    private String NEW_LINE = "\n";
    private String TABLE_JOINT_SYMBOL = "+";
    private String TABLE_V_SPLIT_SYMBOL = "|";
    private String TABLE_H_SPLIT_SYMBOL = "-";

    public String generateTable(List<String> headersList, List<List<String>> rowsList, int... overRiddenHeaderHeight) {
        StringBuilder stringBuilder = new StringBuilder();

        int rowHeight = overRiddenHeaderHeight.length > 0 ? overRiddenHeaderHeight[0] : 1;

        Map<Integer, Integer> columnMaxWidthMapping = getMaximumWidhtofTable(headersList, rowsList);

        stringBuilder.append(NEW_LINE);
        stringBuilder.append(NEW_LINE);
        createRowLine(stringBuilder, headersList.size(), columnMaxWidthMapping);
        stringBuilder.append(NEW_LINE);


        for (int headerIndex = 0; headerIndex < headersList.size(); headerIndex++) {
            fillCell(stringBuilder, headersList.get(headerIndex), headerIndex, columnMaxWidthMapping);
        }

        stringBuilder.append(NEW_LINE);

        createRowLine(stringBuilder, headersList.size(), columnMaxWidthMapping);


        for (List<String> row : rowsList) {

            for (int i = 0; i < rowHeight; i++) {
                stringBuilder.append(NEW_LINE);
            }

            for (int cellIndex = 0; cellIndex < row.size(); cellIndex++) {
                fillCell(stringBuilder, row.get(cellIndex), cellIndex, columnMaxWidthMapping);
            }

        }

        stringBuilder.append(NEW_LINE);
        createRowLine(stringBuilder, headersList.size(), columnMaxWidthMapping);
        stringBuilder.append(NEW_LINE);
        stringBuilder.append(NEW_LINE);

        return stringBuilder.toString();
    }

    private void fillSpace(StringBuilder stringBuilder, int length) {
        for (int i = 0; i < length; i++) {
            stringBuilder.append(" ");
        }
    }

    private void createRowLine(StringBuilder stringBuilder, int headersListSize, Map<Integer, Integer> columnMaxWidthMapping) {
        for (int i = 0; i < headersListSize; i++) {
            if (i == 0) {
                stringBuilder.append(TABLE_JOINT_SYMBOL);
            }

            for (int j = 0; j < columnMaxWidthMapping.get(i) + PADDING_SIZE * 2; j++) {
                stringBuilder.append(TABLE_H_SPLIT_SYMBOL);
            }
            stringBuilder.append(TABLE_JOINT_SYMBOL);
        }
    }


    private Map<Integer, Integer> getMaximumWidhtofTable(List<String> headersList, List<List<String>> rowsList) {
        Map<Integer, Integer> columnMaxWidthMapping = new HashMap<>();

        for (int columnIndex = 0; columnIndex < headersList.size(); columnIndex++) {
            columnMaxWidthMapping.put(columnIndex, 0);
        }

        for (int columnIndex = 0; columnIndex < headersList.size(); columnIndex++) {

            if (headersList.get(columnIndex).length() > columnMaxWidthMapping.get(columnIndex)) {
                columnMaxWidthMapping.put(columnIndex, headersList.get(columnIndex).length());
            }
        }


        for (List<String> row : rowsList) {

            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {

                if (row.get(columnIndex).length() > columnMaxWidthMapping.get(columnIndex)) {
                    columnMaxWidthMapping.put(columnIndex, row.get(columnIndex).length());
                }
            }
        }

        for (int columnIndex = 0; columnIndex < headersList.size(); columnIndex++) {

            if (columnMaxWidthMapping.get(columnIndex) % 2 != 0) {
                columnMaxWidthMapping.put(columnIndex, columnMaxWidthMapping.get(columnIndex) + 1);
            }
        }


        return columnMaxWidthMapping;
    }

    private int getOptimumCellPadding(int cellIndex, int datalength, Map<Integer, Integer> columnMaxWidthMapping, int cellPaddingSize) {
        if (datalength % 2 != 0) {
            datalength++;
        }

        if (datalength < columnMaxWidthMapping.get(cellIndex)) {
            cellPaddingSize = cellPaddingSize + (columnMaxWidthMapping.get(cellIndex) - datalength) / 2;
        }

        return cellPaddingSize;
    }

    private void fillCell(StringBuilder stringBuilder, String cell, int cellIndex, Map<Integer, Integer> columnMaxWidthMapping) {

        int cellPaddingSize = getOptimumCellPadding(cellIndex, cell.length(), columnMaxWidthMapping, PADDING_SIZE);

        if (cellIndex == 0) {
            stringBuilder.append(TABLE_V_SPLIT_SYMBOL);
        }

        fillSpace(stringBuilder, cellPaddingSize);
        stringBuilder.append(cell);
        if (cell.length() % 2 != 0) {
            stringBuilder.append(" ");
        }

        fillSpace(stringBuilder, cellPaddingSize);

        stringBuilder.append(TABLE_V_SPLIT_SYMBOL);

    }


}




































































