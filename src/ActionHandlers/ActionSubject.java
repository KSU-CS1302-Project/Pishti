package ActionHandlers;

import java.util.ArrayList;

public class ActionSubject
{
    public ActionSubject()
    {
        m_observers = new ArrayList<>();
    }

    public void addObserver(ActionObserver observer)
    {
        m_observers.add(observer);
    }

    public void removeObserver(ActionObserver observer)
    {
        m_observers.remove(observer);
    }

    public <T> void sendActionNotification(T object, Action action)
    {
        for (ActionObserver ob : m_observers) {
            ob.onActionNotification(object, action);
        }
    }

    private ArrayList<ActionObserver> m_observers;
}
