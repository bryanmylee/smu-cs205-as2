import lib.HotDogMachine;
import lib.Logger;

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

        HotDogMachine machine = new HotDogMachine(numToProduce, bufferSize, numProducers, numConsumers);

        logger.kill();
    }
}

