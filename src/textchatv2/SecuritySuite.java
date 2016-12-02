/*
 * Copyright (C) 2016 Lucas Burdell lucas.burdell@blackburn.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package textchatv2;

import java.math.BigInteger;
import textchatv2.algorithms.PlainText;
import textchatv2.algorithms.RC4;
import textchatv2.algorithms.TEA;

/**
 *
 * @author Lucas Burdell lucas.burdell@blackburn.edu
 */
public class SecuritySuite {

    private static final Class[] SOLUTIONS = {PlainText.class, RC4.class, TEA.class};



    public static SecuritySolution getSecuritySolution(int solutionIndex, byte[] key) {
        if (solutionIndex < 0 || solutionIndex >= SOLUTIONS.length) {
            throw new RuntimeException("Invalid security solution");
        }
        Class solutionClass = SOLUTIONS[solutionIndex];
        try {
            SecuritySolution solution
                    = (SecuritySolution) solutionClass.getConstructor().newInstance();
            solution.setKey(key);
            return solution;
        } catch (Exception e) {
            // ignore all the red text...
            // Reflection always throws up red flags
        }
        return null;
    }
}
