package Interfaces;

public interface ServerNotifier {

    public void notifyConnect();

    public void notifyDisconnect();

    public void addListener(ClientConnectionListener ccl);

    public void removeListener(ClientConnectionListener ccl);

}
