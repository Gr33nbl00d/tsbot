package de.greenblood.tsbot.plugins.vpnprotection.provider;

import com.google.gson.annotations.SerializedName;

public class IPQualityScoreComVPNResponse {

  public String message;
  public boolean success;
  public boolean proxy;
  @SerializedName("ISP")
  public String internetServiceProvider;
  public String organization;
  @SerializedName("ASN")
  public String autonomousSystemNumber;
  @SerializedName("country_code")
  public String countryCode;
  public String city;
  public String region;
  @SerializedName("is_crawler")
  public boolean crawler;
  public double latitude;
  public double longtitude;
  public String timezone;
  public boolean vpn;
  public boolean tor;
  @SerializedName("recent_abuse")
  public boolean recentAbuse;
  public boolean mobile;
  @SerializedName("fraud_score")
  public int fraudScore;
  @SerializedName("operating_system")
  public int operatingSystem;
  public int browser;
  @SerializedName("device_model")
  public int deviceModel;
  @SerializedName("device_brand")
  public int deviceBrand;
}
