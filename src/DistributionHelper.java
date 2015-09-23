import java.util.Random;

/**
 * Distribution Utilities
 *
 * @author Sam Beckmann
 */
public class DistributionHelper
{
    /**
     * @return a uniformly random integer in the range of mean +- variant
     */
    static public int uniform(int mean, int variant, Random rnd)
    {
        int small = mean - variant;
        int range = 2 * variant + 1;
        return small + rnd.nextInt(range);
    }
}
