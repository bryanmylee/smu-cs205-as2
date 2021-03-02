package lib;

public class Work {

    private static long workPerSecond = 1;

    public static void calibrateWorkPerSecond() {
        long start = System.currentTimeMillis();
        int n = 1_000_000;
        while (n-- > 0);
        // ms/MW
        long millisPerMillionWork = System.currentTimeMillis() - start;
        // W/s
        // 1 / ms/MW = MW/ms * 1000 = MW/s * 1_000_000 = W/s
        workPerSecond = 1_000_000_000 / millisPerMillionWork;
        System.out.printf("work required: %d\n", workPerSecond);
    }

    public static void goWork(int seconds) {
        long workRequired = seconds * workPerSecond;
        while (workRequired-- > 0);
    }

}
