/*
 * Copyright 2009 - 2013 Herb Bowie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.powersurgepub.urlunion;

  import com.powersurgepub.psdatalib.pstags.*;
  import com.powersurgepub.psdatalib.txbio.*;
  import com.powersurgepub.urlvalidator.*;
  import java.text.*;
  import java.util.*;

/**
  A class representing a URL and some associated metadata.
 */
public class URLPlus
    implements Comparable, Taggable, ItemWithURL {
  
  public static final boolean SLASH_TO_SEPARATE = false;
  
  public final static String   YMD_FORMAT_STRING = "yyyy-MM-dd";
  public final static String   MDY_FORMAT_STRING = "MM-dd-yyyy";
  public final static String   STANDARD_FORMAT_STRING 
      = "yyyy-MM-dd'T'HH:mm:ssz";
  public final static String   
      COMPLETE_FORMAT_STRING = "EEEE MMMM d, yyyy KK:mm:ss aa zzz";
  
  public final static DateFormat YMD_FORMAT 
      = new SimpleDateFormat (YMD_FORMAT_STRING);
  public final static DateFormat MDY_FORMAT
      = new SimpleDateFormat (MDY_FORMAT_STRING);
  public final static DateFormat COMPLETE_FORMAT
      = new SimpleDateFormat (COMPLETE_FORMAT_STRING);
  public final static DateFormat STANDARD_FORMAT
      = new SimpleDateFormat (STANDARD_FORMAT_STRING);

  private String   urlPart1 = "http://";
  private String   urlPart2 = "www.";
  private String   urlPart3 = "";
  private String   urlPart4 = "";
  private String   urlPart5 = "";
  private String   title = "";
  private String   comments = "";
  private Tags     tags = new Tags(SLASH_TO_SEPARATE);
  private Date     lastModDate;
  private int      noResponseCount = 0;
  private TagsNode tagsNode = null;

  private TagsIterator iterator = new TagsIterator (tags);

  public URLPlus () {
    setLastModDateToday();
  }
  
  /**
   Does this url have a unique key?
  
   @return True if the url has a unique key, false if not. 
  */
  public boolean hasUniqueKey() {
    return (this.hasURL() || this.getTitle().length() > 0);
  }
  
  /**
   Return the unique key identifying this url. This will be the url, if there is
   one, otherwise it will be the title. 
  
   @return The unique key. 
  */
  public String getUniqueKey() {
    if (this.hasURL()) {
      return this.getURLKey();
    } else {
      return this.getTitle();
    }
  }

  public void merge (URLPlus url2) {

    // Merge URLs
    if (url2.hasURL()) {
      setURL (url2.getURL());
    }

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
  
  /**
   Compare this URLPlus object to another, using the significant
   portions of their URLs for comparison if they have URLs, otherwise
   using their titles.
  
   @param The second object to compare to this one.
   @return A number less than zero if this object is less than the second,
           a number greater than zero if this object is greater than the second,
           or zero if the two titles are equal (ignoring case differences).
   */
  public int compareToUsingUniqueKey (Object obj2) {
    int comparison = -1;
    if (obj2.getClass().getSimpleName().equals ("URLPlus")) {
      URLPlus urlPlus2 = (URLPlus)obj2;
      comparison = this.getUniqueKey().compareToIgnoreCase
          (urlPlus2.getUniqueKey());
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
  
  public void setLastModDateStandard (String date) {
    setLastModDate (STANDARD_FORMAT, date);
  }
    
  public void setLastModDateYMD (String date) {
    setLastModDate (YMD_FORMAT, date);
  }
  
  /**
     Sets the last mod date for this item.
 
     @param  fmt  A DateFormat instance to be used to parse the following string.
     @param  date String representation of a date.
   */
  public void setLastModDate (DateFormat fmt, String date) {
    
    try {
      setLastModDate (fmt.parse (date));
    } catch (ParseException e) {
      System.out.println ("URLPlus.setLastModDate to " + date + " with " + fmt
          + " -- Parse Exception");
    }

  } // end method
  
  /**
    Sets the last mod date to today's date. 
   */
  public void setLastModDateToday () {
    setLastModDate (new GregorianCalendar().getTime());
  }
  
  /**
     Sets the due date for this item.
 
     @param  date Date representation of a date.
   */
  public void setLastModDate (Date date) {
    
    lastModDate = date;

  } // end method
  
  /**
     Gets the due date for this item, formatted as a string.
 
     @return  String representation of a date.
     @param   fmt  A DateFormat instance to be used to format the date as a string.

   */
  public String getLastModDate (DateFormat fmt) {
    
    return fmt.format (lastModDate);

  } // end method
  
  /**
     Gets the due date for this item, formatted as a string 
     in yyyy/mm/dd format.
 
     @return  String representation of a date in yyyy/mm/dd format.
   */
  public String getLastModDateYMD () {
    
    return YMD_FORMAT.format (lastModDate);

  } // end method
  
  public String getLastModDateStandard () {
    
    return STANDARD_FORMAT.format (lastModDate);
  }
  
  /**
     Gets the due date for this item.
 
     @return  date Date representation of a date.
   */
  public Date getLastModDate () {
    
    return lastModDate;

  } // end method

  public String toString () {
    return title;
    // return urlPart1 + urlPart2 + urlPart3 + urlPart4 + urlPart5;
  }

}
