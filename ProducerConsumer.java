import lib.Logger;

public class ProducerConsumer {
    public static void main(String[] args) {
        Logger logger = new Logger("./log.txt");

        Thread[] threads = new Thread[1000];
        for (int t = 0; t < threads.length; t++) {
            int tid = t;
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    logger.addLog(String.format("%d: added log %d.", tid, i));
                }
            });
            threads[t].start();
        }

        logger.kill();
    }
}

