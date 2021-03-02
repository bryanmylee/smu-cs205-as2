package lib.actors;

import lib.Machine;
import lib.NoRemainingOrdersException;
import lib.Order;
import lib.Work;

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
                int orderId = machine.allocateOrderId();
                Work.goWork(6);
                machine.addOrder(new Order(id, orderId));
            }
        } catch (NoRemainingOrdersException e) {
            System.out.println("completed production");
        }
    }

}

