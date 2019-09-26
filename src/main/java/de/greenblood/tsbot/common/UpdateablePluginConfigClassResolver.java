package de.greenblood.tsbot.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import org.springframework.util.ClassUtils;

import java.io.IOException;

public class UpdateablePluginConfigClassResolver extends TypeIdResolverBase {
    @Override
    public String idFromValue(Object o) {

        return ClassUtils.getUserClass(o).getName();
    }

    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        return ClassUtils.getUserClass(o).getName();
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {

        try {
            Class<?> aClass = Class.forName(id);
            if (UpdateablePluginConfig.class.isAssignableFrom(aClass))
                return context.constructType(aClass);
            else {
                //todo replace with concrete exception
                throw new IllegalArgumentException("class is not plugin config " + aClass.getName());
            }
        } catch (ClassNotFoundException e) {
            //todo replace with concrete exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
