import lib.Logger;
import lib.Machine;
import lib.Work;
import lib.actors.ConsumerThread;
import lib.actors.ProducerThread;

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

        Work.calibrateWorkPerSecond();
        Logger logger = new Logger("./log.txt");

        logger.addLog(String.format("order:%d", numToProduce));
        logger.addLog(String.format("capacity:%d", bufferSize));
        logger.addLog(String.format("makers:%d", numProducers));
        logger.addLog(String.format("packers:%d", numConsumers));

        Machine machine = new Machine(numToProduce, bufferSize, logger);

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

        for (int i = 0; i < numProducers; i++) {
            try {
                producers[i].join();
            } catch (InterruptedException e) {}
        }
        for (int i = 0; i < numConsumers; i++) {
            try {
                consumers[i].join();
            } catch (InterruptedException e) {}
        }

        logger.kill();
    }
}

