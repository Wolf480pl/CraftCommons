package com.craftfire.commons.yaml;

import java.util.Map;

public interface YamlNodeSource {

    YamlNode getChild(String name);

    boolean hasChild(String name);

    Map<String, YamlNode> getChildren();
}
