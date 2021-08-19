package io.github.beastars1.cosersets.property;

public class CosProperty {
  /**
   * 保存路径
   */
  private String path;
  /**
   * 真实获取文件的域名
   */
  private String domain;
  /**
   * 真实获取文件的后缀
   */
  private String suffix;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }
}
