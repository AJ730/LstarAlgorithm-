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
        var time = System.currentTimeMillis();

        while (true) {
            int randS = random.nextInt(s.size());
            Word<String> randElemS = s.get(randS);


            StringBuilder sr = new StringBuilder();
            for (int i = 0; i < w; i++) {
                int randI = random.nextInt(this.inputSymbols.length);
                sr.append(inputSymbols[randI]);
            }
            var finalI = sr.toString();

            int randE = random.nextInt(e.size());
            Word<String> randElemE = e.get(randE);
            var append = randElemS.append(finalI).append(randElemE);

            String modelOutput = hypothesis.getLastOutput(append);
            String realOutput = sul.getLastOutput(append);

            if (!modelOutput.equals(realOutput)) {
                return Optional.of(append);
            }

            if(System.currentTimeMillis() - time < 5000){
                return Optional.empty();
            }
        }
    }
}
