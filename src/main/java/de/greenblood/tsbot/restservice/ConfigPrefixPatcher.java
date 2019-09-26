package de.greenblood.tsbot.restservice;

import de.greenblood.tsbot.common.UpdatableTsBotPlugin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

@Component
public class ConfigPrefixPatcher {

    public String patchConfig(Class configClass, String configString) {
        try {
            ConfigurationProperties configurationProperties = (ConfigurationProperties) configClass.getAnnotation(ConfigurationProperties.class);
            String configRootName = configurationProperties.prefix();
            StringBuffer buffer = new StringBuffer();
            buffer.append(configRootName + ":\n");

            //todo this is not nice we could get rid of the manual identation with dumperoptions from snakeyaml
            BufferedReader reader = new BufferedReader(new StringReader(configString));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append("  " + line + "\n");
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
