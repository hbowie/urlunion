package com.powersurgepub.urlunion;

  import java.util.*;

/**
 *
 * @author hbowie
 */
public class FavoriteSection {

  private String    title;
  private ArrayList urls = new ArrayList();
  private int level;

  public FavoriteSection (String title, int level) {
    this.title = title;
    this.level = level;
  }

  public void setTitle (String title) {
    this.title = title;
  }

  public String getTitle () {
    return title;
  }

  public void setLevel (int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  public void addURL (URLPlus url) {
    urls.add(url);
  }

  public int size() {
    return urls.size();
  }

  public URLPlus getURL(int i) {
    return (URLPlus)urls.get(i);
  }

}
