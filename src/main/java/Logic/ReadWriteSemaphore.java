package Logic;

public class ReadWriteSemaphore {

    private int currentWriters;
    private int currentReaders;
    private int writerRequests;

    private static ReadWriteSemaphore readWriteSemaphore;

    private ReadWriteSemaphore()    {
        currentWriters = 0;
        currentReaders = 0;
        writerRequests = 0;
    }

    public static synchronized ReadWriteSemaphore getReadWriteSemaphore()   {
        if(null == readWriteSemaphore)  {
            readWriteSemaphore = new ReadWriteSemaphore();
        }
        return readWriteSemaphore;
    }

    public synchronized void acquireRead() throws InterruptedException {
        while(currentWriters > 0 || writerRequests > 0) {
            wait();
        }
        currentReaders++;
    }

    public synchronized void acquireWrite() throws InterruptedException {
        writerRequests++;
        while(currentReaders > 0 || currentWriters > 0) {
            wait();
        }
        currentWriters++;
        writerRequests--;
    }

    public synchronized void releaseRead()  {
        currentReaders--;
        notifyAll();
    }

    public synchronized void releaseWrite() {
        currentWriters--;
        notifyAll();
    }
}
