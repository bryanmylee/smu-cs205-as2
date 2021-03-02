package lib;

import java.util.HashMap;
import java.util.Map;

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

    public int allocateOrderId() {
        synchronized(orderId) {
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

    private int lastProduced;
    private int lastConsumed;
    private int numItemsInBuffer;

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
        incrementWorksheet(producerWorksheet, order.getProducerId());
        logger.addLog(String.format("p%d puts %d",
                    order.getProducerId(), order.getOrderId()));
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
        incrementWorksheet(consumerWorksheet, consumerId);
        logger.addLog(String.format("c%d gets %d from p%d",
                    consumerId, consumed.getOrderId(), consumed.getProducerId()));
        numItemsInBuffer--;
        notifyAll();
        return consumed;
    }

}

