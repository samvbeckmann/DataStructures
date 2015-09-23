import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 * Simulation object, contains everything needed for a simulation to run.
 *
 * @author Sam Beckmann
 */
public class Simulation
{
    private static final int SNAPSHOT_PERIOD = 500;

    private PriorityQueue<EventBank> eventQueue;
    private int clock;
    private Teller[] tellers;

    /* initial parameters */
    final private int arrivalMean;
    final private int arrivalVariance;
    final private int serviceMean;
    final private int serviceVariance;

    /* stats */
    private int maxQueueLength;
    private int maxWaitTime;
    private int customersServed;
    private int totalWaitTime;

    /* verify */
    private double totalArrivalTime;
    private double totalServiceTime;
    private int customersGenerated;

    private Random rnd;


    /**
     * Constructor for a simulation.
     *
     * @param numTellers Number of tellers in this simulation
     * @param arrivalMean Mean inter-arrival time for generating new customers
     * @param arrivalVariance Possible variance on inter-arrival time
     * @param serviceMean Mean service time for customers
     * @param serviceVariance Possible variance on service time
     */
    public Simulation(int numTellers, int arrivalMean, int arrivalVariance, int serviceMean, int serviceVariance)
    {
        this.tellers = new Teller[numTellers];
        for(int i = 0; i < tellers.length; i++)
            tellers[i] = new Teller();
        this.arrivalMean = arrivalMean;
        this.arrivalVariance = arrivalVariance;
        this.serviceMean = serviceMean;
        this.serviceVariance = serviceVariance;
        clock = 0;
        rnd = new Random();
        this.eventQueue = new PriorityQueue<>();
        eventQueue.add(generateNewCustomer());
        this.eventQueue.add(new EventBank(SNAPSHOT_PERIOD, 0, -2));
    }

    /**
     * Driving method for advancing the simulation.
     * Runs the first event in the queue, including all repercussions.
     */
    public void handleEvent()
    {
        EventBank event = eventQueue.remove();

        for (Teller teller : tellers)
        {
            if (teller.getCustomers().isEmpty())
                teller.addIdleTime(event.getTimeOfDay() - clock);
        }

        clock = event.getTimeOfDay();

        switch (event.getEventType())
        {
            case -1:
                handleArrival(event);
                break;
            case -2:
                handleSnapshot();
                break;
            default:
                handleDeparture(event.getEventType());
        }
    }

    /**
     * Prints a snapshot of the current state of the simulation to the console.
     */
    private void handleSnapshot()
    {
        System.out.println("**************************");
        System.out.printf("Current Time: %12d\n", clock);
        System.out.printf("Event Queue length: %6d\n", eventQueue.size());
        for(int i = 0; i < tellers.length; i++)
        {
            System.out.printf("Teller %d Queue Length: %3d\n", i, tellers[i].getCustomers().size());
        }
        System.out.println();

        eventQueue.add(new EventBank(clock + SNAPSHOT_PERIOD, 0, -2));
    }

    /**
     * Handles all events associated with a departing customer.
     * @param teller Teller whose customer is departing.
     */
    private void handleDeparture(int teller)
    {
        customersServed++;
        EventBank customer = tellers[teller].getCustomers().remove();
        int waitTime = clock - (customer.getTimeOfDay() + customer.getServiceTime());
        maxWaitTime = Math.max(waitTime, maxWaitTime);
        totalWaitTime += waitTime;
        createDepartureIfNeeded(teller, true);
    }

    /**
     * Generates a departure event in the eventQueue if a new one is needed.
     * @param teller Teller that might need a departure generated
     * @param departure If this method was called by a departure.
     */
    private void createDepartureIfNeeded(int teller, boolean departure)
    {
        Queue<EventBank> customers = tellers[teller].getCustomers();
        boolean needsDeparture = !customers.isEmpty() && (departure || customers.size() == 1);
        if (needsDeparture)
        {
            EventBank customer = customers.peek();
            eventQueue.add(new EventBank(clock + customer.getServiceTime(), customer.getServiceTime(), teller));
        }
    }

    /**
     * Handles all events associated with an arriving customer.
     * @param event The event of the customer's arrival.
     */
    private void handleArrival(EventBank event)
    {
        int shortestQueue = getShortestQueue();
        tellers[shortestQueue].getCustomers().add(event);
        createDepartureIfNeeded(shortestQueue, false);
        eventQueue.add(generateNewCustomer());
        updateMaxQueueLength();
    }

    /**
     * Updates the Max Queue Length.
     */
    private void updateMaxQueueLength()
    {
        for (Teller teller : tellers)
            maxQueueLength = Math.max(teller.getCustomers().size(), maxQueueLength);
    }

    /**
     * @return The teller with the current shortest queue length,
     *         or the lowest numbered teller in event of a tie.
     */
    private int getShortestQueue()
    {
        int min = 0;
        for (int i = 0; i < tellers.length; i++)
        {
            if (tellers[i].getCustomers().size() < tellers[min].getCustomers().size())
                min = i;
        }
        return min;
    }

    /**
     * @return A new customer, to be added to the event queue.
     */
    private EventBank generateNewCustomer()
    {
        int arrivalTime = DistributionHelper.uniform(arrivalMean, arrivalVariance, rnd);
        int serviceTime = DistributionHelper.uniform(serviceMean, serviceVariance, rnd);
        totalArrivalTime += arrivalTime;
        totalServiceTime += serviceTime;
        customersGenerated++;
        return new EventBank(clock + arrivalTime, serviceTime, -1);
    }

    public int getClock()
    {
        return clock;
    }

    /**
     * @return A string of the comprehensive results of the simulation at the time of calling.
     */
    public String generateResults()
    {
        String str = "";
        str += "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
        str += String.format("1. Number of Customers Processed: %d\n", customersServed);
        str += String.format("2. Average Inter-Arrival Time: %f\n", totalArrivalTime / (double) customersGenerated);
        str += String.format("3. Average Service Time: %f\n", totalServiceTime / (double) customersGenerated);
        str += String.format("4. Average Wait Time Per Customer: %f\n", totalWaitTime / (double) customersServed);
        str += "5. Percent Idle Times:\n";
        for (int i = 0; i < tellers.length; i++)
        {
            str += String.format("\tTeller %d Percent Idle Time: %02.3f%%\n", i, tellers[i].getIdleTime() / (double) clock * 100);
        }
        str += String.format("6. Maximum Customer Wait Time: %d\n", maxWaitTime);
        str += String.format("7. Maximum Queue Length: %d\n", maxQueueLength);
        int abandonedCustomers = 0;
        for (Teller teller : tellers)
            abandonedCustomers += teller.getCustomers().size();
        str += String.format("8. Total Customers Left in Queues: %d\n", abandonedCustomers);

        return str;
    }
}
