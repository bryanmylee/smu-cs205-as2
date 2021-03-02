package lib.actors;

import lib.Machine;
import lib.NoRemainingOrdersException;

public class ConsumerThread extends Thread {

    private volatile Machine machine;

    private int id;

    public ConsumerThread(int id, Machine machine) {
        this.id = id;
        this.machine = machine;
    }

    @Override
    public void run() {
        try {
            while (true) {
                machine.consumeOrder(id);
            }
        } catch (NoRemainingOrdersException e) {
            System.out.println("completed consumption");
        }
    }

}

