import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for Skyline project.
 *
 * @author Sam Beckmann
 */
public class Skyline
{
    /**
     * List of buildings in the skyline, in SpikeLine notation.
     */
    private List<SpikeLine> buildings = new ArrayList<>();

    /**
     * Main method for Skyline project.
     *
     * @param args array of filenames of skylines to be run
     */
    public static void main(String[] args)
    {
        for (String arg : args)
        {
            Skyline skyline = new Skyline(arg);
            System.out.println("Running Skyline: " + arg);
            System.out.println("Result by Induction:\n" + skyline.useInduction());
            System.out.println("Result by Divide and Conquer:\n" + skyline.useDaC());
            System.out.println();
        }
    }

    /**
     * Skyline constructor. Parses a given file to create SpikeLine representations of each building.
     *
     * @param arg file containing the buildings in a skyline
     */
    public Skyline(String arg)
    {
        try
        {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(arg));
            while ((line = br.readLine()) != null)
            {
                String[] building = line.trim().split(" +");
                buildings.add(new SpikeLine(Integer.parseInt(building[0]),
                        Integer.parseInt(building[1]),
                        Integer.parseInt(building[2])));
            }
        } catch (IOException e)
        {
            System.err.printf("Whoops! Couldn't read file: %s\n\n", arg);
            System.exit(2);
        }
    }

    /**
     * Creates a skyline from a set of buildings via the method of induction:
     * i.e., continually adding on one building to the skyline.
     *
     * @return A skyline, formatted as "p,h,p,h, ... p,h"
     */
    private String useInduction()
    {
        SpikeLine result = buildings.get(0);
        for (int i = 1; i < buildings.size(); i++)
        {
            result = new SpikeLine(result, buildings.get(i));
        }
        return result.toString();
    }

    /**
     * Creates a skyline from a set of building via divide and conquer
     * Divides the skyline into smaller sets of buildings, then merges them together.
     *
     * @return A skyline, formatted as "p,h,p,h, ... p,h"
     */
    private String useDaC()
    {
        return divideAndConquer(0, buildings.size()).toString();
    }

    /**
     * Recursive method for divide and conquer.
     *
     * @param start index of building list to start
     * @param end index of building list to end
     * @return a SpikeLine made of buildings from start to end - 1.
     */
    private SpikeLine divideAndConquer(int start, int end)
    {
        if (end - start < 2)
            return buildings.get(start);
        else
        {
            int pivot = (end + start) / 2;
            return new SpikeLine(divideAndConquer(start, pivot), divideAndConquer(pivot, end));
        }

    }
}
