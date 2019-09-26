package de.greenblood.tsbot.restservice;

import de.greenblood.tsbot.common.UpdatableTsBotPlugin;
import de.greenblood.tsbot.plugins.usercounter.UserCounterConfig;
import de.greenblood.tsbot.plugins.usercounter.UserCounterPluginConfig;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Collections;

@Component
public class YamlConfigStringConverter {
    public <T> String convertToYAMLString(T object, Class<T> aClass) {
        Constructor constructor = new Constructor(aClass);
        Representer representer = new AdvancedRepresenter();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        representer.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setAllowUnicode(true);
        dumperOptions.setLineBreak(DumperOptions.LineBreak.UNIX);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        //dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
        Yaml yaml = new Yaml(representer,dumperOptions);
        String s = yaml.dumpAs(object, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
        return s;
    }

    public static void main(String[] args) {
        UserCounterPluginConfig userCounterPluginConfig = new UserCounterPluginConfig();
        UserCounterConfig o = new UserCounterConfig();
        o.setChannelNameTemplate("blabla");
        o.setChannelSearchString("raba");
        o.setConfigName("hallo");
        o.setServerGroupsToCount(Collections.singletonList(5));
        o.setServerGroupsToIgnore(Collections.singletonList(6));
        userCounterPluginConfig.setUserCounterList(Collections.singletonList(o));
        YamlConfigStringConverter yamlConfigStringConverter = new YamlConfigStringConverter();
        String s = yamlConfigStringConverter.convertToYAMLString(userCounterPluginConfig,UserCounterPluginConfig.class);
        System.out.printf(s);
    }
}
