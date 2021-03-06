/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package NextLongReverser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NextLongEquivalentFinder {
    //{0, 107048004364969} offsets
    //{{-33441*2/(32768*2), 46603/65536}, {17549*2/(32768*2), 39761/65536}}/65536 will be our inverse matrix

    /**
     * Adds seeds which give nextLongs congruent to your structure seed to a list.
     * Has a precondition that structureSeed is 48 bits (its upper 16 bits as a long are 0)
     * @param structureSeed the 48 bit version of the seed
     * @param seedList a list to add the seeds to
     */
    public static void addSeedsToList(long structureSeed, List<Long> seedList) {
        long lowerBits = structureSeed & 0xffff_ffffL;
        long upperBits = structureSeed >>> 32;
        //Did the lower bits affect the upper bits
        if ((lowerBits & 0x8000_0000L) != 0)
            upperBits += 1; //restoring the initial value of the upper bits

        //TODO I can only guarantee the algorithm's correctness for bitsOfDanger = 0 but believe 1 should still always work, needs to be confirmed!!!

        //The algorithm is meant to have bitsOfDanger = 0, but this runs into overflow issues.
        //By using a different small value, we introduce small numerical error which probably cannot break things
        //while keeping everything in range of a long and avoiding nasty BigDecimal/BigInteger overhead
        int bitsOfDanger = 1;

        long lowMin = lowerBits << 16 - bitsOfDanger;
        long lowMax = ((lowerBits + 1) << 16 - bitsOfDanger) - 1;
        long upperMin = ((upperBits << 16) - 107048004364969L) >> bitsOfDanger;

        //hardcoded matrix multiplication again
        long m1lv = Math.floorDiv(lowMax * -33441 + upperMin * 17549, 1L << 31 - bitsOfDanger) + 1; //I cancelled out a common factor of 2 in this line
        long m2lv = Math.floorDiv(lowMin *  46603 + upperMin * 39761, 1L << 32 - bitsOfDanger) + 1;

        //with a lot more effort you can make these loops check 2 things and not 4 but I'm not sure it would even be much faster
        for (int i = 0; i <=1; i++) for (int j = 0; j <=1; j++) {
            long seed = (-39761 * (m1lv + i) + 35098 * (m2lv + j));
            if ((46603 * (m1lv + i) + 66882 * (m2lv + j)) + 107048004364969L >>> 16 == upperBits) {
                if (seed >>> 16 == lowerBits)
                    seedList.add((254681119335897L * seed + 120305458776662L) & 0xffff_ffff_ffffL); //pull back 2 LCG calls
            }
        }
    }

    /**
     * Returns of list of seeds which give nextLongs congruent to your structure seed.
     * Has a precondition that structureSeed is 48 bits (its upper 16 bits as a long are 0)
     * @param structureSeed the 48 bit version of the seed
     */
    public static ArrayList<Long> getSeeds(long structureSeed) {
        ArrayList<Long> seeds = new ArrayList<>(2);
        addSeedsToList(structureSeed,seeds);
        return seeds;
    }

    /**
     * Adds nextLongs congruent to your structure seed to a list.
     * Has a precondition that structureSeed is 48 bits (its upper 16 bits as a long are 0)
     * @param structureSeed the 48 bit version of the seed
     * @param nextLongs a list to add the nextLongs to
     */
    public static void addNextLongEquivalents(long structureSeed, List<Long> nextLongs) {
        //this technically does some redundant operations
        for (long seed: getSeeds(structureSeed)) {
            nextLongs.add(new Random(seed ^ 0x5deece66dL).nextLong());
        }
    }

    /**
     * Returns of nextLongs congruent to your structure seed.
     * Has a precondition that structureSeed is 48 bits (its upper 16 bits as a long are 0)
     * @param structureSeed the 48 bit version of the seed
     */
    public static ArrayList<Long> getNextLongEquivalents(long structureSeed) {
        ArrayList<Long> nextLongs = new ArrayList<>(2);
        addNextLongEquivalents(structureSeed,nextLongs);
        return nextLongs;
    }

    public static void main(String[] args) {
        Random r = new Random();
        long count = 0;
        while (true) {
            if (count % 10000000 == 0)
                System.out.println(count);
            count++;
            long ss = r.nextLong() & 0xffff_ffff_ffffL;
            r.setSeed(ss ^ 0x5deece66dL);
            if (!getSeeds(r.nextLong() & 0xffff_ffff_ffffL).contains(ss))
                System.out.println("asdfasdfasdf");
        }
    }
}