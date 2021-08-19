package io.github.beastars1.cosersets.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileEntity {
  /**
   * 文件或文件夹的名字
   */
  private String name;
  /**
   * 相对路径
   */
  private String path;
  /**
   * 文件大小
   */
  private long size;
  /**
   * 上传时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date time;
  /**
   * 文件类型，文件夹还是文件
   */
  private String type;
  /**
   * 文件的绝对路径
   * 文件夹为 null
   */
  private String url;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    return sdf.format(this.time);
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
