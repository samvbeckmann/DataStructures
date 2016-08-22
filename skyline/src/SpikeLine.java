import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Set of Spikes that represent a building or skyline.
 * A spike is a mapping of a position to a height at that position.
 *
 * @author Sam Beckmann
 */
public class SpikeLine
{
    /**
     * Spike map. Key is position, Value is height at that position.
     */
    private Map<Integer, Integer> spikes;

    /**
     * Building notation constructor for a SpikeLine
     *
     * @param left leftmost position of the building
     * @param height height of the building
     * @param right rightmost position of the building
     */
    public SpikeLine(int left, int height, int right)
    {
        spikes = new TreeMap<>(); // TreeMap since spikes need to be sorted by key.

        for (int i = left; i <= right; i++)
        {
            spikes.put(i, height);
        }
    }

    /**
     * Merge constructor for a SpikeLine.
     *
     * @param sky1 First SpikeLine to be merged
     * @param sky2 Second SpikeLine to be merged
     */
    public SpikeLine(SpikeLine sky1, SpikeLine sky2)
    {
        Iterator<Map.Entry<Integer, Integer>> it = sky1.getSpikes().entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Integer, Integer> pair = it.next();
            if (sky2.getSpikes().containsKey(pair.getKey()))
                sky2.getSpikes().put(pair.getKey(), Math.max(sky2.getSpikes().get(pair.getKey()), pair.getValue()));
            else
                sky2.getSpikes().put(pair.getKey(), pair.getValue());
            it.remove();
        }

        spikes = sky2.getSpikes();
    }

    public Map<Integer, Integer> getSpikes()
    {
        return spikes;
    }

    /**
     * Converts SpikeLine to "p,h,p,h, ... p,h" notation.
     *
     * @return String representation of SpikeLine in output notation.
     */
    @Override
    public String toString()
    {
        String str = "";
        Iterator<Map.Entry<Integer, Integer>> it = spikes.entrySet().iterator();

        int prevHeight = -1;
        int pos = -1;
        int prevPos = -1;
        while (it.hasNext())
        {
            Map.Entry<Integer, Integer> pair = it.next();
            pos = pair.getKey();
            if (pos != prevPos + 1 && prevHeight != 0)
            {
                str += String.format("%d,0,", prevPos + 1);
                prevHeight = 0;
            }
            if (pair.getValue() != prevHeight)
            {
                str += String.format("%d,%d,", pos, pair.getValue());
                prevHeight = pair.getValue();
            }
            prevPos = pos;
        }
        str += String.format("%d,0", pos + 1);
        return str;
    }
}
