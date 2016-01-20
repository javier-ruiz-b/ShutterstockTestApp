package shutterstock.test.com.shutterstockapp.util;

import android.util.Log;

import shutterstock.test.com.shutterstockapp.BuildConfig;

/**
 * Created by javier on 04.01.16.
 */
public class LogHelper {

    /**
     * Enables/disables all logging messages
     */
    private static final boolean LOGGING_ENABLED = BuildConfig.DEBUG;

    /**
     * Default LOG_TAG
     */
    private static final String LOG_TAG = "ShuttApp";

    /**
     * Log event by using the default tag {@link LogHelper#LOG_TAG}
     *
     * @param message
     */
    public static void logEvent(String message) {
        logEvent(LOG_TAG, message);
    }

    /**
     * Log event by using a custom tag
     *
     * @param logTag
     * @param message
     */
    public static void logEvent(String logTag, String message) {
        if (LOGGING_ENABLED) {
            Log.d(logTag, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void logException(String logTag, String message, Throwable e) {
        if (LOGGING_ENABLED) {
            Log.e(logTag, getClassNameMethodNameAndLineNumber() + message, e);
        }
    }

    public static void logException(String message, Throwable e) {
        if (LOGGING_ENABLED) {
            Log.e(LOG_TAG, getClassNameMethodNameAndLineNumber() + message, e);
        }
    }

    public static void logException(Throwable e) {
        if (LOGGING_ENABLED) {
            Log.e(LOG_TAG, getClassNameMethodNameAndLineNumber(), e);
        }
    }

    /**
     * Gets class name of the given stack trace element without the .java suffix
     * @param element
     * @return
     */
    private static String getClassName(StackTraceElement element) {
        String fileName = element.getFileName();

        // Removing ".java" and returning class name
        return fileName.substring(0, fileName.length() - ".java".length());
    }

    private static Integer mStackTraceLevel = null;

    /**
     * Gets the stack trace of the caller method outside of this class
     *
     * @return Stack trace element
     */
    private static StackTraceElement getCallerStackTraceElement() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        //find stack trace level
        if (mStackTraceLevel == null) {
            mStackTraceLevel = 0;
            while(!elements[mStackTraceLevel].getClassName().equals(LogHelper.class.getName())) {
                mStackTraceLevel++;
            }
            while(elements[mStackTraceLevel].getClassName().equals(LogHelper.class.getName())) {
                mStackTraceLevel++;
            }
        }

        return elements[mStackTraceLevel];
    }

    /**
     * Returns the class name, method name, and line number from the currently
     * executing log call in the form <class_name>.<method_name>()-<line_number>
     *
     * @return String - String representing class name, method name, and line
     *         number.
     */
    private static String getClassNameMethodNameAndLineNumber() {
        StackTraceElement element = getCallerStackTraceElement();
        return "[" + element.getClassName() + "." +
                element.getMethodName() + "()-" +
                element.getLineNumber() + "]: ";
    }

}
