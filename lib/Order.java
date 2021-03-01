package lib;

public class Order {

    private int producerId;
    private int orderId = -1;

    public Order(int producerId) {
        this.producerId = producerId;
    }

    public int getProducerId() {
        return producerId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return String.format("{ producer: %d, orderId: %d }", producerId, orderId);
    }

}

