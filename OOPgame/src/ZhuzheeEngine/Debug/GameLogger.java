package ZhuzheeEngine.Debug;

import java.util.ArrayList;
import java.util.List;

public class GameLogger {
    private static final List<String> logs = new ArrayList<>();
    private static final int MAX_LOGS = 100;
    private static final List<LogListener> listeners = new ArrayList<>();

    public interface LogListener {
        void onLogAdded(String message);
    }

    public static void addListener(LogListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeListener(LogListener listener) {
        listeners.remove(listener);
    }

    public static void log(String message) {
        System.out.println("[GAME LOG] " + message);
        logs.add(message);
        if (logs.size() > MAX_LOGS) {
            logs.remove(0);
        }
        
        for (LogListener listener : listeners) {
            listener.onLogAdded(message);
        }
    }

    public static void logInfo(String message) {
        log("INFO: " + message);
    }

    public static void logWarning(String message) {
        log("WARNING: " + message);
    }

    public static void logError(String message) {
        log("ERROR: " + message);
    }

    public static List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public static void clear() {
        logs.clear();
    }
}
