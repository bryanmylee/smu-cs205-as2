package lib;

public class Order {

    private int producerId;
    private int orderId;

    public Order(int producerId, int orderId) {
        this.producerId = producerId;
        this.orderId = orderId;
    }

    public int getProducerId() {
        return producerId;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return String.format("{ producer: %d, orderId: %d }", producerId, orderId);
    }

}

