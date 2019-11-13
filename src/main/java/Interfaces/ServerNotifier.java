package Interfaces;

public interface ServerNotifier {

    public void doConnect();

    public void doDisconnect();

    public void addListener(ClientConnectionListener ccl);

    public void removeListener(ClientConnectionListener ccl);

}
