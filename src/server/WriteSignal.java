package server;

import transmission.TextPacket;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WriteSignal {
    private TextPacket packet = new TextPacket();
    private boolean continueRun = true;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    synchronized TextPacket getPacket() {
        return packet;
    }

    synchronized void setPacket(TextPacket packet) {
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
}
