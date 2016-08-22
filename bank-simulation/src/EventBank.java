
/**
 * EventBank object, for use in eventQueue and teller queues.
 *
 * @author Sam Beckmann
 */
public class EventBank implements Comparable<EventBank>
{
    private final int timeOfDay;
    private final int serviceTime;
    private final int eventType;

    public EventBank(int timeOfDay, int serviceTime, int eventType)
    {
        this.timeOfDay = timeOfDay;
        this.serviceTime = serviceTime;
        this.eventType = eventType;
    }

    public int getTimeOfDay()
    {
        return timeOfDay;
    }

    public int getServiceTime()
    {
        return serviceTime;
    }

    public int getEventType()
    {
        return eventType;
    }

    /**
     * Overriding compareTo to make use of a sorted priorityQueue.
     */
    @Override
    public int compareTo(EventBank o)
    {
        if (o.getTimeOfDay() < this.getTimeOfDay())
            return 1;
        else
            return o.getTimeOfDay() == this.getTimeOfDay() ? 0 : -1;
    }
}
