package lib;

public class HotDogMachine {

    int bufferSize;
    Order[] buffer;

    ProducerThread[] producers;
    ConsumerThread[] consumers;

    public HotDogMachine(int numToProduce, int bufferSize,
            int numProducers, int numConsumers) {
        this.bufferSize = bufferSize;
        this.buffer = new Order[bufferSize];

        producers = new ProducerThread[numProducers];
        for (int i = 0; i < numProducers; i++) {
            producers[i] = new ProducerThread(this, i);
            producers[i].start();
        }

        consumers = new ConsumerThread[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new ConsumerThread(this, i);
            consumers[i].start();
        }
    }

    volatile private int orderId = 0;
    volatile private int numItems = 0;

    Integer lastProducedIndex = 0;
    Integer lastConsumedIndex = 0;

    public void queueOrder(Order order) {
        synchronized(lastProducedIndex) {
            while (numItems >= bufferSize) {
                try {
                    lastProducedIndex.wait();
                } catch (InterruptedException e) {}
            }
            numItems++;
            order.setOrderId(orderId++);
            System.out.printf("queued order %s\n", order);
            lastProducedIndex = (lastProducedIndex + 1) % bufferSize;
            buffer[lastProducedIndex] = order;
        }
        synchronized(lastConsumedIndex) {
            lastConsumedIndex.notify();
        }
    }

    public Order consumeOrder() {
        synchronized(lastConsumedIndex) {
            while (numItems <= 0) {
                try {
                    lastConsumedIndex.wait();
                } catch (InterruptedException e) {}
            }
            numItems--;
            lastConsumedIndex = (lastConsumedIndex + 1) % bufferSize;
            synchronized(lastProducedIndex) {
                lastProducedIndex.notify();
            }
            return buffer[lastConsumedIndex];
        }
    }

}

