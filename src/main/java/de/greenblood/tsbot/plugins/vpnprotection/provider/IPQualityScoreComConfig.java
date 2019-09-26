package de.greenblood.tsbot.plugins.vpnprotection.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import de.greenblood.tsbot.common.UpdateablePluginConfigClassResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component

@Validated
@JsonIgnoreProperties({"targetClass", "targetSource", "targetObject", "advisors", "frozen", "exposeProxy", "preFiltered", "proxiedInterfaces", "proxyTargetClass"})
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "class", visible = true)
@JsonTypeIdResolver(UpdateablePluginConfigClassResolver.class)
@ConfigurationProperties(prefix="ipqualityscorecomconfig")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:ipqualityscorecomconfig.yml")
public class IPQualityScoreComConfig {
    @NotNull
    private Integer timeout;
    @NotNull
    private String apiKey;
    @NotNull
    private Integer maximumAllowedFraudScore;
    private boolean useVpnDetection;
    private boolean useProxyDetection;
    @NotNull
    private Integer strictness;
    private boolean allowPublicAccessPoints;
    private boolean fastCheck;
    private boolean lighterPenalties;

    public boolean isLighterPenalties() {
        return lighterPenalties;
    }

    public void setLighterPenalties(boolean lighterPenalties) {
        this.lighterPenalties = lighterPenalties;
    }

    public boolean isFastCheck() {
        return fastCheck;
    }

    public void setFastCheck(boolean fastCheck) {
        this.fastCheck = fastCheck;
    }

    public boolean isAllowPublicAccessPoints() {
        return allowPublicAccessPoints;
    }

    public void setAllowPublicAccessPoints(boolean allowPublicAccessPoints) {
        this.allowPublicAccessPoints = allowPublicAccessPoints;
    }

    public Integer getStrictness() {
        return strictness;
    }

    public void setStrictness(Integer strictness) {
        this.strictness = strictness;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getMaximumAllowedFraudScore() {
        return maximumAllowedFraudScore;
    }

    public void setMaximumAllowedFraudScore(Integer maximumAllowedFraudScore) {
        this.maximumAllowedFraudScore = maximumAllowedFraudScore;
    }

    public boolean isUseVpnDetection() {
        return useVpnDetection;
    }

    public void setUseVpnDetection(boolean useVpnDetection) {
        this.useVpnDetection = useVpnDetection;
    }

    public boolean isUseProxyDetection() {
        return useProxyDetection;
    }

    public void setUseProxyDetection(boolean useProxyDetection) {
        this.useProxyDetection = useProxyDetection;
    }
}
