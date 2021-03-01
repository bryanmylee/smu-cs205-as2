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
        System.out.printf("%d %d %d %d\n", numToProduce, bufferSize, numProducers, numConsumers);
        Logger logger = new Logger("./log.txt");

        logger.kill();
    }
}

