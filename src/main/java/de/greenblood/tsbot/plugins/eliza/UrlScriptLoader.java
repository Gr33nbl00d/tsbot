package de.greenblood.tsbot.plugins.eliza;

import de.greenblood.eliza.Eliza;
import de.greenblood.eliza.ScriptDataLoader;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Greenblood on 08.04.2019.
 */
public class UrlScriptLoader implements ScriptDataLoader {
    private String scriptUrl;

    public UrlScriptLoader(String scriptUrl) {
        this.scriptUrl = scriptUrl;
    }

    @Override
    public String[] loadStrings(String script) {
        URLConnection connection = null;
        try {
            URL url = Eliza.class.getResource(scriptUrl);
            connection = url.openConnection();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        InputStream is = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        try {
            is = connection.getInputStream();
            in = new InputStreamReader(is, "windows-1252");
            br = new BufferedReader(in);
            List<String> strings = IOUtils.readLines(br);
            String[] stringsArray = new String[strings.size()];
            return strings.toArray(stringsArray);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(br,in,is);
        }
    }
}
