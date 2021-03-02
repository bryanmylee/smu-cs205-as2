package lib.actors;

import lib.Machine;
import lib.NoRemainingOrdersException;

public class ProducerThread extends Thread {

    private volatile Machine machine;

    private int id;

    public ProducerThread(int id, Machine machine) {
        this.id = id;
        this.machine = machine;
    }

    @Override
    public void run() {
        try {
            while (true) {
                machine.addOrder(id);
            }
        } catch (NoRemainingOrdersException e) {
            System.out.println("completed production");
        }
    }

}

