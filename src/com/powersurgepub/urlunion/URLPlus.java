package com.powersurgepub.urlunion;

  import com.powersurgepub.psdatalib.pstags.*;
  import com.powersurgepub.psdatalib.txbio.*;
  import com.powersurgepub.urlvalidator.*;

/**
  A class representing a URL and some associated metadata.
 */
public class URLPlus
    implements Comparable, Taggable, ItemWithURL {

  private String   urlPart1 = "http://";
  private String   urlPart2 = "www.";
  private String   urlPart3 = "";
  private String   urlPart4 = "";
  private String   urlPart5 = "";
  private String   title = "";
  private String   comments = "";;
  private Tags     tags = new Tags();
  private int      noResponseCount = 0;
  private TagsNode tagsNode = null;

  private TagsIterator iterator = new TagsIterator (tags);

  public URLPlus () {

  }

  public void merge (URLPlus url2) {

    // Merge URLs
    setURL (url2.getURL());

    // Merge titles
    if (url2.getTitle().length() > getTitle().length()) {
      setTitle (url2.getTitle());
    }

    // Merge tags
    tags.merge (url2.getTags());

    // Merge comments
    if (getComments().equals(url2.getComments())) {
      // do nothing
    }
    else
    if (url2.getComments().length() == 0) {
      // do nothing
    }
    else
    if (getComments().length() == 0) {
      setComments (url2.getComments());
    } else {
      setComments (getComments() + " " + url2.getComments());
    }
  }

  public void setURL (String urlString) {

    // Look for part 1 of the url, delimited by a colon and zero or more
    // slashes (as in "http://" or "mailto:")
    urlPart1 = "";
    int i = 0;
    while (i < urlString.length()
        && i <= 8
        && urlString.charAt (i) != ':') {
      i++;
    }
    if (i < urlString.length()
        && urlString.charAt (i) == ':') {
      i++;
      while ((i) < urlString.length()
          && urlString.charAt (i) == '/') {
        i++;
      }
      urlPart1 = urlString.substring (0, i);
    } else {
      i = 0;
    }
    // Look for parts 2 and 3 of the url, including the domain name
    urlPart2 = "";
    urlPart3 = "";
    int start = i;
    int atSign = -1;
    int firstPeriod = -1;
    int periodCount = 0;
    while (i < urlString.length()
        && urlString.charAt(i) != '/') {
      if (urlString.charAt(i) == '@') {
        atSign = i;
      }
      else
      if (urlString.charAt(i) == '.') {
        periodCount++;
        if (periodCount == 1) {
          firstPeriod = i;
        } // end if first period
      } // end if a period
      i++;
    } // end while scanning for slash to end part 3
    int end = start;
    if (atSign > start) {
      end = atSign + 1;
    }
    else
    if (firstPeriod > start
        && periodCount > 1) {
      end = firstPeriod + 1;
    }
    if (end > start) {
      urlPart2 = urlString.substring (start, end);
      start = end;
    }
    urlPart3 = urlString.substring (start, i);
    // Look for part 4 of the url, following the domain name
    urlPart4 = "";
    urlPart5 = "";
    if (i < urlString.length()) {
      if (urlString.charAt(urlString.length() - 1) == '/') {
        urlPart4 = urlString.substring (i, urlString.length() - 1);
        urlPart5 = "/";
      } else {
        urlPart4 = urlString.substring (i, urlString.length());
      }
    }
    // System.out.println ("url = " + urlPart1 + " + "
    //     + urlPart2 + " + "
    //     + urlPart3 + " + "
    //     + urlPart4);
  } // end setURL method

  public String getURL () {
    return urlPart1 + urlPart2 + urlPart3 + urlPart4 + urlPart5;
  }

  public String getURLasString () {
    return urlPart1 + urlPart2 + urlPart3 + urlPart4 + urlPart5;
  }

  public String getURLPart1 () {
    return urlPart1;
  }

  public String getURLPart2 () {
    return urlPart2;
  }

  public String getURLPart3 () {
    return urlPart3;
  }

  public String getURLPart4 () {
    return urlPart4;
  }

  public String getURLPart5 () {
    return urlPart5;
  }

  public boolean hasURL () {
    return (urlPart3.length() > 0);
  }

  public boolean blankURL () {
    return (urlPart3.length() == 0);
  }

  public String getURLKey () {
    return urlPart3 + urlPart1 + urlPart2 + urlPart4;
  }

  /**
   Compare this URLPlus object to another, using the titles for comparison.
   @param The second object to compare to this one.
   @return A number less than zero if this object is less than the second,
           a number greater than zero if this object is greater than the second,
           or zero if the two titles are equal (ignoring case differences).
   */
  public int compareTo (Object obj2) {
    int comparison = -1;
    if (obj2.getClass().getSimpleName().equals ("URLPlus")) {
      URLPlus urlPlus2 = (URLPlus)obj2;
      comparison = this.getTitle().compareToIgnoreCase(urlPlus2.getTitle());
    }
    return comparison;
  }

  /**
   Compare this URLPlus object to another, using the significant
   portions of their URLs for comparison.
   @param The second object to compare to this one.
   @return A number less than zero if this object is less than the second,
           a number greater than zero if this object is greater than the second,
           or zero if the two titles are equal (ignoring case differences).
   */
  public int compareToURL (Object obj2) {
    int comparison = -1;
    if (obj2.getClass().getSimpleName().equals ("URLPlus")) {
      URLPlus urlPlus2 = (URLPlus)obj2;
      comparison = this.getURLKey().compareToIgnoreCase(urlPlus2.getURLKey());
    }
    return comparison;
  }

  public boolean equals (Object obj2) {
    boolean eq = false;
    if (obj2.getClass().getSimpleName().equals ("URLPlus")) {
      URLPlus url2 = (URLPlus)obj2;
      eq = (this.getTitle().equalsIgnoreCase (url2.getTitle()));
    }
    return eq;
  }

  public boolean equalsURL (Object obj2) {
    boolean eq = false;
    if (obj2.getClass().getSimpleName().equals ("URLPlus")) {
      URLPlus url2 = (URLPlus)obj2;
      eq = (this.getURLKey().equalsIgnoreCase (url2.getURLKey()));
    }
    return eq;
  }

  public void setTitle (String title) {
    this.title 
        = MarkupEntityTranslator.getSharedInstance().translateFromMarkup
          (title.trim());
  }

  public String getTitle () {
    return title;
  }

  public boolean equalsTitle (String title2) {
    return title.equals (title2.trim());
  }

  public void setTags (String tagString) {
    tags.set (MarkupEntityTranslator.getSharedInstance().translateFromMarkup 
          (tagString.trim()));
  }

  public void flattenTags () {
    tags.flatten();
  }

  public void lowerCaseTags () {
    tags.makeLowerCase();
  }

  public void makeTagsLowerCase() {
    tags.makeLowerCase();
  }

  public Tags getTags () {
    return tags;
  }

  public String getTagsAsString () {
    return tags.toString();
  }

  public boolean equalsTags (String tags2) {
    return tags.toString().equals (tags2.trim());
  }

  /**
   Start iteration through the list of tagsCount assigned to this item.
   */
  public void startTagIteration () {
    iterator = new TagsIterator (tags);
  }

  public String nextWord () {
    return iterator.nextWord();
  }

  public boolean isEndOfTag () {
    return iterator.isEndOfTag();
  }

  public boolean hasNextWord () {
    return iterator.hasNextWord();
  }

  public boolean hasNextTag () {
    return iterator.hasNextTag();
  }

  public String nextTag () {
    return iterator.nextTag();
  }

  public void setComments (String comments) {
    this.comments
        = MarkupEntityTranslator.getSharedInstance().translateFromMarkup
          (comments);
  }

  public String getComments () {
    return comments;
  }

  public void setNoResponseCount (int noResponseCount) {
    this.noResponseCount = noResponseCount;
  }

  public void resetNoResponseCount () {
    noResponseCount = 0;
  }

  public void incrementNoResponseCount () {
    noResponseCount++;
  }

  public int getNoResponseCount () {
    return noResponseCount;
  }
  
  public void setTagsNode (TagsNode tagsNode) {
    this.tagsNode = tagsNode;
  }

  public TagsNode getTagsNode () {
    return tagsNode;
  }

  public String toString () {
    return title;
    // return urlPart1 + urlPart2 + urlPart3 + urlPart4 + urlPart5;
  }

}
