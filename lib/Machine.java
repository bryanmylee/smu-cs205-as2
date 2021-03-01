package lib;

public class Machine {

    private Order[] buffer;

    private int lastProduced;
    private int lastConsumed;
    private int numItems;

    public Machine(int size) {
        buffer = new Order[size];
    }

    private int orderId;

    public synchronized void addOrder(int producerId) {
        while (numItems >= buffer.length) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        lastProduced = (lastProduced + 1) % buffer.length;
        buffer[lastProduced] = new Order(producerId, orderId++);
        System.out.printf("produced, buffer: [");
        for (int i = 0; i < buffer.length; i++) {
            System.out.printf("%s, ", buffer[i]);
        }
        System.out.printf("]\n");
        numItems++;
        notifyAll();
    }

    public synchronized Order consumeOrder(int consumerId) {
        while (numItems <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        lastConsumed = (lastConsumed + 1) % buffer.length;
        Order consumed = buffer[lastConsumed];
        System.out.printf("consumer %d consumed %s\n", consumerId, consumed);
        numItems--;
        notifyAll();
        return consumed;
    }

}

