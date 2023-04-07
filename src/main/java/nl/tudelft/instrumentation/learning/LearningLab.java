package nl.tudelft.instrumentation.learning;

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

    static void run() {

        SystemUnderLearn sul = new RersSUL();
        observationTable = new ObservationTable(LearningTracker.inputSymbols, sul);
        preprocessing();
//        equivalenceChecker = new RandomWalkEquivalenceChecker(sul, LearningTracker.inputSymbols, 100, 1000);
        equivalenceChecker = new WMethodEquivalenceChecker(sul, LearningTracker.inputSymbols, 1, observationTable, observationTable);
        MealyMachine hypothesis = observationTable.generateHypothesis();
        Optional<Word<String>> counterexample = equivalenceChecker.verify(hypothesis);
        System.out.println("Counterexample: " + counterexample.toString());
        while (counterexample.isPresent()){
            processCounterexample(counterexample.get());
            preprocessing();
            MealyMachine newHypothesis = observationTable.generateHypothesis();
            counterexample = equivalenceChecker.verify(newHypothesis);
            System.out.println("Counterexample: " + counterexample.toString());
        }
        hypothesis.writeToDot("hypothesis.dot");
        observationTable.print();
    }

    private static void preprocessing() {
        while(!observationTable.checkForClosed().isEmpty()){
            System.out.print("Check closed");
            observationTable.addToS(observationTable.checkForClosed().get());
        }
        while(!observationTable.checkForConsistent().isEmpty()){
            System.out.print("Check consistent");
            observationTable.addToE(observationTable.checkForConsistent().get());
        }
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
