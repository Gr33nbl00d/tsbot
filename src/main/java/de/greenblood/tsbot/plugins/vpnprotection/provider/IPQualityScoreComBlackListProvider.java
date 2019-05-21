package de.greenblood.tsbot.plugins.vpnprotection.provider;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

@Component
public final class IPQualityScoreComBlackListProvider implements BlackListProvider {


  private final static String API_URL = "http://www.ipqualityscore.com/api/json/ip/";
  private static final Logger logger = LoggerFactory.getLogger(IPQualityScoreComBlackListProvider.class);
  @Autowired
  private IPQualityScoreComConfig config;
  private final Gson gson = new Gson();


  private IPQualityScoreComVPNResponse getResponse(String ip) throws IOException {
    String requestUrl = this.createRequestUrl(ip);
    logger.debug("checking ip with request url: {]", requestUrl);
    String queryResult = this.query(requestUrl, this.config.getTimeout(), "Java-VPNDetection Library");
    logger.debug("query result: {]", queryResult);

    return gson.fromJson(queryResult, IPQualityScoreComVPNResponse.class);
  }

  private String createRequestUrl(String ip) {

    String options =
        "?strictness=" + config.getStrictness() +
        "&allow_public_access_points=" + config.isAllowPublicAccessPoints() +
        "&fast=" + config.isFastCheck();

    return this.API_URL + this.config.getApiKey() + "/" + ip + "/" + options;
  }


  private String query(String url, int timeout, String userAgent)
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

  private static void injectProxy() {
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
    System.setProperty("http.proxyHost", "100.200.2.145");
    System.setProperty("http.proxyPort", "821");
    System.setProperty("http.proxyUser", authUser);
    System.setProperty("http.proxyPassword", authPassword);
  }

  @Override
  public BlackListCheckResult isBlacklistedIp(String ip) {
    injectProxy();
    try {
      IPQualityScoreComVPNResponse response = getResponse(ip);
      if (response.fraudScore >= config.getMaximumAllowedFraudScore() || (response.vpn == true && config.isUseVpnDetection()) || (
          response.proxy == true && config.isUseProxyDetection())) {
        return new BlackListCheckResult(true);
      }
    } catch (IOException e) {
      logger.error("error checking blacklist status", e);
    }
    return new BlackListCheckResult(false);
  }
}