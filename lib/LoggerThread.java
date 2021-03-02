package lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.Queue;

public class LoggerThread extends Thread {

    /**
     * @brief A reference to the file on the filesystem to write to.
     */
    private Path logFile;

    public LoggerThread(String logFilePath) {
        logFile = Paths.get(logFilePath);
        try {
            logFile.toFile().createNewFile();
        } catch (IOException e) {
            System.out.println("Failed to initialize log file");
            e.printStackTrace();
        }
    }

    /**
     * @brief A queue of logs to write to the file.
     */
    private Queue<String> buffer = new LinkedList<>();

    /**
     * @brief Add a log entry to be written to disk.
     *
     * Any thread waiting for a new log entry will be woken up.
     *
     * @param toLog The entry to log.
     */
    public synchronized void addLog(String toLog) {
        buffer.add(toLog);
        notify();
    }

    /**
     * @brief Remove a log entry from buffer.
     *
     * If there are no log entries in the buffer, the calling thread will be
     * suspended.
     *
     * The only exception is if the logger has been signalled to terminate.
     *
     * @return The removed log entry, or null if the logger is terminating and
     * no log entries exist in the buffer.
     */
    private synchronized String removeLog() {
        while (buffer.size() == 0 && !terminating) { // catch spurious wakeups.
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        return buffer.poll();
    }

    private volatile boolean terminating = false;

    public synchronized void kill() {
        terminating = true;
        notifyAll();
    }

    @Override
    public void run() {
        // read-only, no need for synchronization.
        while (!terminating) {
            String log = removeLog();
            writeLog(log);
        }
        // cleanup before exiting.
        String log;
        while ((log = removeLog()) != null) {
            writeLog(log);
        }
    }

    private void writeLog(String log) {
        if (log == null) {
            return;
        }
        try {
            Files.write(logFile, (log + "\n").getBytes(), StandardOpenOption.APPEND);
            System.out.println("logged to disk.");
        } catch (IOException e) {
            System.out.println("Failed to write to log file");
            e.printStackTrace();
        }
    }

}

