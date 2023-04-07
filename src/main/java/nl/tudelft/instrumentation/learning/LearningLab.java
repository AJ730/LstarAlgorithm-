package nl.tudelft.instrumentation.learning;

import org.checkerframework.checker.units.qual.A;

import java.util.*;

/**
 * You should write your own solution using this class.
 */
public class LearningLab {
    static Random r = new Random();
    static int traceLength = 10;
    static boolean isFinished = false;

    static ObservationTable observationTable;
    static EquivalenceChecker equivalenceChecker;

    static ArrayList<Integer> timeMap = new ArrayList<>();

    static long runtime = 0;

    static void run() {
        final long start = System.currentTimeMillis();
        timeMap.add(0);
        SystemUnderLearn sul = new RersSUL();
        observationTable = new ObservationTable(LearningTracker.inputSymbols, sul);
        preprocessing();
//       equivalenceChecker = new RandomWalkEquivalenceChecker(sul, LearningTracker.inputSymbols, 100, 1000);
        equivalenceChecker = new WMethodEquivalenceChecker(sul, LearningTracker.inputSymbols, 4, observationTable, observationTable);
        MealyMachine hypothesis = observationTable.generateHypothesis();
        Optional<Word<String>> counterexample = equivalenceChecker.verify(hypothesis);
        System.out.println("Here is the counter example: " + counterexample);
        while (counterexample.isPresent()) {
            processCounterexample(counterexample.get());
            preprocessing();
            MealyMachine newHypothesis = observationTable.generateHypothesis();
            timeMap.add(getNumberOfStates(observationTable.S));
            counterexample = equivalenceChecker.verify(newHypothesis);
        }

        hypothesis.writeToDot("hypothesis.dot");
        final long end = System.currentTimeMillis();
        long runtime  = end - start;

        System.out.println("Number of states: " + timeMap.get(timeMap.size()-1));
        System.out.println("Time map: " + timeMap);
        System.out.println("Runtime (in ms): "+ runtime);
        System.out.println("Membership queries: "+ LearningTracker.membershipQuery);
    }

    private static void preprocessing() {
        while(!observationTable.checkForClosed().isEmpty()){
            observationTable.addToS(observationTable.checkForClosed().get());
        }
        while(!observationTable.checkForConsistent().isEmpty()){
            observationTable.addToE(observationTable.checkForConsistent().get());
        }
    }

    public static int getNumberOfStates(List<Word<String>> states){
        Set<ArrayList<String>> set = new HashSet<>();
        for (Word<String> element : states){
            set.add(observationTable.table.get(element));
        }
        return set.size();
    }

    public static void processCounterexample(Word<String> counterExample){
        var counterExamplleList = counterExample.asList();
        for (int i = 0; i < counterExamplleList.size(); i++) {
            var list = counterExamplleList.subList(0, i);
            var wordList = new Word<String>();
            wordList = wordList.append(list);
            observationTable.addToS(wordList);
            observationTable.addToE(wordList);
        }
    }

    /**
     * Method that is used for catching the output from standard out.
     * 
     * @param out the string that has been outputted in the standard out.
     */
    public static void output(String out) {
        // System.out.println(out);
    }
}
