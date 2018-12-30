package server;

import transmission.Packet;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WriteSignal {
    private int idInSession;
    private Packet packet;
    private boolean continueRun = true;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public WriteSignal(int idInSession) {
        this.idInSession = idInSession;
    }

    synchronized Packet getPacket() {
        return packet;
    }

    synchronized void setPacket(Packet packet) {
        this.packet = packet;
    }

    synchronized void stopRunning() {
        this.continueRun = false;
        condition.signal();
    }

    synchronized boolean isContinueRun() {
        return continueRun;
    }

    synchronized void signal() {
        condition.signal();
    }

    synchronized void await() throws InterruptedException {
        condition.await();
    }

    public void setIdInSession(int idInSession) {
        this.idInSession = idInSession;
    }

    public int getIdInSession() {
        return idInSession;
    }
}
