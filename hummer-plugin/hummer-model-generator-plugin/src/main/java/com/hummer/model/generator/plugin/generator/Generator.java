package com.hummer.model.generator.plugin.generator;

import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于生产MBG的代码 Created by macro on 2018/4/26.
 */
public class Generator {

    @Test
    public void test() {
        String path = "/generatorConfig.xml";
        try {
            execute(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void execute(String configPath) throws Exception {
        // MBG 执行过程中的警告信息
        List<String> warnings = new ArrayList<String>();
        // 当生成的代码重复时，覆盖原代码
        boolean overwrite = true;
        // 读取我们的 MBG 配置文件
        if (configPath == null) {
            configPath = "/generatorConfig.xml";
        }
        InputStream is = getGeneratorConfigStream(configPath);
        if (is == null) {
            throw new NullPointerException("generatorConfig.xml not exist");
        }
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(is);
        is.close();

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        // 创建 MBG
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        // 执行生成代码
        myBatisGenerator.generate(null);
        // 输出警告信息
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

    private static InputStream getGeneratorConfigStream(String configPath) {
        InputStream in = getFileStreamByPath(configPath);
        if (in == null) {
            in = Generator.class.getResourceAsStream(configPath);
        }
        return in;
    }

    private static InputStream getFileStreamByPath(String configPath) {
        String rootPath = System.getProperty("user.dir");
        String path = rootPath + "/src/main/resources" + configPath;
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
