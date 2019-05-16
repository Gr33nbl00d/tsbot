package de.greenblood.tsbot.plugins.vpnprotection.detector;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

/**
 * Allows you to detect whether or not a specified IPv4 Address belongs to a
 * hosting or vpn / proxy organization.
 *
 * This class facilitates and simplifies using the web API, and allows you to
 * easily implement the functionality in your applications.
 *
 * API Homepage: https://vpnblocker.net
 *
 *
 * @author HiddenMotives
 */
public final class VPNBlockerNetVPNDetector
{

    private String api_key;
    private String api_url = "http://api.vpnblocker.net/v2/json/";
    private int api_timeout = 5000;

    public VPNBlockerNetVPNDetector() {
        this.api_key = null;
    }

    public VPNBlockerNetVPNDetector(String key) {
        this.api_key = key;
    }

    public VPNBlockerNetVPNDetector(String key, int timeout) {
        this.api_key = key;
        this.api_timeout = timeout;
    }

    /**
     * You can obtain a API key from: https://vpnblocker.net
     * (optional)
     *
     * @param key
     */
    public void set_api_key(String key) {
        this.api_key = key;
    }

    /**
     * Units are in milliseconds Allows you to set the timeout of the API
     * web request
     *
     * @param timeout
     */
    public void set_api_timeout(int timeout) {
        this.api_timeout = timeout;
    }

    /**
     * Allows you to use SSL on the API Query, you must have the appropriate
     * package from the API provider in order to use this feature.
     */
    public void useSSL() {
        this.api_url = this.api_url.replace("http://", "https://");
    }

    /**
     * Queries the API server, gets the JSON result and parses using Gson.
     *
     * @param ip
     * @return
     * @throws IOException
     */
    public VPNBlockerNetResponse getResponse(String ip) throws IOException {
        String query_url = this.get_query_url(ip);
        String query_result = this.query(query_url, this.api_timeout, "Java-VPNDetection Library");
        return new Gson().fromJson(query_result, VPNBlockerNetResponse.class);
    }

    /**
     * The Generated API Query URL
     *
     * @param ip
     * @return
     */
    public String get_query_url(String ip) {
        String query_url;
        if (this.api_key == null) {
            query_url = this.api_url + ip;
        } else {
            query_url = this.api_url + ip + "/" + this.api_key;
        }
        return query_url;
    }

    /**
     * Function that reads and returns the contents of a URL. Using the
     * specified user agent and timeout when making the URL request.
     *
     * @param url
     * @param timeout
     * @param userAgent
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public String query(String url, int timeout, String userAgent)
            throws MalformedURLException, IOException {
        StringBuilder response = new StringBuilder();
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        connection.setConnectTimeout(timeout);
        connection.setRequestProperty("User-Agent", userAgent);
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()))) {
            while ((url = in.readLine()) != null) {
                response.append(url);
            }

            in.close();
        }

        return response.toString();
    }

    public static void main(String[] args) throws IOException
    {
        final String authUser = "greenblood2k";
        final String authPassword = "test2383";
        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                authUser, authPassword.toCharArray());
                    }
                }
        );
        System.setProperty("http.proxyHost","100.200.2.145");
        System.setProperty("http.proxyPort","821");
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

        VPNBlockerNetVPNDetector vpnDetection = new VPNBlockerNetVPNDetector();
        //String ip = "88.198.133.24";
        String ip = "185.11.138.93";
        VPNBlockerNetResponse VPNBlockerNetResponse = vpnDetection.getResponse(ip);
        System.out.println(VPNBlockerNetResponse);
    }
}