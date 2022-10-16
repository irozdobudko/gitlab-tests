package com.gitlab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AppPropeties {

    private static Properties config;

    static {
        config = new Properties();
        String config_fileName = "project.properties";
        String config_path = System.getProperty("user.dir") + File.separator + "src/test/resources" + File.separator + config_fileName;
        FileInputStream configProps;
        try {
            configProps = new FileInputStream(config_path);
            config.load(configProps);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String property) {
        return config.getProperty(property);
    }

}
