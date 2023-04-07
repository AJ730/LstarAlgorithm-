package nl.tudelft.instrumentation.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

/**
 * @author Bram Verboom
 *         The observation of the L* algorithm for learning mealy machines.
 */

public class ObservationTable implements DistinguishingSequenceGenerator, AccessSequenceGenerator {

    public static Word<String> word;

    private static final String SEPERATOR = ",";
    private static final String LAMBDA = " ";
    private static final Word<String> EMPTY = new Word<>();

    public String[] inputSymbols;
    public List<Word<String>> S;
    private List<Word<String>> E;

    // The actual observations: a map with (S u S A) as keys where each value
    // (row) represents the observations corresponding to (S u S A) E.
    public Map<Word<String>, ArrayList<String>> table;
    private SystemUnderLearn sul;

    public ObservationTable(String[] inputSymbols, SystemUnderLearn sul) {
        this.sul = sul;
        this.S = new ArrayList<>();
        this.E = new ArrayList<>();
        this.inputSymbols = inputSymbols;
        table = new HashMap<>();
        this.addToS(EMPTY);
        for (String s : this.inputSymbols) {
            Word<String> sym = new Word<>(s);
            this.addToE(sym);
        }
    }

    /**
     * Method that is used for checking whether the observation table is closed
     *
     * You should write your own logic here.
     *
     * @return an Optional.empty() if the table is consistent, or an Optional.of(_)
     *         with something useful to extend the observation table with.
     */
    public Optional<Word<String>> checkForClosed() {
        // TODO implement the check for closedness of the observation table.
        for (Word<String> i: table.keySet()){
            ArrayList<String> rowSA = table.get(i);
            if (!S.contains(i)){
                int count = 0;
                for (Word<String> j: S) {
                    ArrayList<String> rowS = table.get(j);
                    if (rowSA.equals(rowS)) {
                        count++;
                        break;
                    }
                }
                if (count == 0) {
                    return Optional.of(i);
                }
            }

        }
        return Optional.empty();
    }

    /**
     * Method that is used for checking whether the observation table is consistent
     *
     * You should write your own logic here.
     *
     * @return an Optional.empty() if the table is consistent, or an Optional.of(_)
     *         with something usefull to extend the observation table with.
     */
    public Optional<Word<String>> checkForConsistent() {
        // TODO implement the consistency check.
        for (Word<String> i: S){
            ArrayList<String> rowS1 = table.get(i);
            for (Word<String> j: S){
                ArrayList<String> rowS2 = table.get(j);
                if (rowS1.equals(rowS2)) {
                    for (String str : inputSymbols) {
                        Word<String> sa1 = i.append(str);
                        Word<String> sa2 = j.append(str);
                        ArrayList<String> rowsa1 = table.get(sa1);
                        ArrayList<String> rowsa2 = table.get(sa2);
                        if (!rowsa1.equals(rowsa2)) {
                            for (int k = 0; k < rowsa1.size(); k++) {
                                if(!rowsa1.get(k).equals(rowsa2.get(k))){
                                    var w = new Word<String>();
                                    w = w.append(str).append(E.get(k));
                                    return Optional.of(w);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public String getResultFromSul(Word<String> trace) {
        String res = sul.getLastOutput(trace);
        // System.out.printf("Output for trace %s is %s\n", trace, res);
        return res;
    }

    /**
     * Method that is used for adding a new prefix to S
     * 
     * @param prefix the prefix to add to S, must be a list of symbols in the
     *               alphabet
     */
    public void addToS(Word<String> prefix) {
//        System.out.printf("Adding %s to S\n", prefix);
        if (!S.contains(prefix)) {
            S.add(prefix);
            addRow(prefix);
            for (String symbol : inputSymbols) {
                addRow(prefix.append(symbol));
            }
        }
    }

    /**
     * Method that is used for adding a new suffix to E
     *
     * @param suffix the suffix to add to E, must be a list of symbols in the
     *               alphabet
     */
    public void addToE(Word<String> suffix) {
//        System.out.printf("Adding %s to E\n", suffix);
        if (!E.contains(suffix)) {
            E.add(suffix);
            for (Entry<Word<String>, ArrayList<String>> entry : table.entrySet()) {
                Word<String> joined = entry.getKey().append(suffix);
                entry.getValue().add(getResultFromSul(joined));
            }
        }
    }

    /**
     * Method for adding a row to the observation table.
     *
     * Adds a row to the observation table and fills it with the correct
     * observations.
     */
    private void addRow(Word<String> base) {

        if (table.containsKey(base)) {
            return;
        } else {
            ArrayList<String> row = new ArrayList<>();
            for (Word<String> e : E) {
                row.add(getResultFromSul(base.append(e)));
            }
            table.put(base, row);
        }
    }

    private String rowToKey(ArrayList<String> input) {
        return String.join(",", input);
    }

    /**
     * Method to generate a {@link MealyMachine} from this observation table.
     *
     * Note: in order to generate a MealyMachine the observation table must be
     * consistent and closed. For any inconstistencies the model will create dummy
     * states.
     *
     * @return an MealyMachine that reflects the observations
     */
    public MealyMachine generateHypothesis() {
        Map<String, MealyState> states = new HashMap<>();
        int numStates = 0;
        for (Word<String> s : S) {
            String key = rowToKey(table.get(s));
            if (!states.containsKey(key)) {
                states.put(key, new MealyState(String.format("s%s", numStates++)));
            }
        }
        for (Word<String> s : S) {
            ArrayList<String> baseRow = table.get(s);
            MealyState from = states.get(rowToKey(baseRow));
            int index_in_alphabet = 0;
            for (String sym : inputSymbols) {
                Word<String> base = s.append(sym);
                ArrayList<String> row = table.get(base);
                String toKey = rowToKey(row);
                String output = baseRow.get(index_in_alphabet++);

                if (states.containsKey(toKey)) {
                    MealyTransition newTransition = new MealyTransition(output, states.get(toKey));
                    MealyTransition t = from.next(sym);
                    if (t != null && !t.equals(newTransition)) {
                        // assert false: "Observation table is not consistent";
                        MealyState unknownState = new MealyState(String.format("\"s%s?\"", numStates++));
                        newTransition = new MealyTransition(output, unknownState);
                        for (String sym2 : inputSymbols) {
                            unknownState.addEdge(sym2, new MealyTransition("?", unknownState));
                        }
                        from.addEdge(sym, new MealyTransition("?", unknownState));
                    } else {
                        from.addEdge(sym, newTransition);
                    }
                } else {
                    // assert false : "Observation table is not closed";
                    MealyState unknownState = new MealyState(String.format("\"s%s?\"", numStates++));
                    MealyTransition newTransition = new MealyTransition(output, unknownState);
                    for (String sym2 : inputSymbols) {
                        MealyTransition newTransition2 = new MealyTransition("?", unknownState);
                        unknownState.addEdge(sym2, newTransition2);
                    }
                    from.addEdge(sym, newTransition);
                }
            }
        }
        MealyState initialState = states.get(rowToKey(table.get(EMPTY)));
        return new MealyMachine(initialState);
    }

    public static String pretty(ArrayList<String> trace) {
        if (trace.size() == 0) {
            return LAMBDA;
        }
        return String.join(SEPERATOR, trace);
    }

    /**
     * Method to print the observation table in a nice way.
     */
    public void print() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> header = new ArrayList<>();
        rows.add(null);
        header.add("E");
        header.add(" ");
        for (Word<String> e : E) {
            header.add(e.toString());
        }
        rows.add(header);
        rows.add(null);
        int index = 0;
        for (Word<String> s : S) {
            ArrayList<String> row = new ArrayList<>();
            row.add(index++ == 0 ? "S" : "");
            row.add(s.toString());
            row.addAll(table.get(s));
            rows.add(row);
        }
        rows.add(null);
        index = 0;
        for (Word<String> s : S) {
            for (String symbol : inputSymbols) {
                Word<String> joined = s.append(symbol);
                ArrayList<String> row = new ArrayList<>();
                row.add(index++ == 0 ? "SA" : "");
                row.add(joined.toString());
                row.addAll(table.get(joined));
                rows.add(row);
            }
        }
        rows.add(null);
        int[] minSizes = new int[rows.get(1).size()];
        Arrays.fill(minSizes, 1);
        for (ArrayList<String> row : rows) {
            if (row == null) {
                continue;
            }
            for (int i = 0; i < minSizes.length; i++) {
                minSizes[i] = Math.max(minSizes[i], row.get(i).length() + 1);
            }
        }
        String empty = "-";
        for (int i = 0; i < minSizes.length; i++) {
            for (int j = 0; j < minSizes[i] + 1; j++) {
                empty += "-";
            }
            empty += "-";
        }
        for (ArrayList<String> row : rows) {
            if (row == null) {
                System.out.println(empty);
                continue;
            }
            for (int i = 0; i < minSizes.length; i++) {
                int l = minSizes[i];
                String f = "%-" + l + "s";
                row.set(i, String.format(f, row.get(i)));
            }
            System.out.printf("| %s|\n", String.join("| ", row));
        }
    }

    @Override
    public List<Word<String>> getAccessSequences() {
        return new ArrayList<>(S);
    }

    @Override
    public List<Word<String>> getDistinguishingSequences() {
        return new ArrayList<>(E);
    }

}
