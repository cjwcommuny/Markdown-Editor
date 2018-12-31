package server;

import transmission.Packet;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WriteSignal {
    private int idInSession;
    private final AtomicReference<Packet> packet = new AtomicReference<>();
    private final AtomicBoolean continueRun = new AtomicBoolean(true);
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    WriteSignal(int idInSession) {
        this.idInSession = idInSession;
    }

    synchronized Packet getPacket() {
        return this.packet.get();
    }

    void setPacket(Packet packet) {
        this.packet.set(packet);
    }

    synchronized void stopRunning() {
        this.continueRun.set(false);
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    boolean isContinueRun() {
        return this.continueRun.get();
    }

    void signal() {
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    void await() throws InterruptedException {
        lock.lock();
        condition.await();
        lock.unlock();
    }

    int getIdInSession() {
        return idInSession;
    }
}
