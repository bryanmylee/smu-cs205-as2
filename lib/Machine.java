package lib;

public class Machine {

    private Order[] buffer;
    private int numToProduce;

    public Machine(int bufferSize, int numToProduce) {
        buffer = new Order[bufferSize];
        this.numToProduce = numToProduce;
    }

    private int lastProduced;
    private int lastConsumed;
    private int numItemsInBuffer;
    private Integer orderId = 0;

    public int allocateOrderId() {
        synchronized(orderId) {
            return orderId++;
        }
    }

    public synchronized void addOrder(Order order) {
        while (numItemsInBuffer >= buffer.length && numToProduce > 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        if (numToProduce <= 0) {
            notifyAll();
            throw new NoRemainingOrdersException();
        }
        lastProduced = (lastProduced + 1) % buffer.length;
        Work.goWork(1000);
        buffer[lastProduced] = order;
        System.out.printf("produced, buffer: [");
        for (int i = 0; i < buffer.length; i++) {
            System.out.printf("%s, ", buffer[i]);
        }
        System.out.printf("]\n");
        numItemsInBuffer++;
        numToProduce--;
        notifyAll();
    }

    public synchronized Order consumeOrder(int consumerId) {
        while (numItemsInBuffer <= 0 && numToProduce > 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        if (numToProduce <= 0 && numItemsInBuffer <= 0) {
            notifyAll();
            throw new NoRemainingOrdersException();
        }
        lastConsumed = (lastConsumed + 1) % buffer.length;
        Work.goWork(1000);
        Order consumed = buffer[lastConsumed];
        System.out.printf("consumer %d consumed %s\n", consumerId, consumed);
        numItemsInBuffer--;
        notifyAll();
        return consumed;
    }

}

