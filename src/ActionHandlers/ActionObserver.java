package ActionHandlers;

public interface ActionObserver
{
    <T> void onActionNotification(T object, Action action);
}
