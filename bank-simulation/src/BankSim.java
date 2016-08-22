import java.util.Scanner;

/**
 * Driving class for The Bank Sim project
 *
 * @author Sam Beckmann
 * @version 1.0
 */
public class BankSim
{
    public static void main(String[] args)
    {
        Scanner console = new Scanner(System.in);
        int numTellers, arrivalMean, arrivalVariance, serviceMean, serviceVariance, timeLimit;

        System.out.println("Bank Simulation Program by Sam Beckmann");
        System.out.println("     Project 1 for Data Structures     ");
        System.out.println("---------------------------------------\n");

        System.out.print("Input number of tellers: ");
        numTellers = console.nextInt();

        System.out.print("Input mean inter-arrival time: ");
        arrivalMean = console.nextInt();

        System.out.print("Input variance for inter-arrival times: ");
        arrivalVariance = console.nextInt();

        System.out.print("Input mean service time: ");
        serviceMean = console.nextInt();

        System.out.print("Input variance for service time: ");
        serviceVariance = console.nextInt();

        System.out.print("Input time limit for simulation: ");
        timeLimit = console.nextInt();

        System.out.println();

        Simulation sim = new Simulation(numTellers, arrivalMean, arrivalVariance, serviceMean, serviceVariance);

        while (sim.getClock() <= timeLimit)
            sim.handleEvent();

        System.out.print(sim.generateResults());
    }
}
