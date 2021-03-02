package lib.actors;

import lib.Machine;
import lib.NoRemainingOrdersException;
import lib.Order;
import lib.Work;

public class ProducerThread extends Thread {

    private Machine machine;

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
                Work.goWork(6); // work required to make an order.
                machine.addOrder(new Order(id, orderId));
            }
        } catch (NoRemainingOrdersException e) {}
    }

}

