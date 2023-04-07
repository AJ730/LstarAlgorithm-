package nl.tudelft.instrumentation.learning;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;

public class WMethodEquivalenceChecker extends EquivalenceChecker{

    private int w;
    private AccessSequenceGenerator accessSequenceGenerator;
    private DistinguishingSequenceGenerator distinguishingSequenceGenerator;

    private Random random;

    public WMethodEquivalenceChecker(SystemUnderLearn sul, String[] inputSymbols, int w, DistinguishingSequenceGenerator dg, AccessSequenceGenerator ag) {
        super(sul, inputSymbols);
        this.w = w;
        this.distinguishingSequenceGenerator= dg;
        this.accessSequenceGenerator= ag;
        this.random = new Random();
    }

    @Override
    public Optional<Word<String>> verify(MealyMachine hypothesis) {
        // TODO implement the W-method equivalence checker
        var e  = distinguishingSequenceGenerator.getDistinguishingSequences();
        var s = accessSequenceGenerator.getAccessSequences();
        long time = System.currentTimeMillis();

        while (true) {
            int randS = random.nextInt(s.size());
            Word<String> randElemS = s.get(randS);

            Word<String> finalI = new Word<String>();
            for (int i = 0; i < this.inputSymbols.length; i++) {
                String curr = inputSymbols[random.nextInt(inputSymbols.length)];
                finalI.append(curr);
            }

            int randE = random.nextInt(e.size());
            Word<String> randElemE = e.get(randE);
            var append = randElemS.append(finalI);
            append = append.append(randElemE);


            String modelOutput = hypothesis.getLastOutput(append);
            String realOutput = sul.getLastOutput(append);

            if (!modelOutput.equals(realOutput)) {
                return Optional.of(append);
            }

            if(System.currentTimeMillis() - time > 10000){
                return Optional.empty();
            }
        }
    }
}
