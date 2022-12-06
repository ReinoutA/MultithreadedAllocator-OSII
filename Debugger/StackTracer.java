package Debugger;

public class StackTracer {
    private static final int STANDARD_DEPTH = 3;

    private static int depth = STANDARD_DEPTH;

    public static int getCurrentLineNumber() {
        return Thread.currentThread().getStackTrace()[depth].getLineNumber();
    }

    public static String getCurrentClass() {
        return Thread.currentThread().getStackTrace()[depth].getClassName();
    }

    public static String getCurrentMethod() {
        return Thread.currentThread().getStackTrace()[depth].getMethodName();
    }

    public static String getInfo() {
        depth++;
        String output = getCurrentClass() + "." + getCurrentMethod() + " at line " + getCurrentLineNumber();
        depth = STANDARD_DEPTH;
        return output;
    }

    public static String getInfo(int d) {
        depth += d + 1;
        String output = getCurrentClass() + "." + getCurrentMethod() + " at line " + getCurrentLineNumber();
        depth = STANDARD_DEPTH;
        return output;
    }
}
