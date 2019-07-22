package de.greenblood.tsbot.common;

import de.greenblood.tsbot.TsBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greenblood on 19.07.2019.
 */
@Component
public class PluginManager {
    @Autowired
    private ApplicationContext applicationContext;
    private List<TsBotPluginInterface> tsBotPluginList = new ArrayList();
    @Autowired
    private TsBotConfig config;

    @Autowired
    private BeanUtil beanUtil;

    @PostConstruct
    private void init() {
        this.tsBotPluginList = new ArrayList<>();
        for (String pluginClassName : this.config.getTsBotPluginList()) {
            beanUtil.setApplicationContext(applicationContext);
            try {
                Object bean = beanUtil.getBean(Class.forName(pluginClassName));
                this.tsBotPluginList.add((TsBotPluginInterface) bean);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("plugin not found " + pluginClassName, e);
            }
        }
    }

    public List<TsBotPluginInterface> getTsBotPluginList() {
        return tsBotPluginList;
    }

    public List<UpdatableTsBotPlugin> getUpdatableTsBotPluginList() {
        List<UpdatableTsBotPlugin> updatableTsBotPluginList = new ArrayList<>();
        for (TsBotPluginInterface tsBotPluginInterface : this.tsBotPluginList) {
            if (tsBotPluginInterface instanceof UpdatableTsBotPlugin) {
                updatableTsBotPluginList.add((UpdatableTsBotPlugin) tsBotPluginInterface);
            }
        }
        return updatableTsBotPluginList;
    }
}
