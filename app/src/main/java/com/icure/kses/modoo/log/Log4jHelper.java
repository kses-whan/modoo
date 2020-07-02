package com.icure.kses.modoo.log;

import android.content.Context;
import android.os.Environment;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;

public class Log4jHelper {
    private volatile static Log4jHelper uniqueInstance;
    private static boolean bIsWriteLog = true;
    public static String strLogParentFolderName = "Modoo";
    public static String strLogFolderName = "Logs";
    public static String strLogFileName = "kses-modoo-log.log";

    private Logger logger;

    public static void nullInstance() {
        uniqueInstance = null;
    }

    public static Log4jHelper getInstance() {
        if (uniqueInstance == null) {
            synchronized (Log4jHelper.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new Log4jHelper();

                    uniqueInstance.logger = Logger.getRootLogger();
                    String fileName = Environment.getExternalStorageDirectory()
                            + File.separator
                            + strLogParentFolderName
                            + File.separator
                            + strLogFolderName;

                    String slRootDir = Environment.getExternalStorageDirectory()
                            + File.separator
                            + strLogParentFolderName;
                    File slRootDirFile = new File(slRootDir);
                    if (!slRootDirFile.exists()) {
                        slRootDirFile.mkdir();
                    }

                    File slFileName = new File(slRootDir);
                    if (!slFileName.exists()) {
                        slFileName.mkdir();
                    }

                    File nomediaFile = new File(fileName + File.separator + ".nomedia");
                    if (nomediaFile.exists() == false) {
                        nomediaFile.mkdir();
                    }

                    String layout = "%d %-5p - %m%n";
                    String logfilename = strLogFileName;
                    String datePattern = "'.'yyyy-MM-dd ";

                    PatternLayout patternlayout = new PatternLayout(layout);
                    DailyRollingFileAppender appender;
                    try {
                        appender = new DailyRollingFileAppender(patternlayout,
                                fileName + File.separator + logfilename,
                                datePattern);
                        appender.setAppend(true);
                        appender.activateOptions();

                        uniqueInstance.logger.addAppender(appender);
                        uniqueInstance.logger.setLevel(Level.DEBUG);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return uniqueInstance;
    }

    public static String getFileName() {
        String fileName = "";

        fileName = Environment.getExternalStorageDirectory()
                + File.separator
                + strLogParentFolderName
                + File.separator
                + strLogFolderName;

        fileName += File.separator + strLogFileName;

        return fileName;
    }

    public static String GetLogDirectoryPath(Context context) {
        String dirPath = "";

        dirPath = Environment.getExternalStorageDirectory()
                + File.separator
                + strLogParentFolderName
                + File.separator
                + strLogFolderName;

        dirPath += File.separator;

        return dirPath;
    }

    public static String GetLogDirectoryPath() {
        String dirPath = "";

        dirPath = Environment.getExternalStorageDirectory()
                + File.separator
                + strLogParentFolderName
                + File.separator
                + strLogFolderName;

        return dirPath;
    }

    public static void setIsWriteLog(boolean bFlag) {
        bIsWriteLog = bFlag;
    }

    public static boolean IsWriteLog() {
        return bIsWriteLog;
    }

    public void info(String message) {
        if (bIsWriteLog == true) {
            try {
                if (uniqueInstance != null) {
                    uniqueInstance.logger.info(message + "\r");
                } else {
                    uniqueInstance = Log4jHelper.getInstance();
                    uniqueInstance.logger.info(message + "\r");
                }
            } catch (Exception e) {

            }
        }
    }

    public void error(String message) {
        if (bIsWriteLog == true) {
            try {
                if (uniqueInstance != null) {
                    uniqueInstance.logger.error(message + "\r");
                } else {
                    uniqueInstance = Log4jHelper.getInstance();
                    uniqueInstance.logger.error(message + "\r");
                }
            } catch (Exception e) {

            }
        }
    }

    public void error(String message, Throwable thr) {
        if (bIsWriteLog == true) {
            try {
                if (uniqueInstance != null) {
                    uniqueInstance.logger.error(message + "\r", thr);
                } else {
                    uniqueInstance = Log4jHelper.getInstance();
                    uniqueInstance.logger.error(message + "\r", thr);
                }
            } catch (Exception e) {

            }
        }
    }

    public void warn(String message, Throwable thr) {
        if (bIsWriteLog == true) {
            try {
                if (uniqueInstance != null) {
                    uniqueInstance.logger.warn(message + "\r", thr);
                } else {
                    uniqueInstance = Log4jHelper.getInstance();
                    uniqueInstance.logger.warn(message + "\r", thr);
                }
            } catch (Exception e) {

            }
        }
    }
}
