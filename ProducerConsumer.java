import lib.Logger;

public class ProducerConsumer {
    public static void main(String[] args) {
        Logger logger = new Logger("./log.txt");
        Thread loggerThread = new Thread(logger);
        loggerThread.start();

        for (int i = 0; i < 100; i++) {
            logger.addLog("hello world.");
            System.out.println("added log.");
        }

        System.out.println("trying to kill.");
        logger.kill();
        try {
            loggerThread.join();
        } catch (InterruptedException e) {}
    }
}

