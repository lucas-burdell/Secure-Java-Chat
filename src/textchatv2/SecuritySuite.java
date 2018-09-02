package textchatv2;

import textchatv2.algorithms.PlainText;
import textchatv2.algorithms.RC4;
import textchatv2.algorithms.TEA;

public class SecuritySuite {

    private static final Class[] SOLUTIONS = {PlainText.class, RC4.class, TEA.class};

    public static SecuritySolution getSecuritySolution(int solutionIndex, byte[] key) throws Exception {
        if (solutionIndex < 0 || solutionIndex >= SOLUTIONS.length) {
            throw new RuntimeException("Invalid security solution");
        }
        Class solutionClass = SOLUTIONS[solutionIndex];
        SecuritySolution solution = (SecuritySolution) 
                        solutionClass.getConstructor().newInstance();
        solution.setKey(key);
        return solution;
    }
}