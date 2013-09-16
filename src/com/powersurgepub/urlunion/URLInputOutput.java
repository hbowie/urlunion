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

  import com.powersurgepub.psdatalib.psdata.*;
  import com.powersurgepub.psdatalib.psexcel.*;
  import com.powersurgepub.psdatalib.pstags.*;
  import com.powersurgepub.psdatalib.txbio.*;
  import com.powersurgepub.psdatalib.txbmodel.*;
  import com.powersurgepub.psdatalib.tabdelim.*;
  import com.powersurgepub.psutils.*;
  import java.io.*;
  import java.util.*;
  import org.xml.sax.*;

/**
  A class to perform open, save, close, import and export operations
  to and from disk files.
 */
public class URLInputOutput {
  
  private             URLMainFrame mainFrame;
  
  private             FavoritesPrefs favoritesPrefs = null;
  private             String      favoritesHome = "";

  private             int         favoritesColumns = 4;
  private             int         favoritesEmptyColumns = 0;
  private             String      favoritesColumnClass = "span3";

  public static final String      TAGS        = "tags";
  public static final String      FAVORITES   = "favorites";

  public static final String      BOOKMARKS   = "Bookmarks";

    /** Log used to record events. */
  private     Logger              log = Logger.getShared();

  private     boolean             okSoFar = true;
  private     int                 level = 0;
  private     StringBuffer        textOut = new StringBuffer();
  private     boolean             markupWriterOpen = false;
  private     MarkupWriter        markupWriter;

  // Used to read links from HTML
  private     HTMLFile            htmlFile;
  private     HTMLTag             htmlTag;
  
  private     DataSource          dataSource;        
  private     StringBuffer        textIn = new StringBuffer();
  private     boolean             defForTags = false;
  private     URLCollection       urls;
  private     URLPlus             url = new URLPlus();
  private     StringBuffer        tags = new StringBuffer();
  private     int                 tagsLastLevel = -1;
  private     int[]               tagStart = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

  private     boolean             demoLimitExceeded = false;

  private     FavoriteSection     favoriteSection;
  private     ArrayList           sections;

  // Used to track progress on writing HTML
  private     int                 lineCount = 0;
  private     int                 lineMax   = 30;
  private     int                 columnCount = 0;

  public URLInputOutput (URLMainFrame mainFrame) {
    this.mainFrame = mainFrame;
  }

  /* ----------------------------------------------------------------
   *
   * The following methods allow data to be read/imported
   *
   * ---------------------------------------------------------------- */

  /**
   Parse a specific html file.
   */
  public boolean read (File file, URLCollection urls) {

    FileName fileName = new FileName (file);
    boolean ok = true;
    if (fileName.getExt().equals("htm")
        || fileName.getExt().equals("html")
        || fileName.getExt().equals("xhtml")) {
      ok = readHTML (fileName, urls);
    }
    else
    if (fileName.getExt().equals("txt")
        || fileName.getExt().equals("xls")) {
      ok = readTable (fileName, urls);
    } else {
      Trouble.getShared().report("Input file name " 
          + file.getName() 
          + " does not have a recognized file extension", 
          "File Input Error");
    }
    return ok;
  }
  
  /**
   Parse a specific html file.
   */
  public boolean readHTML (FileName fileName, URLCollection urls) {

    boolean ok = true;
    demoLimitExceeded = false;
    htmlFile = new HTMLFile (fileName.toString());
    this.urls = urls;
    try {
      htmlFile.openForInput();
      htmlTag = htmlFile.readTag();
      url = new URLPlus();
      textIn = new StringBuffer();
      while (! htmlFile.isAtEnd()) {
        processHTMLTag (htmlTag);
        htmlTag = htmlFile.readTag();
      }
      addItemIfLink();
    } catch (java.io.IOException e) {
      ok = false;
      log.recordEvent (LogEvent.MEDIUM,
            "Encountered I/O error while reading HTML file " + fileName.toString()
            + e.toString(),
            false);
    }
    return ok;
  }

  private void processHTMLTag (HTMLTag htmlTag) {

    if (htmlTag.hasPrecedingText()) {
      textIn.append (htmlTag.getPrecedingText());
    }
    if (htmlTag.isEnding()) {
      // Ending Tag
      if (htmlTag.getName().equals (TextType.TITLE)) {
        // Collection Title
        if (textIn.length() > 0) {
          urls.setTitle(textIn.toString());
          textIn = new StringBuffer();
        }
      }
      else
      if (htmlTag.isHeadingTag() && htmlTag.getHeadingLevel() == 1) {
        // Collection Title
        urls.setTitle(textIn.toString());
        textIn = new StringBuffer();
      }
      else
      if (htmlTag.isHeadingTag() && htmlTag.getHeadingLevel() > 1) {
        addItemIfLink();
        while (tagsLastLevel >= 0
            && tagsLastLevel >= htmlTag.getListLevel()) {
          tags.delete(tagStart[tagsLastLevel], tags.length());
          tagStart[tagsLastLevel] = 0;
          tagsLastLevel--;
        }
        tagStart [htmlTag.getListLevel()] = tags.length();
        if (tags.length() > 0) {
          tags.append(".");
        } // end if appending to higher-level tags
        tags.append(textIn);
        textIn = new StringBuffer();
        tagsLastLevel = htmlTag.getListLevel();
      }
      else
      if (htmlTag.getName().equals (TextType.HEAD)) {
        textIn = new StringBuffer();
      }
      else
      if (htmlTag.getName().equals (TextType.HTML_LINK)) {
        // URL Title
        if (textIn.length() > 0) {
          url.setTitle (textIn.toString());
          textIn = new StringBuffer();
        }
      }
      else
      if (htmlTag.getName().equals (TextType.DEFINITION_DEF)) {
        // Tags or Comments
        if (defForTags) {
          url.setTags (textIn.toString());
        } else {
          String trimmedComments = textIn.toString().trim();
          if (trimmedComments.length() == 0
              || trimmedComments.equalsIgnoreCase("missing value")) {
            // do nothing
          } else {
            url.setComments (trimmedComments);
          }
        }
        textIn = new StringBuffer();
      }
    } else {
      // Beginning Tag
      if (htmlTag.getName().equals (TextType.HTML_LINK)) {
        // Get URL itself
        HTMLAttribute attr = htmlTag.getAttribute (TextType.HREF);
        if (attr != null) {
          addItemIfLink();
          url.setURL (attr.getValue());
          HTMLAttribute attr2 = htmlTag.getAttribute (TAGS);
          boolean tagsAttrFound = false;
          if (attr2 != null) {
            String tagsAttr = attr2.getValue();
            if (tagsAttr != null && tagsAttr.length() > 0) {
              tagsAttrFound = true;
              url.setTags (tagsAttr);
            }
          }
          if ((! tagsAttrFound) && tags.length() > 0 && tagsLastLevel >= 0) {
            url.setTags (tags.toString());
          }
        }
      } // end if link
      else
      if (htmlTag.getName().equals (TextType.DEFINITION_DEF)) {
        HTMLAttribute attr = htmlTag.getAttribute (TextType.CLASS);
        defForTags = (attr != null && attr.getValue().equals (TAGS));
      } // end if DD tag
    } // end if beginning tab
  } // end method processHTMLTag
  
  /**
   Parse a specific tabular file.
   */
  public boolean readTable (FileName fileName, URLCollection urls) {

    boolean ok = true;
    demoLimitExceeded = false;
    if (fileName.getExt().equals("xls")) {
      dataSource = new ExcelFile(fileName.toString());
    } else {
      dataSource = new TabDelimFile (fileName.toString());
    }
    this.urls = urls;
    try {
      dataSource.openForInput();
      url = new URLPlus();
      DataRecord row;
      DataField column;
      while (! dataSource.isAtEnd()) {
        row = dataSource.nextRecordIn();
        if (row != null) {
          url = new URLPlus();
          row.startWithFirstField();
          while (row.hasMoreFields()) {
            column = row.nextField();
            if (column != null) {
              if (column.getCommonFormOfName().contains("title")
                  && url.getTitle().length() == 0) {
                url.setTitle(column.getData());
              }
              else
              if (column.getCommonFormOfName().contains("url")
                  && url.blankURL()) {
                url.setURL(column.getData());
              }
              else
              if (column.getCommonFormOfName().contains("tags")
                  && url.getTagsAsString().length() == 0) {
                url.setTags(column.getData());
              }
              else
              if ((column.getCommonFormOfName().contains("comments")
                  || column.getCommonFormOfName().contains("notes"))
                  && (url.getComments().length() == 0)) {
                url.setComments(column.getData());
              }
            } // end if column not null
          } // end while row has more columns to consider
          if (url.hasUniqueKey()) {
            urls.add(url);
          }
        } // end if row not null
      } // end while data source has more records
      
    } catch (java.io.IOException e) {
      ok = false;
      log.recordEvent (LogEvent.MEDIUM,
            "Encountered I/O error while reading HTML file " + fileName.toString()
            + e.toString(),
            false);
    }
    return ok;
  }

  private void addItemIfLink () {
    if (url.hasURL()) {
      urls.add (url);
      url = new URLPlus();
    }
  }
  
  public void setDemoLimitExceeded(boolean demoLimitExceeded) {
    this.demoLimitExceeded = demoLimitExceeded;
  }

  public boolean wasDemoLimitExceeded() {
    return demoLimitExceeded;
  }

  /* ----------------------------------------------------------------
   *
   * The following methods allow data to be saved/exported
   *
   * ---------------------------------------------------------------- */


  /**
   Save an entire URL collection to a specified output file.
   */
  public boolean save (File file, URLCollection urls) {

    markupWriter = new MarkupWriter (file, MarkupWriter.HTML_FORMAT);
    this.urls = urls;
    okSoFar = markupWriter.openForOutput();
    level = 0;
    startFile(urls.getTitle(), false, false);
    writeStartTag (TextType.DEFINITION_LIST);


    // Now write the collection
    if (okSoFar) {
      URLPlus url;
      for (int i = 0; i < urls.size(); i++) {
        url = urls.get (i);
        saveNextURLToFile (url);
      } // end for loop
    } // end if open okSoFar

    if (okSoFar) {
      writeEndTag (TextType.DEFINITION_LIST);
      endFile();
      okSoFar = closeOutput();
    }

    return okSoFar;
  }

  /**
   Write the next item to the passed output file. Assume that the opening
   and closing of the output file, along with writing of headers,
   is being handled elsewhere.
   */
  private void saveNextURLToFile (URLPlus nextURL) {

    // Create file content
    writeStartTag (TextType.DEFINITION_TERM);
    beginLink(nextURL.getURL());;
    writeContent(nextURL.getTitle());
    endLink();
    writeEndTag   (TextType.DEFINITION_TERM);

    if (nextURL.getTagsAsString().length() > 0) {
      writeStartTag (TextType.DEFINITION_DEF, TAGS);
      writeContent  (nextURL.getTagsAsString());
      writeEndTag   (TextType.DEFINITION_DEF);
    }

    writeStartTag (TextType.DEFINITION_DEF);
    writeContent (nextURL.getComments());
    writeEndTag   (TextType.DEFINITION_DEF);

  }
  
  /**
   Export the urls to a tab-delimited file. 
  
   @param urls    The collection of urls to be output. 
   @param urlsTab The file to be written.
   @param favoritesOnly Do we only want favorites, or everything?
   @param favoritesTagsString The string containing the tag(s) identifying
                              the user's favorites. 
  
   @return True if everything went ok; false if an I/O error. 
  */
  public boolean exportToTabDelimited (
      URLCollection urls, 
      File urlsTab, 
      boolean favoritesOnly, 
      String favoritesTagsString) {
    
    this.urls = urls;
    TagsModel tree = urls.getTagsModel();
    boolean ok = true;
    TabDelimFile tdf = new TabDelimFile (urlsTab);
    RecordDefinition recDef = new RecordDefinition();
    recDef.addColumn ("Title");
    recDef.addColumn ("URL");
    recDef.addColumn ("Tags");
    recDef.addColumn ("Comments");
    try {
      tdf.openForOutput (recDef);
    } catch (java.io.IOException e) {
      reportExportTrouble();
      ok = false;
    }
    
    ArrayList favoritesTagsList;
    if (favoritesOnly) {
      favoritesTagsList = getFavoritesList(favoritesTagsString);
    } else {
      favoritesTagsList = new ArrayList();
    }

    if (ok) {
      DataRecord rec;
      for (int i = 0; i < urls.size(); i++) {
        url = urls.get(i);
        if (url != null) {
          boolean favoritesFound = false;
          StringBuilder favoritesCategory = new StringBuilder();
          if (favoritesOnly) {
            TagsIterator iterator = new TagsIterator (url.getTags());
            String word;
            int level = 0;
            boolean favoritesCategoryBuilt = false;
            favoritesCategory = new StringBuilder();
            while (iterator.hasNextWord() && (! favoritesCategoryBuilt)) {
              word = iterator.nextWord();
              if (word != null && word.length() > 0) {
                if (level == 0) {
                  String tag;
                  int j = 0;
                  favoritesFound = false;
                  while (j < favoritesTagsList.size() && (! favoritesFound)) {
                    tag = (String)favoritesTagsList.get(j);
                    if (tag.equalsIgnoreCase(word)) {
                      favoritesFound = true;
                    } else {
                      j++;
                    }
                  } // end while searching list of favorites tags
                } else {
                  if (favoritesFound) {
                    if (favoritesCategory.length() > 0) {
                      favoritesCategory.append(Tags.PREFERRED_LEVEL_SEPARATOR);
                    }
                    favoritesCategory.append(word);
                    if (iterator.isEndOfTag()) {
                      favoritesCategoryBuilt = true;
                    }
                  }
                }
                if (iterator.isEndOfTag()) {
                  level = 0;
                } else {
                  level++;
                }
              } // end if valid word
            } // end while more words in tag
          } // end if favorites only
          boolean writeRec = true;
          if (favoritesOnly) {
            writeRec = favoritesFound;
          }
          if (writeRec) {
            rec = new DataRecord();
            rec.addField(recDef, url.getTitle());
            rec.addField(recDef, url.getURL());
            if (favoritesOnly) {
              rec.addField(recDef, favoritesCategory.toString());
            } else {
              rec.addField(recDef, url.getTagsAsString());
            }
            rec.addField(recDef, url.getComments());
            try {
              tdf.nextRecordOut(rec);
            } catch (java.io.IOException e) {
              reportExportTrouble();
              ok = false;
            } // end exception processing
          }
        } // end if url not null
      } // end for each url
    } // end if output file opened successfully
    
    if (ok) {
      try {
        tdf.close();
      } catch (java.io.IOException e) {
        reportExportTrouble();
        ok = false;
      }
    }

    return ok;
  }
  
  private void reportExportTrouble() {
    Trouble.getShared().report
        ("I/O Problems encountered during export", "I/O Error");
  }

  /**
   Create an HTML file containing all the bookmarks tagged with "Favorites".

   @param file The output file to be written.
   @param urls The collection of URLs containing the favorites.
   @param favoritesColumns The number of columns to use on the Favorites page.
   @param favoritesRows The maximum number of rows to print per column.
   @return True if everything went ok and the file was written successfully,
           false if i/o errors writing the file, or if no favorites were found.
   */
  public boolean publishFavorites
      (File publishTo, URLCollection urls, FavoritesPrefs favoritesPrefs) {

    this.favoritesPrefs = favoritesPrefs;
    favoritesColumns = favoritesPrefs.getFavoritesColumns();
    int favoritesRows    = favoritesPrefs.getFavoritesRows();
    String favoritesTags = favoritesPrefs.getFavoritesTags();
    favoritesHome = favoritesPrefs.getFavoritesHome();
              
    this.urls = urls;
    TagsModel tree = urls.getTagsModel();
    
    lineMax = favoritesRows;
    switch (favoritesColumns) {
      case 1:
        favoritesEmptyColumns = 0;
        favoritesColumnClass = "span12";
        break;
      case 2:
        favoritesEmptyColumns = 0;
        favoritesColumnClass = "span6";
        break;
      case 3:
        favoritesEmptyColumns = 0;
        favoritesColumnClass = "span4";
        break;
      case 4:
        favoritesEmptyColumns = 0;
        favoritesColumnClass = "span3";
        break;
      case 5:
        favoritesEmptyColumns = 1;
        favoritesColumnClass = "span2";
        break;
      case 6:
        favoritesEmptyColumns = 0;
        favoritesColumnClass = "span2";
        break;
      default:
        favoritesEmptyColumns = 0;
        favoritesColumnClass = "span3";
    }
    
    ArrayList favoritesTagsList = getFavoritesList(favoritesTags);

    boolean favoritesFound = false;
    boolean inFavorites = false;
    int favoritesLevel = -1;
    int listLevel = -1;
    okSoFar = true;
    markupWriterOpen = false;

    TagsNode onDeckCircle = tree.getRoot();
    TagsNode node = null;
    boolean noMoreSiblings = true;
    int level = 0;
    File file = null;
    String tag = "";
    while (onDeckCircle != null && okSoFar) {
      node = onDeckCircle;
      level = node.getLevel();
      switch (node.getNodeType()) {
        case TagsNode.ROOT:
          // Don't need to do anything with the root node
          break;
        case TagsNode.TAG:
          // A tag, or folder within which bookmarks are stored
          if (inFavorites && (level <= favoritesLevel)) {
            // Beyond Favorites
            writeFavorites(file);
            inFavorites = false;
          }
          if (inFavorites) {
            // Favorites sub-folder
            favoriteSection = new FavoriteSection
                (node.toString(), level - favoritesLevel);
            sections.add(favoriteSection);
            listLevel = level;
          } else {
            String nodeTag = node.toString();
            tag = "";
            int i = 0;
            boolean found = false;
            while (i < favoritesTagsList.size() && (! found)) {
              tag = (String)favoritesTagsList.get(i);
              if (tag.equalsIgnoreCase(nodeTag)) {
                found = true;
              } else {
                i++;
              }
            }
            if (found) {
              // Found Favorites
              favoritesFound = true;
              inFavorites = true;
              favoritesLevel = level;
              sections = new ArrayList();
              file = new File (publishTo, nodeTag.toLowerCase() + ".html");
              markupWriter = new MarkupWriter (file, MarkupWriter.HTML_FORMAT);
              markupWriterOpen = true;
              startFavorites(node.toString());
            }
          }
          break;
        case TagsNode.ITEM:
          if (inFavorites) {
            // We've found a favorite url
            url = (URLPlus)node.getUserObject();
            favoriteSection.addURL(url);
          }
          break;
        default:
          break;
      }

      onDeckCircle = tree.getNextNode (node);
    } // end while more nodes

    writeFavorites(file);

    return (okSoFar && favoritesFound);
  }
  
  /**
   Build a list of favorites tags specified by the user. 
  
   @param favoritesTagsString The string containing the specified tag(s).
  
   @return A list of the specified tags. 
  */
  private ArrayList getFavoritesList(String favoritesTagsString) {
    Tags favoritesTags = new Tags(favoritesTagsString);
    ArrayList favoritesTagsList = new ArrayList();
    int t = 0;
    boolean more = true;
    String tag = "?";
    while (tag.length() > 0) {
      tag = favoritesTags.getTag(t);
      if (tag.length() > 0) {
        favoritesTagsList.add(tag);
        t++;
      } 
    }
    return favoritesTagsList;
  }
  
  private void writeFavorites(File file) {
    if (markupWriterOpen) {
      for (int i = 0; i < sections.size(); i++) {
        favoriteSection = (FavoriteSection)sections.get(i);
        writeFavoriteHeading (
            favoriteSection.getTitle(),
            favoriteSection.getLevel(),
            favoriteSection.size() + 2);
        for (int j = 0; j < favoriteSection.size(); j++) {
          URLPlus fave = favoriteSection.getURL(j);
          writeFavorite (fave.getTitle(), fave.getURL());
        }
      }
      finishFavorites(file);
      markupWriterOpen = false;
    }
  }

  /**
   Start the favorites output file.
   @param suffix String to be appended to the title.
   */
  private void startFavorites (String suffix) {
    markupWriter.openForOutput();
    startFile (urls.getTitle() + " | " + suffix, true, false);

    startDiv ("navbar navbar-inverse navbar-fixed-top");
    startDiv ("navbar-inner");
    startDiv ("container");
    beginStartTag  (TextType.ANCHOR);
    addAttribute   (TextType.CLASS, "btn btn-navbar");
    addAttribute   (TextType.DATA_TOGGLE, "collapse");
    addAttribute   (TextType.DATA_TARGET, ".nav-collapse");
    finishStartTag (TextType.ANCHOR);

    beginStartTag  (TextType.SPAN);
    addAttribute   (TextType.CLASS, "icon-bar");
    finishStartTag (TextType.SPAN);
    writeEndTag    (TextType.SPAN);

    beginStartTag  (TextType.SPAN);
    addAttribute   (TextType.CLASS, "icon-bar");
    finishStartTag (TextType.SPAN);
    writeEndTag    (TextType.SPAN);

    beginStartTag  (TextType.SPAN);
    addAttribute   (TextType.CLASS, "icon-bar");
    finishStartTag (TextType.SPAN);
    writeEndTag    (TextType.SPAN);

    writeEndTag    (TextType.ANCHOR);

    FileName favoritesFile = new FileName (markupWriter.getDestination());
    beginLink(favoritesFile.getFileName(), "brand");
    writeContent(urls.getTitle() + " | " + suffix);
    endLink();
    startDiv ("nav-collapse collapse");
    writeStartTag  (TextType.UNORDERED_LIST, "nav");
    if (favoritesHome.length() > 0) {
      writeNavLink   ("active", favoritesHome, "Home");
    }
    writeNavLink   ("", "urlunion.html", "List");
    writeNavLink   ("", "bookmark.html", "Netscape");
    writeNavLink   ("", "outline.html",  "Outline");
    writeEndTag   (TextType.UNORDERED_LIST);
    endDiv();
    endDiv();
    endDiv();
    endDiv();
    
    startDiv("container");
    startDiv("row");
    if (favoritesEmptyColumns > 0) {
      startDiv("span1");
      endDiv(); // end column
    }
    startDiv(favoritesColumnClass);
    
    columnCount = 0;
    lineCount = 0;
    // writeFavoriteHeading ("Bookmarks", 1);
    // writeFavorite ("All in Outline", "outline.html");
  }
  
  private void writeNavLink(String klass, String link, String content) {
    writeStartTag (TextType.LIST_ITEM, klass);
    beginLink (link);
    writeContent(content);
    endLink();
    writeEndTag(TextType.LIST_ITEM);
  }

  /**
   Write a heading for a new Favorites category of bookmarks.
  
   @param title The title of the category.
   @param headingLevel The heading level of the category.
   */
  private void writeFavoriteHeading (
      String title,
      int headingLevel,
      int linesInSection) {
    if ((lineCount + linesInSection) >lineMax) {
      startNewColumn();
    }
    if (headingLevel > 6) {
      headingLevel = 6;
    }
    beginHeading (headingLevel, FAVORITES);
    writeContent  (title);
    endHeading (headingLevel);
    lineCount = lineCount + 2;
  }

  /**
   Write a favorite bookmark.
  
   @param title The title of the bookmark.
   @param link  The actual URL.
   */
  private void writeFavorite (String title, String link) {
    if (lineCount > lineMax) {
      startNewColumn();
    }
    writeStartTag (TextType.PARAGRAPH, FAVORITES);
    beginLink(link);;
    writeContent(title);
    endLink();
    writeEndTag (TextType.PARAGRAPH);
    lineCount++;
  }

  /**
   Finish off the favorites page
  
   @param file The file being produced.
   */
  private void finishFavorites (File file) {
    while (columnCount < (favoritesColumns - 1)) {
      startNewColumn();
    }
    if (favoritesEmptyColumns > 0) {
      endDiv(); // end column
      startDiv("span1");
    }
    endDiv(); // column
    endDiv(); // row
    endDiv(); // container
    
    endFile();
    okSoFar = closeOutput();
    Logger.getShared().recordEvent (LogEvent.NORMAL,
          "Favorites published to " + file.toString(),
            false);
  }

  /**
   Start a new column on the favorites page.
   */
  private void startNewColumn () {

    endDiv(); // end column
    columnCount++;
    if (columnCount >= favoritesColumns) {
      if (favoritesEmptyColumns > 0) {
        startDiv("span1");
        endDiv(); // end column
      }
      endDiv(); // end row
      startDiv("row");
      columnCount = 0;
      if (favoritesEmptyColumns > 0) {
        startDiv("span1");
        endDiv(); // end column
      }
    } 
    startDiv(favoritesColumnClass);

    lineCount = 0;
  }

  /**
   Publish the collection as an outline in xoxo format, using the tags
   as folders.

   @param file The output file to be created.
   @param urls The URL Collection to be written.
   @return True if everything went ok.
   */
  public boolean publishOutline
      (File file, URLCollection urls) {

    this.urls = urls;
    TagsModel tree = urls.getTagsModel();
    markupWriter = new MarkupWriter
        (file, MarkupWriter.HTML_FORMAT);
    startOutline();

    /* File insertFile
        = new File (file.getParent(), "urlunion/outline_head_insert.html");
    markupWriter.insertFile (insertFile); */

    markupWriter.startUnorderedList("id", "ol1");

    int lastLevel = -1;
    okSoFar = true;

    TagsNode onDeckCircle = tree.getRoot();
    TagsNode node = null;
    boolean noMoreSiblings = true;
    int level = 0;
    while (onDeckCircle != null && okSoFar) {
      node = onDeckCircle;
      level = node.getLevel();
      while (lastLevel > level) {
        markupWriter.endUnorderedList();
        markupWriter.endListItem();
        lastLevel--;
      }
      switch (node.getNodeType()) {
        case TagsNode.ROOT:
          // Don't need to do anything with the root node
          break;
        case TagsNode.TAG:
          // A tag, or folder within which bookmarks are stored
          markupWriter.startListItem (TextType.NO_STYLE);
          markupWriter.writeTextForMarkup (node.toString());
          markupWriter.startUnorderedList("");
          lastLevel = level;
          break;
        case TagsNode.ITEM:
          // A bookmark
          url = (URLPlus)node.getUserObject();
          markupWriter.startListItem (TextType.NO_STYLE);
          markupWriter.startLink (url.getURL());
          markupWriter.writeTextForMarkup (url.getTitle());
          markupWriter.endLink (url.getURL());
          markupWriter.endListItem();
          lastLevel = level;
          break;
        default:
          break;
      }

      onDeckCircle = tree.getNextNode (node);
    } // end while more nodes

    while (lastLevel > 1) {
      markupWriter.endUnorderedList();
      markupWriter.endListItem();
      lastLevel--;
    }
    markupWriter.endUnorderedList();
    endDiv(); // column
    endDiv(); // row
    endDiv(); // container
    endFile();
    okSoFar = closeOutput();
    Logger.getShared().recordEvent (LogEvent.NORMAL,
          "Tags Outline published to " + file.toString(),
            false);

    return okSoFar;
  }
  
  /**
   Start the outline output file.
  
   @param suffix String to be appended to the title.
   */
  private void startOutline () {
    markupWriter.openForOutput();
    startFile (urls.getTitle() + " | Outline", true, true);

    startDiv ("navbar navbar-inverse navbar-fixed-top");
    startDiv ("navbar-inner");
    startDiv ("container");
    beginStartTag  (TextType.ANCHOR);
    addAttribute   (TextType.CLASS, "btn btn-navbar");
    addAttribute   (TextType.DATA_TOGGLE, "collapse");
    addAttribute   (TextType.DATA_TARGET, ".nav-collapse");
    finishStartTag (TextType.ANCHOR);

    beginStartTag  (TextType.SPAN);
    addAttribute   (TextType.CLASS, "icon-bar");
    finishStartTag (TextType.SPAN);
    writeEndTag    (TextType.SPAN);

    beginStartTag  (TextType.SPAN);
    addAttribute   (TextType.CLASS, "icon-bar");
    finishStartTag (TextType.SPAN);
    writeEndTag    (TextType.SPAN);

    beginStartTag  (TextType.SPAN);
    addAttribute   (TextType.CLASS, "icon-bar");
    finishStartTag (TextType.SPAN);
    writeEndTag    (TextType.SPAN);

    writeEndTag    (TextType.ANCHOR);

    FileName outlineFile = new FileName (markupWriter.getDestination());
    beginLink(outlineFile.getFileName(), "brand");
    writeContent(urls.getTitle() + " | Outline");
    endLink();
    startDiv ("nav-collapse collapse");
    writeStartTag  (TextType.UNORDERED_LIST, "nav");
    if (favoritesHome.length() > 0) {
      writeNavLink   ("active", favoritesHome, "Home");
    }
    writeNavLink   ("", "urlunion.html", "List");
    writeNavLink   ("", "bookmark.html", "Netscape");
    writeNavLink   ("", "favorites.html",  "Favorites");
    writeEndTag   (TextType.UNORDERED_LIST);
    endDiv();
    endDiv();
    endDiv();
    endDiv();
    
    startDiv(TextType.CONTAINER);
    startDiv(TextType.ROW);
    startDiv(TextType.SPAN12);
    
  }

  /**
   Create an HTML file containing all the bookmarks in the traditional
   Netscape bookmarks format.

   @param file The output file to be written.
   @param urls The collection of URLs containing the favorites.
   @return True if everything went ok, false if i/o errors writing the file.
   */
  public boolean publishNetscape
      (File file, URLCollection urls) {

    this.urls = urls;
    TagsModel tree = urls.getTagsModel();
    markupWriter = new MarkupWriter
        (file, MarkupWriter.NETSCAPE_BOOKMARKS_FORMAT);
    markupWriter.setIndentPerLevel(4);
    markupWriter.openForOutput();
    markupWriter.writeLine ("<Title>Bookmarks</Title>");
    markupWriter.writeLine ("<H1>Bookmarks</H1>");
    markupWriter.startXML("DL", "", true, false, false);
    markupWriter.startXML("p", "", false, true, false);
    // markupWriter.moreIndent();

    int lastLevel = -1;
    okSoFar = true;

    TagsNode onDeckCircle = tree.getRoot();
    TagsNode node = null;
    boolean noMoreSiblings = true;
    int level = 0;
    while (onDeckCircle != null && okSoFar) {
      node = onDeckCircle;
      level = node.getLevel();
      while (lastLevel > level) {
        markupWriter.endDefinitionList();
        markupWriter.startParagraph (TextType.NO_STYLE, false);
        markupWriter.lessIndent();
        lastLevel--;
      }
      switch (node.getNodeType()) {
        case TagsNode.ROOT:
          // Don't need to do anything with the root node
          break;
        case TagsNode.TAG:
          // A tag, or folder within which bookmarks are stored
          markupWriter.startXML("DT", "", true, false, false);
          markupWriter.startXML (
              TextType.HEADING_3.toUpperCase(),
              TextType.FOLDED.toUpperCase(), "",
              false, false, false);
          markupWriter.write (node.toString());
          markupWriter.endXML (
              TextType.HEADING_3.toUpperCase(),
              false, true);
          markupWriter.startXML("DL", "", true, false, false);
          markupWriter.startXML("p", "", false, true, false);
          // markupWriter.moreIndent();
          lastLevel = level;
          break;
        case TagsNode.ITEM:
          // A bookmark
          url = (URLPlus)node.getUserObject();
          markupWriter.startXML("DT", "", true, false, false);
          markupWriter.writeLink (url.getTitle(), url.getURL());
          lastLevel = level;
          break;
        default:
          break;
      }

      onDeckCircle = tree.getNextNode (node);
    } // end while more nodes

    while (lastLevel > 1) {
      markupWriter.endDefinitionList();
      // markupWriter.startParagraph (NO_STYLE, false);
      // markupWriter.lessIndent();
      lastLevel--;
    }
    markupWriter.endDefinitionList();
    okSoFar = closeOutput();
    Logger.getShared().recordEvent (LogEvent.NORMAL,
          "Netscape bookmarks published to " + file.toString(),
            false);

    return okSoFar;
  }


  public boolean publishIndex (File indexFile, File urlFile,
      boolean favoritesWritten, String favoritesFileName,
      boolean netscapeWritten, String netscapeFileName,
      boolean xoxoWritten, String xoxoFileName) {

    markupWriter = new MarkupWriter (indexFile, MarkupWriter.HTML_FORMAT);
    okSoFar = markupWriter.openForOutput();
    startFile(urls.getTitle() + " | " + "Index", false, false);
    writeStartTag (TextType.PARAGRAPH);
    writeContent ("This folder <cite>("
        + indexFile.getParent()
        + ")</cite> is maintained by URL Union. "
        + "The folder contains a number of files representing your collection "
        + "of URLs in a variety of different formats.");
    writeEndTag (TextType.PARAGRAPH);
    writeStartTag (TextType.DEFINITION_LIST);

    publishIndexLink ("List of all URLS", urlFile.getName(),
        "A complete list of your URL collection, sequenced by domain name. "
        + "This is the native format used by URL Union to store your URLs.");

    if (favoritesWritten) {
      publishIndexLink ("Favorites", favoritesFileName,
          "A single page containing all your URLs tagged as Favorites");
    }

    if (netscapeWritten) {
      publishIndexLink ("Netscape Bookmarks", netscapeFileName,
          "All of your URLS formatted in the time-honored Netscape format, "
          + "suitable for import to most Web browsers.");
    }

    if (xoxoWritten) {
      publishIndexLink ("Dynamic HTML Outline", xoxoFileName,
          "All of your URLs written in a dynamic HTML outline format, "
          + "with each category represented as a node whose contents can "
          + "be hidden or revealed.");
    }

    if (okSoFar) {
      writeEndTag (TextType.DEFINITION_LIST);
      endFile();
      okSoFar = closeOutput();
      Logger.getShared().recordEvent (LogEvent.NORMAL,
          "Index published to " + indexFile.toString(),
            false);
    }

    return okSoFar;
  }

  private void publishIndexLink (String title, String link, String description) {

    // Create file content
    writeStartTag (TextType.DEFINITION_TERM);
    beginLink(link);;
    writeContent(title);
    endLink();
    writeEndTag   (TextType.DEFINITION_TERM);

    writeStartTag (TextType.DEFINITION_DEF);
    writeContent  (description);
    writeEndTag   (TextType.DEFINITION_DEF);

  }

  private void startFile (String title, boolean bootstrap, boolean outliner) {

    // writeStartTag (HTML);
    writeStartTag (TextType.HEAD);
    writeMetadata("charset", "UTF-8");
    writeStartTag (TextType.TITLE);
    writeContent (title);
    writeEndTag (TextType.TITLE);
    if (bootstrap) {
      writeMetadataNameAndContent 
          ("viewport", "width=device-width, initial-scale=1.0");
    }
    writeMetadataNameAndContent ("generator", Home.getShared().getProgramName() + " "
        + Home.getShared().getProgramVersion());
    if (bootstrap) {
      writeStyleSheetLink ("bootstrap/css/bootstrap.css");
      beginStartTag (TextType.STYLE);
      addAttribute (TextType.TYPE, TextType.TEXT_CSS);
      finishStartTag(TextType.STYLE);
      writeLine ("      body {");
      writeLine ("        padding-top: 50px;");
      writeLine ("        padding-bottom: 40px;");
      writeLine ("      }");
      writeEndTag (TextType.STYLE);
      writeStyleSheetLink ("bootstrap/css/bootstrap-responsive.css");
      writeScriptSrc ("javascript/jquery.js");
      writeScriptSrc ("bootstrap/js/bootstrap.js");
    }
    if (outliner) {
      writeScriptSrc ("javascript/outliner.js");
  	  writeScriptSrc ("javascript/nodomws.js");
  	  writeScriptSrc ("javascript/detect.js");
      writeScriptSrc ("javascript/outlineinit.js");
    }
    String cssHref = mainFrame.getWebPrefs().getCSShref();
    if (cssHref != null && cssHref.length() > 0) {
      writeStyleSheetLink (cssHref);
    }
    beginStartTag (TextType.STYLE);
    addAttribute (TextType.TYPE, TextType.TEXT_CSS);
    finishStartTag(TextType.STYLE);
    writeLine ("      body, p, h1, li {");
    writeLine ("        font-family: '" + mainFrame.getWebPrefs().getFontFamily() + "';");
    writeLine ("        font-size: " + mainFrame.getWebPrefs().getFontSize() + ";");
    writeLine ("      }");
    writeEndTag (TextType.STYLE);
    writeStyleSheetLink ("css/styles.css");

    writeLine ("    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->");
    writeLine ("    <!--[if lt IE 9]>");
    writeScriptSrc ("http://html5shim.googlecode.com/svn/trunk/html5.js");
    writeLine ("    <![endif]-->");
    
    writeEndTag (TextType.HEAD);

    writeStartTag (TextType.BODY);

  }

  private void endFile () {
    writeEndTag (TextType.BODY);
  }

  private void writeElement (String elementName, String content) {
    String trimmed = content.trim();
    // Do not write empty elements
    if (trimmed.length() > 0) {
      writeStartTag (elementName);
      writeContent (content);
      writeEndTag (elementName);
    }
  }
  
  private void startDiv (String klass) {
    beginStartTag  (TextType.DIV);
    addAttribute   (TextType.CLASS, klass);
    finishStartTag (TextType.DIV);
  }
  
  private void endDiv() {
    writeEndTag (TextType.DIV);
  }
  
  private void writeScriptSrc (String src) {
    beginStartTag  (TextType.SCRIPT);
    addAttribute   (TextType.SRC, src);
    finishStartTag (TextType.SCRIPT);
    writeEndTag    (TextType.SCRIPT);
  }
  
  private void writeStyleSheetLink (String link) {
    beginStartTag (TextType.LINK);
    addAttribute (TextType.REL, TextType.STYLESHEET);
    addAttribute (TextType.TYPE, TextType.TEXT_CSS);
    addAttribute (TextType.HREF, link);
    finishStartAndEndTag (TextType.LINK);
  }

  private void beginLink (String address) {
    writeStartTag (TextType.HTML_LINK, TextType.HREF, address);
  }

  private void beginLink (String address, String className) {
    writeStartTag (TextType.HTML_LINK, className, TextType.HREF, address);
  }

  private void endLink () {
    writeEndTag (TextType.HTML_LINK);
  }

  private void beginHeading (int headingLevel) {
    writeStartTag ("h" + String.valueOf(headingLevel));
  }

  private void beginHeading (int headingLevel, String className) {
    writeStartTag ("h" + String.valueOf(headingLevel), className);
  }

  private void endHeading (int headingLevel) {
    writeEndTag ("h" + String.valueOf(headingLevel));
  }

  private void writeMetadataNameAndContent (String name, String content) {
    beginStartTag (TextType.META);
    addAttribute (TextType.NAME, name);
    addAttribute (TextType.CONTENT, content);
    finishStartAndEndTag (TextType.META);
  }
  
  private void writeMetadata (String attr, String value) {
    beginStartTag (TextType.META);
    addAttribute (attr, value);
    finishStartAndEndTag (TextType.META);
  }

  private void writeStartTag (String elementName) {
    writeStartTag (elementName, "", "");
  }

  private void writeStartTag (String elementName, String className) {
    writeStartTag(elementName, TextType.CLASS, className);
  }

  /**
   Write a start tag for an element, with an optional attribute.

   @param elementName The name of the start tag.
   @param attr        An optional attribute.
   @param value       A value for the attribute.
   */
  private void writeStartTag (String elementName, String className,
      String attr, String value) {
    beginStartTag (elementName);
    addAttribute (TextType.CLASS, className);
    addAttribute (attr, value);
    finishStartTag (elementName);
  }

  private void writeStartTag (String elementName, String attr, String value) {
    beginStartTag (elementName);
    addAttribute (attr, value);
    finishStartTag (elementName);
  }

  /**
   Write the beginning of a start tag, assuming attributes and a close
   will follow.

   @param elementName The name of the start tag.
   */
  private void beginStartTag (String elementName) {
    startLine();
    indent();
    append ("<"
        + elementName);
  }

  /**
   Add an attribute to a start tag after it has been begun.

   @param attr  The attribute that goes before the equals sign.
   @param value The value that goes after the equals sign.
   */
  private void addAttribute (String attr, String value) {
    if (attr != null && value != null) {
      if (attr.length() > 0 && value.length() > 0) {
        append (" "
            + attr
            + "=\""
            + value
            + "\"");
      } // end if both attr and value are non-blank
    } // end if attr and value are both non-null
  } // end method addAttribute

  /**
   Finish off the start tag, writing the closing angle bracket.

   @param elementName The name of the element to be finished.
   */
  private void finishStartTag (String elementName) {
    append (">");
    writeLine();
    level++;
  }

  /**
   Finish off the start tag, writing the closing angle bracket.

   @param elementName The name of the element to be finished.
   */
  private void finishStartAndEndTag (String elementName) {
    append (" />");
    writeLine();
  }

  public void writeStartTag (
      String namespaceURI,
      String localName,
      String qualifiedName,
      Attributes attributes,
      boolean emptyTag) {
    startLine();
    indent();

    append ("<");
    append (localName);
    for (int i = 0; i < attributes.getLength(); i++) {
      append (" "
          + attributes.getLocalName(i)
          + "=\""
          + attributes.getValue(i)
          + "\"");
    }
    if (emptyTag) {
      append (" /");
    }
    append (">");

    writeLine();
    if (! emptyTag) {
      level++;
    }
  }

  public void writeContent (String s) {
    startLine();
    indent();
    append (markupWriter.formatTextForMarkup (s));
    writeLine();
  }

  public void writeContentAsIs (String s) {
    startLine();
    indent();
    append (s);
    writeLine();
  }

  private void writeEndTag (String elementName) {
    startLine();
    level--;
    indent();
    append ("</"
        + elementName + ">");
    writeLine();
  }

  public void writeEndTag (
      String namespaceURI,
      String localName,
      String qualifiedName) {
    startLine();
    level--;
    indent();
    append ("</"
        + localName
        + ">");
    writeLine();
  }

  private void startLine () {
    textOut.setLength(0);
  }

  private void indent() {
    xmlAppendSpaces (level * 2);
  }

  private void append(String s) {
    textOut.append (s);
  }

  /**
    Write the requested number of spaces.
   */
  public void xmlAppendSpaces (int spaces) {
    for (int i = 0; i < spaces; i++) {
      textOut.append (" ");
    }
  }

  private boolean closeOutput () {
    if (okSoFar) {
      okSoFar = markupWriter.close();
    }
    return okSoFar;
  } // end method

  private void writeLine() {
    writeLine (textOut.toString());
    startLine();
  }

  private void writeLine (String line) {
    if (okSoFar) {
      okSoFar = markupWriter.writeLine (line);
    }
  } // end method writeLine

  /**
   The following four methods implement the MarkupLineWriter interface.
   */

  public void write (StringBuffer s) {
    write (s.toString());
  }

  public void write (String s) {
    if (okSoFar) {
      okSoFar = markupWriter.write (s);
    } // end if okSoFar so far
  } // end method write

  public void newLine () {
    if (okSoFar) {
      okSoFar = markupWriter.newLine();
    } // end if okSoFar so far
  } // end method newLine

  public void flush () {
    markupWriter.flush();
  }

  public void close () {
    markupWriter.close();
  }

}
