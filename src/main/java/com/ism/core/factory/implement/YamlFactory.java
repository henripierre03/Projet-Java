package com.ism.core.factory.implement;

import com.ism.core.services.IYamlService;
import com.ism.core.services.implement.YamlService;

public class YamlFactory {
    private YamlFactory() {
    }

    private static IYamlService yamlService;

    public static IYamlService createInstance() {
        if (yamlService == null) {
            yamlService = new YamlService();
        }
        return yamlService;
    }
}
