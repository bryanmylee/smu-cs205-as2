package lib;

public class ConsumerThread extends Thread {

    private volatile Machine machine;

    private int id;

    public ConsumerThread(int id, Machine machine) {
        this.id = id;
        this.machine = machine;
    }

    @Override
    public void run() {
        while (true) {
            machine.consumeOrder(id);
        }
    }

}

