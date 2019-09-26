package de.greenblood.tsbot.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import de.greenblood.tsbot.common.UpdateablePluginConfigClassResolver;

/**
 * Created by Greenblood on 24.05.2019.
 */
@JsonIgnoreProperties({"targetClass", "targetSource", "targetObject", "advisors", "frozen", "exposeProxy", "preFiltered", "proxiedInterfaces", "proxyTargetClass"})
@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, include=JsonTypeInfo.As.PROPERTY, property="class",visible=true)
@JsonTypeIdResolver(UpdateablePluginConfigClassResolver.class)
public interface UpdateablePluginConfig<T> {
    void update(T t);
}
