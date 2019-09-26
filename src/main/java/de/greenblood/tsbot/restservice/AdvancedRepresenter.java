package de.greenblood.tsbot.restservice;

import org.springframework.jdbc.datasource.IsolationLevelDataSourceAdapter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.lang.reflect.Field;

public class AdvancedRepresenter extends Representer {

    @Override
    protected NodeTuple representJavaBeanProperty(
            Object javaBean,
            Property property,
            Object propertyValue,
            Tag customTag) {
        NodeTuple nodeTuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        Node valueNode = nodeTuple.getValueNode();
        if (Tag.NULL.equals(valueNode.getTag())) {
            return null;// skip 'null' values
        }
        if (valueNode instanceof CollectionNode) {
            if (Tag.SEQ.equals(valueNode.getTag())) {
                SequenceNode seq = (SequenceNode) valueNode;
                if (seq.getValue().isEmpty()) {
                    return null;// skip empty lists
                }
            }
            if (Tag.MAP.equals(valueNode.getTag())) {
                MappingNode seq = (MappingNode) valueNode;
                if (seq.getValue().isEmpty()) {
                    return null;// skip empty maps
                }
            }
        }
        if (Tag.STR.equals(valueNode.getTag())) {

            try {
                Field f1 = valueNode.getClass().getDeclaredField("style");
                f1.setAccessible(true);
                f1.set(valueNode, DumperOptions.ScalarStyle.SINGLE_QUOTED);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        return new NodeTuple(representData(property.getName()), valueNode);
    }

}