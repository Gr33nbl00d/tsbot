package de.greenblood.tsbot.plugins.vpnprotection.detector;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;


public final class IPQualityScoreComVPNDetector implements VpnDetector
{

    private String api_key;
    private final static String API_URL = "http://www.ipqualityscore.com/api/json/ip/";
    private int api_timeout = 5000;
    private static final Logger logger = LoggerFactory.getLogger(IPQualityScoreComVPNDetector.class);

    public IPQualityScoreComVPNDetector(String api_key)
    {
        this.api_key = api_key;
    }

    private IPQualityScoreComVPNResponse getResponse(String ip) throws IOException
    {
        String query_url = this.get_query_url(ip);
        String query_result = this.query(query_url, this.api_timeout, "Java-VPNDetection Library");
        System.out.println(query_result);
        return new Gson().fromJson(query_result, IPQualityScoreComVPNResponse.class);
    }

    private String get_query_url(String ip)
    {
        String query_url;
        if (this.api_key == null)
        {
            query_url = this.API_URL + ip;
        }
        else
        {
            query_url = this.API_URL + this.api_key + "/" + ip + "/?strictness=0&allow_public_access_points";
        }
        return query_url;
    }


    private String query(String url, int timeout, String userAgent)
            throws MalformedURLException, IOException
    {
        StringBuilder response = new StringBuilder();
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        connection.setConnectTimeout(timeout);
        connection.setRequestProperty("User-Agent", userAgent);
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream())))
        {
            while ((url = in.readLine()) != null)
            {
                response.append(url);
            }

            in.close();
        }

        return response.toString();
    }

    public static void main(String[] args) throws IOException
    {
        injectProxy();

        IPQualityScoreComVPNDetector vpnDetection = new IPQualityScoreComVPNDetector("CcwrH10kpY4PRRlXL86BpbowU72pKfi5");
        //String ip = "88.198.133.24";
        //String ip = "185.11.138.93";
        String ip = "134.119.224.141";
        IPQualityScoreComVPNResponse resp = vpnDetection.getResponse(ip);
        System.out.println(resp);
    }

    private static void injectProxy()
    {
        final String authUser = "greenblood2k";
        final String authPassword = "test2383";
        Authenticator.setDefault(
                new Authenticator()
                {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(
                                authUser, authPassword.toCharArray());
                    }
                }
        );
        System.setProperty("http.proxyHost", "100.200.2.145");
        System.setProperty("http.proxyPort", "821");
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);
    }

    @Override
    public BlackListCheckResult isBlacklistedIp(String ip)
    {
        injectProxy();
        try
        {
            IPQualityScoreComVPNResponse response = getResponse(ip);
            if (response.fraudScore > 75 || response.vpn == true || response.proxy == true)
            {
                return new BlackListCheckResult(true);
            }
        }
        catch (IOException e)
        {
            logger.error("error checking blacklist status", e);
        }
        return new BlackListCheckResult(false);
    }
}