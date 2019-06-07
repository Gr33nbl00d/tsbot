package de.greenblood.tsbot.plugins.greeter;

/**
 * Created by Greenblood on 24.05.2019.
 */
public interface UpdateablePluginConfig<T> {
    void update(T t);
}
