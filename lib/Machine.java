package lib;

public class Machine {

    private Order[] buffer;
    private int numToProduce;
    private Logger logger;

    public Machine(int numToProduce, int bufferSize, Logger logger) {
        this.numToProduce = numToProduce;
        buffer = new Order[bufferSize];
        this.logger = logger;
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
        Work.goWork(1);
        buffer[lastProduced] = order;
        logger.addLog(String.format("p%d puts %d",
                    order.getProducerId(), order.getOrderId()));
        // System.out.printf("produced, buffer: [");
        // for (int i = 0; i < buffer.length; i++) {
        //     System.out.printf("%s, ", buffer[i]);
        // }
        // System.out.printf("]\n");
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
        Work.goWork(1);
        Order consumed = buffer[lastConsumed];
        logger.addLog(String.format("c%d gets %d from p%d",
                    consumerId, consumed.getOrderId(), consumed.getProducerId()));
        // System.out.printf("consumer %d consumed %s\n", consumerId, consumed);
        numItemsInBuffer--;
        notifyAll();
        return consumed;
    }

}

