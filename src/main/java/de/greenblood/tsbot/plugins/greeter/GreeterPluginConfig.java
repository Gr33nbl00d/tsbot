package de.greenblood.tsbot.plugins.greeter;

import de.greenblood.tsbot.common.TsBotPlugin;
import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@ConfigurationProperties("greeterplugin")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:greeter.yml")
@Validated
public class GreeterPluginConfig implements UpdateablePluginConfig<GreeterPluginConfig>{

  private List<String> greetingMessages;

  public List<String> getGreetingMessages() {
    return greetingMessages;
  }

  public void setGreetingMessages(List<String> greetingMessages) {
    this.greetingMessages = greetingMessages;
  }

  @Override
  public void update(GreeterPluginConfig greeterPluginConfig) {
    this.greetingMessages=greeterPluginConfig.getGreetingMessages();
  }
}
