package org.cedon.kqnbot.util;

import java.nio.file.Path;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 检查目录是否存在
     * 
     * @param dir
     * @return
     */
    public static boolean isDirectoryExist(Path dir) {
        return (Files.exists(dir) && Files.isDirectory(dir));
    }

    /**
     * 检查文件是否存在
     * 
     * @param file
     * @return
     */
    public static boolean isFileExist(Path file) {
        return (Files.exists(file) && Files.isRegularFile(file));
    }
}
