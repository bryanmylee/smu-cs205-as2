import lib.ConsumerThread;
import lib.Logger;
import lib.Machine;
import lib.ProducerThread;

public class ProducersConsumers {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Invalid arguments. <N> <M> <X> <Y> required.");
            return;
        }
        int numToProduce = Integer.valueOf(args[0]);
        int bufferSize = Integer.valueOf(args[1]);
        int numProducers = Integer.valueOf(args[2]);
        int numConsumers = Integer.valueOf(args[3]);

        Logger logger = new Logger("./log.txt");
        Machine machine = new Machine(bufferSize);

        ProducerThread[] producers = new ProducerThread[numProducers];
        for (int i = 0; i < numProducers; i++) {
            producers[i] = new ProducerThread(i, machine);
            producers[i].start();
        }

        ConsumerThread[] consumers = new ConsumerThread[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new ConsumerThread(i, machine);
            consumers[i].start();
        }

        logger.kill();
    }
}

