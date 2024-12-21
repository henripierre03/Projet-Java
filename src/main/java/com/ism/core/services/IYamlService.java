package com.ism.core.services;

import java.util.Map;

public interface IYamlService {
    Map<String, Object> yamlToMap();
    Map<String, Object> yamlToMap(String path);
}
