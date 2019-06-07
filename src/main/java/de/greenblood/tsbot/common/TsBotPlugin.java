package de.greenblood.tsbot.common;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * Created by Greenblood on 24.05.2019.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface TsBotPlugin {
}
