package Debugger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger extends Thread {
    private static Logger instance;
    private static DateTimeFormatter dateTimeFormatter;

    private Logger() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");
    }

    public static Logger getInstance() {
        if(instance == null) {
            instance = new Logger();
            instance.start();
        }
        return instance;
    }

    public synchronized void log(Object s) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + dateTimeFormatter.format(now) + "]" + "{" + Thread.currentThread().getId() + "} "
                            + StackTracer.getInfo() + " : " + s);
    }
    
    public synchronized void log(Object s, int depth) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + dateTimeFormatter.format(now) + "]" + "{" + Thread.currentThread().getId() + "} "
                            + StackTracer.getInfo(depth) + " : " + s);
    }
}