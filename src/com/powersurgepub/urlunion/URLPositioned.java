package com.powersurgepub.urlunion;

  import com.powersurgepub.psdatalib.pstags.TagsNode;

/**

 Consists of a URLPlus object and information to position it within the
 URLCollection.

 */
public class URLPositioned {

  public static final int NAVIGATE_USING_LIST = 1;
  public static final int NAVIGATE_USING_TREE = 2;

  private   URLPlus  url;
  private   int      index;
  private   TagsNode tagsNode;
  private   int      navigator = NAVIGATE_USING_LIST;
  private   boolean  newURL = true;

  public URLPositioned () {
    this.url = new URLPlus();
    this.index = -1;
    this.tagsNode = null;
  }

  public URLPositioned (URLPlus url, int index) {
    this.url = url;
    this.index = index;
    this.tagsNode = url.getTagsNode();
    newURL = false;
  }

  public void setURLPlus (URLPlus url) {
    this.url = url;
    if (url != null && url.hasURL()) {
      newURL = false;
    }
  }

  public URLPlus getURLPlus () {
    return url;
  }

  public void setIndex (int index) {
    this.index = index;
  }

  public void incrementIndex (int increment) {
    this.index = index + increment;
  }

  public boolean hasValidIndex (URLCollection urls) {
    return (index >= 0 && index < urls.size());
  }

  public int getIndex () {
    return index;
  }

  public int getIndexForDisplay () {
    return (index + 1);
  }

  public void setTagsNode (TagsNode tagsNode) {
    this.tagsNode = tagsNode;
  }

  public TagsNode getTagsNode () {
    return tagsNode;
  }

  public void setNavigator (int navigator) {
    if (navigator == NAVIGATE_USING_TREE) {
      this.navigator = NAVIGATE_USING_TREE;
    } else {
      this.navigator = NAVIGATE_USING_LIST;
    }
  }

  public void setNavigatorToList (boolean useList) {
    this.navigator =
        useList ? NAVIGATE_USING_LIST : NAVIGATE_USING_TREE;
  }

  public int getNavigator () {
    return navigator;
  }

  public boolean navigateUsingList () {
    return (navigator == NAVIGATE_USING_LIST);
  }

  public boolean navigateUsingTree () {
    return (navigator == NAVIGATE_USING_TREE);
  }

  public void setNewURL (boolean newURL) {
    this.newURL = newURL;
  }

  public boolean isNewURL () {
    return newURL;
  }

}
