package org.cedon.kqnbot.template;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ClassPathResource;

import org.cedon.kqnbot.util.FileUtils;

public class TemplateResourceExtractor {

    private static final Logger logger = LoggerFactory.getLogger(TemplateRender.class);

    /**
     * Map<路径, Set<文件>>
     */
    private static final Map<String, Set<String>> staticResourcePaths = new HashMap<>() {
        {
            put("static/svg/", new HashSet<>(List.of("QueryServer-Info.svg", "whitespace.svg")));
        }
    };

    public static void init() {
        File appPath = new ApplicationHome().getDir();
        logger.info("Bot运行路径为: " + appPath);
        String targetRootPath = String.valueOf(appPath);
        logger.info("正在解压静态文件到: " + targetRootPath);
        for (Map.Entry<String, Set<String>> entry : staticResourcePaths.entrySet()) {
            String resourcePath = entry.getKey();
            Set<String> fileNameList = entry.getValue();
            for (String fileName : fileNameList) {
                try {
                    Path targetPath = Paths.get(targetRootPath, resourcePath, fileName);

                    // 如果没有路径或文件就创建
                    if (!FileUtils.isFileExist(targetPath)) {
                        if (!FileUtils.isDirectoryExist(targetPath.getParent())) {
                            logger.info("目录不存在, 创建目录: " + targetPath.getParent());
                            Files.createDirectories(targetPath.getParent());
                        }
                        logger.info("文件不存在, 创建文件: " + targetPath);
                        Files.createFile(targetPath);
                    }
                    InputStream resource = new ClassPathResource(resourcePath + fileName).getInputStream();
                    logger.info("源路径为: " + resourcePath + fileName + "\t目标路径为:" + targetPath);
                    Files.copy(resource, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.error("释放静态资源时报错: " + e.getMessage());
                }
            }

        }
    }
}
