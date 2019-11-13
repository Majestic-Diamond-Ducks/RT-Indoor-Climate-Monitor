package Logic;

import Interfaces.TimeoutListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class TimeoutNotifier extends TimerTask {

    private List<TimeoutListener> listeners;

    public TimeoutNotifier()  {
        listeners = new ArrayList<>();
    }

    public void addListener(TimeoutListener listener)   {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TimeoutListener listener)    {
        listeners.remove(listener);
    }

    @Override
    public void run() {
        for(TimeoutListener listener : listeners)   {
            listener.checkTimeout();
        }
    }
}
