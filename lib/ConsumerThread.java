package lib;

public class ConsumerThread extends Thread {

    private HotDogMachine machine;

    private int id;

    public ConsumerThread(HotDogMachine machine, int id) {
        this.machine = machine;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            Order consumed = machine.consumeOrder();
            System.out.printf("consumer %d consumed %s\n", id, consumed);
        }
    }

}

