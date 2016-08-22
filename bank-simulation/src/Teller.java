import java.util.LinkedList;
import java.util.Queue;

/**
 * Teller Object
 *
 * @author Sam Beckmann
 */
public class Teller
{
    private int idleTime;
    private Queue<EventBank> customers;

    public Teller()
    {
        idleTime = 0;
        customers = new LinkedList<>();
    }

    public Queue<EventBank> getCustomers()
    {
        return customers;
    }

    public int getIdleTime()
    {
        return idleTime;
    }

    public void addIdleTime(int time)
    {
        idleTime += time;
    }
}
