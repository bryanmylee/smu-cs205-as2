package lib;

public class Logger {

    LoggerThread thread;

    public Logger(String logFilePath) {
        thread = new LoggerThread(logFilePath);
        thread.start();
    }

    public void addLog(String toLog) {
        thread.addLog(toLog);
    }

    public void kill() {
        thread.kill();
        try {
            thread.join();
        } catch (InterruptedException e) {}
    }

}

