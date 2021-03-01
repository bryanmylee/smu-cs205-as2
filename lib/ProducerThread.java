package lib;

public class ProducerThread extends Thread {

    private HotDogMachine machine;

    private int id;

    public ProducerThread(HotDogMachine machine, int id) {
        this.machine = machine;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            machine.queueOrder(new Order(id));
        }
    }

}

