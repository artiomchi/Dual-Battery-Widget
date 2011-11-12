package org.flexlabs.widgets.dualbattery;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 16/06/11
 * Time: 19:25
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private String localPath;

    public CustomExceptionHandler(String localPath) {
        this.localPath = localPath;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stackTrace = result.toString();
        printWriter.close();

        if (localPath != null) {
            writeToFile(stackTrace, Constants.STACKTRACE_FILENAME);
        }

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stackTrace, String filename) {
        try {
            new File(localPath).mkdirs();
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath + "/" + filename));
            bos.write(stackTrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}