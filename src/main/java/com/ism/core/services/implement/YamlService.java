package com.ism.core.services.implement;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.ism.core.services.IYamlService;

public class YamlService implements IYamlService {
    private static String path = "application.yaml";

    @Override
    public Map<String, Object> yamlToMap() {
        return yamlToMap(path);
    }

    @Override
    public Map<String, Object> yamlToMap(String path) {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(path);
        return yaml.load(inputStream);
    }
}
