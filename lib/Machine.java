package lib;

import java.util.HashMap;
import java.util.Map;
import lib.logger.Logger;

public class Machine {

    private int numToProduce;
    private Order[] buffer;
    private Logger logger;

    public Machine(int numToProduce, int bufferSize, Logger logger) {
        this.numToProduce = numToProduce;
        buffer = new Order[bufferSize];
        this.logger = logger;
    }

    private Integer orderId = 0;

    /**
     * @brief Assign an order with id to a producer.
     *
     * A producer begins to do work for creating an order if and only if there
     * exists an order to be allocated to it.
     *
     * If there are no more orders to produce, throws {@link
     * NoRemainingOrdersException}.
     *
     * Allocation of order ids can be done independently of production and
     * consumption.
     *
     * @return The unique order id allocated to a producer.
     */
    public int allocateOrderId() throws NoRemainingOrdersException {
        synchronized(orderId) {
            if (orderId >= numToProduce) {
                synchronized(this) {
                    notifyAll();
                }
                throw new NoRemainingOrdersException();
            }
            return orderId++;
        }
    }

    Map<Integer, Integer> producerWorksheet = new HashMap<>();
    Map<Integer, Integer> consumerWorksheet = new HashMap<>();

    private void incrementWorksheet(Map<Integer, Integer> worksheet, int id) {
        worksheet.put(id, worksheet.getOrDefault(id, 0) + 1);
    }

    public void logSummary() {
        logger.addLog("summary:");
        for (Map.Entry<Integer, Integer> entry : producerWorksheet.entrySet()) {
            logger.addLog(String.format("p%d makes %d",
                        entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Integer, Integer> entry : consumerWorksheet.entrySet()) {
            logger.addLog(String.format("c%d packs %d",
                        entry.getKey(), entry.getValue()));
        }
    }

    private int lastProducedIdx;
    private int lastConsumedIdx;
    private int numItemsInBuffer;

    /**
     * @brief Add an order to the machine and log the operation atomically.
     *
     * If the machine is at full capacity, the thread will be blocked until
     * some capacity is freed.
     *
     * @param order The order to add to the machine.
     */
    public synchronized void addOrder(Order order) {
        while (numItemsInBuffer >= buffer.length && numToProduce > 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        lastProducedIdx = (lastProducedIdx + 1) % buffer.length;
        Work.goWork(1); // work required to add order to queue.
        buffer[lastProducedIdx] = order;
        incrementWorksheet(producerWorksheet, order.getProducerId());
        logger.addLog(String.format("p%d puts %d",
                    order.getProducerId(), order.getOrderId()));
        numItemsInBuffer++;
        notifyAll();
    }

    /**
     * @brief Consume an order from the machine and log the operation
     *        atomically.
     *
     * If the machine has no orders to consume, the thread will be blocked until
     * an order is added.
     *
     * If the machine has fulfilled its production quota and has no orders to
     * consume, throws {@link NoRemainingOrdersException}.
     *
     * @param consumerId The id of the consumer consuming the order.
     *
     * @return The order consumed.
     */
    public synchronized Order consumeOrder(int consumerId)
            throws NoRemainingOrdersException {
        while (numItemsInBuffer <= 0 && orderId < numToProduce) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        if (orderId >= numToProduce && numItemsInBuffer <= 0) {
            notifyAll();
            throw new NoRemainingOrdersException();
        }
        lastConsumedIdx = (lastConsumedIdx + 1) % buffer.length;
        Work.goWork(1); // work required to take order from queue.
        Order consumed = buffer[lastConsumedIdx];
        incrementWorksheet(consumerWorksheet, consumerId);
        logger.addLog(String.format("c%d gets %d from p%d",
                    consumerId, consumed.getOrderId(),
                    consumed.getProducerId()));
        numItemsInBuffer--;
        notifyAll();
        return consumed;
    }

}

