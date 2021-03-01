package lib;

public class ProducerThread extends Thread {

    private volatile Machine machine;

    private int id;

    public ProducerThread(int id, Machine machine) {
        this.id = id;
        this.machine = machine;
    }

    @Override
    public void run() {
        while (true) {
            machine.addOrder(new Order(id));
        }
    }

}

