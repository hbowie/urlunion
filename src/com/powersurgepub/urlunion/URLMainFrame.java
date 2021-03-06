
/*
 * Copyright 2009 - 2015 Herb Bowie
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


  import com.powersurgepub.linktweaker.*;
  import com.powersurgepub.psfiles.*;
  import com.powersurgepub.psdatalib.notenik.*;
  import com.powersurgepub.psdatalib.psdata.*;
  import com.powersurgepub.psdatalib.psdata.widgets.*;
  import com.powersurgepub.psdatalib.pstags.*;
  import com.powersurgepub.psdatalib.txbio.*;
  import com.powersurgepub.psdatalib.ui.*;
  import com.powersurgepub.pspub.*;
  import com.powersurgepub.psutils.*;
  import com.powersurgepub.urlvalidator.*;
  import com.powersurgepub.xos2.*;
  import java.awt.*;
  import java.awt.event.*;
  import java.io.*;
  import java.net.*;
  import java.text.*;
  import java.util.*;
  import javax.swing.*;
  import javax.swing.event.*;
  import javax.swing.tree.*;

/**
  The Main JFrame window for URL Union.

  @author Herb Bowie
 */
public class URLMainFrame extends javax.swing.JFrame
    implements 
      ActionListener,
      AppToBackup,
      TagsChangeAgent,
      FileSpecOpener,
      PublishAssistant,
      URLValidationRegistrar,
      XHandler,
      LinkTweakerApp {

  public static final String PROGRAM_NAME    = "URL Union";
  public static final String PROGRAM_VERSION = "2.00";

  public static final int    CHILD_WINDOW_X_OFFSET = 60;
  public static final int    CHILD_WINDOW_Y_OFFSET = 60;

  public static final        int    ONE_SECOND    = 1000;
  public static final        int    ONE_MINUTE    = ONE_SECOND * 60;
  public static final        int    ONE_HOUR      = ONE_MINUTE * 60;

  public static final String INVALID_URL_TAG = "Invalid URL";

  public static final String DEFAULT_FILE_NAME            = "urlunion.html";
  public static final String INDEX_FILE_NAME              = "index.html";
  public static final String FAVORITES_FILE_NAME          = "favorites.html";
  public static final String NETSCAPE_BOOKMARKS_FILE_NAME = "bookmark.html";
  public static final String OUTLINE_FILE_NAME            = "outline.html";
  public static final String SUPPORT_FOLDER_NAME          = "urlunion";

  private             Appster appster;

  private             String  country = "  ";
  private             String  language = "  ";

  private             Home home;
  private             ProgramVersion      programVersion;
  private             XOS                 xos = XOS.getShared();
  private             Trouble             trouble = Trouble.getShared();

  private             File                appFolder;
  private             String              userName;
  private             String              userDirString;
  
  private             StatusBar           statusBar = new StatusBar();

  // About window
  private             AboutWindow         aboutWindow;

  // Publish Window
  private             PublishWindow       publishWindow;

  // Properties Window
  private             CollectionWindow    collectionWindow;
  
  // Replace Window
  private             ReplaceWindow       replaceWindow;

  // Variables used for logging
  private             Logger              logger = Logger.getShared();
  private             LogOutput           logOutput;
  private             LogWindow           logWindow;

  private DateFormat    longDateFormatter
      = new SimpleDateFormat ("EEEE MMMM d, yyyy");
  private DateFormat  backupDateFormatter
      = new SimpleDateFormat ("yyyy-MM-dd-HH-mm");

  private             UserPrefs           userPrefs;
  private             PrefsWindow         prefsWindow;
  private             RecentFiles         recentFiles;
  private             FilePrefs           filePrefs;
  private             WebPrefs            webPrefs;

  // GUI Elements
  private             TextSelector        tagsTextSelector;
  private             TagTreeCellRenderer treeCellRenderer;

  private             URLPositioned       position = new URLPositioned();
  private             boolean             modified = false;
  private             boolean             unsavedChanges = false;
  private             URLCollection       urls = new URLCollection();
  private             XFileChooser        fileChooser = new XFileChooser();

  /** File of URLs that is currently open. */
  private             FileSpec            currentFileSpec = null;
  private             File                urlFile = null;
  private             File                currentDirectory;
  private             URLInputOutput      io;

  public  static final String             FIND = "Find";
  public  static final String             FIND_AGAIN = "Again";

  private             String              lastTextFound = "";
  
  private             URLPlus             foundURL = null;
  
  private             StringBuilder       titleBuilder = new StringBuilder();
  private             int                 titleStart = -1;
  private             StringBuilder       urlBuilder = new StringBuilder();
  private             int                 urlStart = -1;
  private             StringBuilder       tagsBuilder = new StringBuilder();
  private             int                 tagsStart = -1;
  private             StringBuilder       commentsBuilder = new StringBuilder();
  private             int                 commentsStart = -1;

  // Fields used to validate Web Page URLs
  private             javax.swing.Timer   validateURLTimer;
  private             ThreadGroup         webPageGroup;
  private             ArrayList           urlValidators;
  private             ProgressMonitor     progressDialog;
  private             int                 progressMax = 0;
  private             int                 progress = 0;
  private             int                 badPages = 0;

  // Help fields
  private             Tips                tips = null;

  // Written flags
  private             boolean             urlUnionWritten = false;
  private             boolean             favoritesWritten = false;
  private             boolean             netscapeWritten = false;
  private             boolean             outlineWritten = false;
  private             boolean             indexWritten = false;
  
  private             LinkTweaker         linkTweaker;
  private             TweakerPrefs        tweakerPrefs;
  private             LinkLabel           linkLabel;

  /** Creates new form URLMainFrame */
  public URLMainFrame() {
    appster = new Appster
        ("powersurgepub", "com",
          PROGRAM_NAME, PROGRAM_VERSION,
          language, country,
          this, this);
    home = Home.getShared ();
    programVersion = ProgramVersion.getShared ();
    initComponents();
    linkLabel = new LinkLabel("URL:");
    linkLabel.setLinkTextArea(urlText);
    linkLabel.setFrame(this);
    
    getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
    WindowMenuManager.getShared(windowMenu);
    currentDirectory = fileChooser.getCurrentDirectory();

    // Set About, Quit and other Handlers in platform-specific ways
    xos.setFileMenu (fileMenu);
    home.setHelpMenu(this, helpMenu);
    xos.setHelpMenu (helpMenu);
    xos.setHelpMenuItem(home.getHelpMenuItem());
    xos.setXHandler (this);
    xos.setMainWindow (this);
    xos.enablePreferences();

    // Initialize user preferences
    userPrefs = UserPrefs.getShared();
    prefsWindow = new PrefsWindow (this);
    
    webPrefs = prefsWindow.getWebPrefs();
    
    filePrefs = new FilePrefs(this);
    filePrefs.loadFromPrefs();
    prefsWindow.setFilePrefs(filePrefs);
    
    tweakerPrefs = new TweakerPrefs();
    prefsWindow.getPrefsTabs().add(TweakerPrefs.PREFS_TAB_NAME, tweakerPrefs);
    
    io = new URLInputOutput(this);
    
    recentFiles = new RecentFiles();
    
    filePrefs.setRecentFiles(recentFiles);
    recentFiles.registerMenu(openRecentMenu, this);
    
    recentFiles.loadFromPrefs();
    
    if (filePrefs.purgeRecentFilesAtStartup()) {
      recentFiles.purgeInaccessibleFiles();
    }

    // Use special text selector for the tags
    tagsTextSelector = new TextSelector();
    tagsTextSelector.setEditable(true);
    tagsTextSelector.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        tagsActionPerformed(evt);
      }
    });
    java.awt.GridBagConstraints gridBagConstraints;
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 2;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    linkPanel.add(tagsTextSelector, gridBagConstraints);
    tagsTextSelector.setValueList(urls.getTagsList());
    
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 2;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.1;
    gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
    linkPanel.add(linkLabel, gridBagConstraints);

    // Set initial UI prefs
    setBounds (
        userPrefs.getPrefAsInt (FavoritesPrefs.PREFS_LEFT, 100),
        userPrefs.getPrefAsInt (FavoritesPrefs.PREFS_TOP,  100),
        userPrefs.getPrefAsInt (FavoritesPrefs.PREFS_WIDTH, 620),
        userPrefs.getPrefAsInt (FavoritesPrefs.PREFS_HEIGHT, 540));
    CommonPrefs.getShared().setSplitPane(mainSplitPane);
    CommonPrefs.getShared().setMainWindow(this);
    setPreferredCollectionView();

    // Set up Logging
    logWindow = new LogWindow ();
    logOutput = new LogOutputText(logWindow.getTextArea());
    Logger.getShared().setLog (logOutput);
    Logger.getShared().setLogAllData (false);
    Logger.getShared().setLogThreshold (LogEvent.NORMAL);
    WindowMenuManager.getShared().add(logWindow);

    // Get App Folder
    appFolder = home.getAppFolder();
    if (appFolder == null) {
      trouble.report ("The " + home.getProgramName()
          + " Folder could not be found",
          "App Folder Missing");
    } else {
      Logger.getShared().recordEvent (LogEvent.NORMAL,
        "App Folder = " + appFolder.toString(),
        false);
    }

    aboutWindow = new AboutWindow(
      false,   // loadFromDisk, 
      true,    // jxlUsed,
      false,   // pegdownUsed,
      false,   // xerces used
      false,   // saxon used
      "2009"); // copyRightYearFrom
        

    publishWindow = new PublishWindow(this);
    publishWindow.setOnSaveOption(true);
    publishWindow.setStatusBar(statusBar);

    collectionWindow = new CollectionWindow();
    replaceWindow = new ReplaceWindow(this);
    
    linkTweaker = new LinkTweaker(this, prefsWindow.getPrefsTabs());
    linkLabel.setLinkTweaker(linkTweaker);

    // Get System Properties
    userName = System.getProperty ("user.name");
    userDirString = System.getProperty (GlobalConstants.USER_DIR);
    Logger.getShared().recordEvent (LogEvent.NORMAL,
      "User Directory = " + userDirString,
      false);

    // Write some basic data about the run-time environment to the log
    Logger.getShared().recordEvent (LogEvent.NORMAL,
        "Java Virtual Machine = " + System.getProperty("java.vm.name") +
        " version " + System.getProperty("java.vm.version") +
        " from " + StringUtils.removeQuotes(System.getProperty("java.vm.vendor")),
        false);
    if (xos.isRunningOnMacOS()) {
      Logger.getShared().recordEvent (LogEvent.NORMAL,
          "Mac Runtime for Java = " + System.getProperty("mrj.version"),
          false);
    }
    Runtime runtime = Runtime.getRuntime();
    runtime.gc();
    NumberFormat numberFormat = NumberFormat.getInstance();
    Logger.getShared().recordEvent (LogEvent.NORMAL,
        "Available Memory = " + numberFormat.format (Runtime.getRuntime().freeMemory()),
        false);

    // Automatically open the last file opened, if any

    // url = new URLPlus();
    // displayURL();
    // String lastFileString = userPrefs.getPref (FavoritesPrefs.LAST_FILE, "");
    String lastFileString = filePrefs.getStartupFilePath();
    if (lastFileString != null
        && lastFileString.length() > 0) {
      File lastFile = new File (lastFileString);
      if (lastFile.exists()
          && lastFile.isFile()
          && lastFile.canRead()) {
        openFile (lastFile);
        if (prefsWindow.getFavoritesPrefs().isOpenStartup()) {
          launchStartupURLs();
        }
      }
    }

    if (urls == null || urls.size() == 0) {
      File defaultDataFolder = Home.getShared().getProgramDefaultDataFolder();
      Home.getShared().ensureProgramDefaultDataFolder();
      File defaultFile = new File (defaultDataFolder, DEFAULT_FILE_NAME);
      if (defaultFile.exists()) {
        openFile (defaultFile);
      } else {
        newFile();
        saveFileAs(defaultFile);
      }
      // displayURL();
    }

    CommonPrefs.getShared().appLaunch();

  }

  public boolean preferencesAvailable() {
    return true;
  }

  /**
   Prepare the data entry screen for a new URL.
   */
  public void newURL() {

    // Capture current category selection, if any
    String selectedTags = "";
    TagsNode tags = (TagsNode)urlTree.getLastSelectedPathComponent();
    if (tags != null) {
      selectedTags = tags.getTagsAsString();
    }

    modIfChanged();

    position = new URLPositioned();
    position.setIndex (urls.size());
    displayURL();
    tagsTextSelector.setText (selectedTags);
  }

  /**
   Add the first URL for a new collection.
   */
  public void addFirstURL() {
    position = new URLPositioned();
    position.setIndex (urls.size());

    URLPlus url = position.getURLPlus();
    url.setTitle("PowerSurge Publishing");
    url.setURL("http://www.powersurgepub.com/");
    url.setTags("Software.Java.Groovy");
    url.setComments("Home to URL Union");

    setUnsavedChanges(true);
    addURL ();
    urls.fireTableDataChanged();

    modified = false;
  }

  private void removeURL () {
    if (position.isNewURL()) {
      System.out.println ("New URL -- ignoring delete command");
    } else {
      boolean okToDelete = true;
      if (CommonPrefs.getShared().confirmDeletes()) {
        int userOption = JOptionPane.showConfirmDialog(this, "Really delete this URL?",
            "Delete Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        okToDelete = (userOption == JOptionPane.YES_OPTION);
      }
      if (okToDelete) {
        position.setNavigatorToList
            (collectionTabbedPane.getSelectedIndex() == 0);
        position = urls.remove (position);
        setUnsavedChanges (true);
        positionAndDisplay();
      } // end if user confirmed delete
    } // end if new URL not yet saved
  } // end method removeURL

  private void checkTags() {
    modIfChanged();
    TagsChangeScreen replaceScreen = new TagsChangeScreen
        (this, true, urls.getTagsList(), this);
    replaceScreen.setLocation (
        this.getX() + CHILD_WINDOW_X_OFFSET,
        this.getY() + CHILD_WINDOW_Y_OFFSET);
    replaceScreen.setVisible (true);
    setUnsavedChanges (true);
    // catScreen.show();
  }

  /**
   Called from TagsChangeScreen.
   @param from The from String.
   @param to   The to String.
   */
  public void changeAllTags (String from, String to) {

    modIfChanged();
    URLPositioned workURL = new URLPositioned ();
    int mods = 0;
    for (int workIndex = 0; workIndex < urls.size(); workIndex++) {
      workURL.setURLPlus (urls.get (workIndex));
      workURL.setIndex (workIndex);
      String before = workURL.getURLPlus().getTags().toString();
      workURL.getURLPlus().getTags().replace (from, to);
      if (! before.equals (workURL.getURLPlus().getTags().toString())) {
        mods++;
        urls.modify(workURL);
      }
    }

    JOptionPane.showMessageDialog(this,
      String.valueOf (mods)
          + " tags changed",
      "Tags Replacement Results",
      JOptionPane.INFORMATION_MESSAGE);
    displayURL();
  }

  private void flattenTags() {
    modIfChanged();
    URLPositioned workURL = new URLPositioned();
    for (int workIndex = 0; workIndex < urls.size(); workIndex++) {
      workURL.setURLPlus (urls.get (workIndex));
      workURL.getURLPlus().flattenTags();
      urls.modify(workURL);
    }
    noFindInProgress();
    displayURL();
  }

  private void lowerCaseTags() {
    modIfChanged();
    URLPositioned workURL = new URLPositioned();
    for (int workIndex = 0; workIndex < urls.size(); workIndex++) {
      workURL.setURLPlus (urls.get (workIndex));
      workURL.getURLPlus().lowerCaseTags();
      urls.modify(workURL);
    }
    noFindInProgress();
  }

/*  public int checkTags (String find, String replace) {
    int mods = 0;
    URLPlus next;
    Tags tags;
    String tag;
    for (int i = 0; i < urls.size(); i++) {
      next = urls.get(i);
      tags = next.getTags();
      boolean modified = false;
      if (find.equals("")) {
        tags.merge (replace);
        modified = true;
      } else {
        TagsIterator iterator = new TagsIterator (tags);
        while (iterator.hasNextTag() && (! modified)) {
          tag = iterator.nextTag();
          if (tag.equalsIgnoreCase (find)) {
            iterator.removeTag();
            if (replace.length() > 0) {
              tags.merge (replace);
            }
            modified = true;
          }
        } // end while this item has more categories
      } // end if we the find category is not blank
      if (modified) {
        mods++;
        setUnsavedChanges (true);
      } // end if modified
    } // end of  items
    return mods;
  } */
  
  /**
   If requested, launch any URL that has been tagged with "startup"
  */
  private void launchStartupURLs() {
    URLPlus next;
    Tags tags;
    String tag;
    for (int i = 0; i < urls.size(); i++) {
      next = urls.get(i);
      tags = next.getTags();
      TagsIterator iterator = new TagsIterator (tags);
      while (iterator.hasNextTag()) {
        tag = iterator.nextTag();
        if (tag.equalsIgnoreCase("Startup")) {
          openURL(next.getURL());
        }
      }
    }
  }
  
  private void startReplace() {
    replaceWindow.startReplace(findText.getText());
    displayAuxiliaryWindow(replaceWindow);
  }
  
  /**
    Replace all occurrences of the given text string with a 
    specified replacement.
   
    @param findString    The string we're searching for. 
    @param replaceString The string to replace the find string. 
    @param checkTitle    Should we check the title of the URL item?
    @param checkURL      Should we check the URL of the URL item?
    @param checkTags     Should we check the tags of the URL item?
    @param checkComments Should we check the comments?
    @param caseSensitive Should we do a case-sensitive comparison?
  
  */
  public int replaceAll (String findString, String replaceString, 
      boolean checkTitle, 
      boolean checkURL, 
      boolean checkTags, 
      boolean checkComments,
      boolean caseSensitive) {
    
    int itemsChanged = 0;
    boolean found = true;
    findButton.setText(FIND);
    while (found) {
      found = findURL (
        findButton.getText(),
        findString, 
        checkTitle, 
        checkURL, 
        checkTags,
        checkComments,
        caseSensitive,
        false);
      if (found) {
        boolean replaced = replaceURL(
            findString, 
            replaceString, 
            checkTitle, 
            checkURL, 
            checkTags, 
            checkComments);
        if (replaced) {
          itemsChanged++;
        }
      } // end if another item found
    } // end while more matching URLs found
    
    
    if (itemsChanged == 0) {
      JOptionPane.showMessageDialog(this,
          "No matching URLS found",
          "OK",
          JOptionPane.WARNING_MESSAGE);
      statusBar.setStatus("No URLs found");
    } else {
      JOptionPane.showMessageDialog(this,
        String.valueOf (itemsChanged)
          + " URLs modified",
        "Replacement Results",
        JOptionPane.INFORMATION_MESSAGE);
      statusBar.setStatus(String.valueOf(itemsChanged) + " URLs modified");
    }
    
    return itemsChanged;
    
  } // end replaceAll method

  /**
    Find the next URL item containing the search string, or position the cursor
    on the search string, if it is currently empty. 
  */
  private void findURL () {

    findURL (
      findButton.getText(), 
      findText.getText().trim(), 
      replaceWindow.titleSelected(), 
      replaceWindow.urlSelected(), 
      replaceWindow.tagsSelected(),
      replaceWindow.commentsSelected(),
      replaceWindow.caseSensitive(),
      true);
      
    if (findText.getText().trim().length() == 0) {
      findText.grabFocus();
      statusBar.setStatus("Enter a search string");
    }
  }
  
  /**
    Find the specified text string within the list of URL items. This method may
    be called internally, or from the ReplaceWindow. The result will be to 
    position the displays on the item found, or display a message to the user
    that no matching item was found. 
  
    @param findButtonText Either "Find" or "Again", indicating whether we
                          are starting a new search or continuing an 
                          existing one. 
    @param findString  The string we're searching for. 
    @param checkTitle  Should we check the title of the URL item?
    @param checkURL    Should we check the URL of the URL item?
    @param checkTags   Should we check the tags of the URL item?
    @param checkComments Should we check the comments?
    @param caseSensitive Should we do a case-sensitive comparison?
    @param showDialogAtEnd Show a dialog to user when no remaining URLs found?
  */
  public boolean findURL (
      String findButtonText, 
      String findString, 
      boolean checkTitle, 
      boolean checkURL, 
      boolean checkTags,
      boolean checkComments,
      boolean caseSensitive,
      boolean showDialogAtEnd) {
        
    modIfChanged();
    boolean found = false;

    String notFoundMessage;
    if (findString != null && findString.length() > 0) {
      if (findButtonText.equals (FIND)) {
        notFoundMessage = "No URLs Found";
        position.setIndex (-1);
      } else {
        notFoundMessage = "No further URLs Found";
      }
      position.incrementIndex (1);
      String findLower = findString.toLowerCase();
      String findUpper = findString.toUpperCase();
      while (position.hasValidIndex(urls) && (! found)) {
        URLPlus urlCheck = urls.get (position.getIndex());
        found = findWithinURL(
            urlCheck,
            findString, 
            checkTitle, 
            checkURL, 
            checkTags,
            checkComments,
            caseSensitive,
            findLower,
            findUpper);
        if (found) {
          foundURL = urlCheck;
        } else {
          position.incrementIndex (1);
        }
      } // while still looking for next match
      if (found) {
        findInProgress();
        lastTextFound = findString;
        position = urls.positionUsingListIndex (position.getIndex());
        positionAndDisplay();
        statusBar.setStatus("Matching URL found");
      } else {
        JOptionPane.showMessageDialog(this,
            notFoundMessage,
            "OK",
            JOptionPane.WARNING_MESSAGE);
        noFindInProgress();
        lastTextFound = "";
        statusBar.setStatus(notFoundMessage);
        foundURL = null;
      }
    } // end if we've got a find string
    return found;
  } // end method findURL
  
  /**
    Check for a search string within the given URL Item. 

    @param urlToSearch The URL item to be checked. 
    @param findString  The string we're searching for. 
    @param checkTitle  Should we check the title of the URL item?
    @param checkURL    Should we check the URL of the URL item?
    @param checkTags   Should we check the tags of the URL item?
    @param checkComments Should we check the comments?
    @param caseSensitive Should we do a case-sensitive comparison?
    @param findLower   The search string in all lower case.
    @param findUpper   The search string in all upper case. 
    @return True if an item containing the search string was found. 
  */
  private boolean findWithinURL(
      URLPlus urlToSearch, 
      String findString, 
      boolean checkTitle, 
      boolean checkURL, 
      boolean checkTags,
      boolean checkComments,
      boolean caseSensitive,
      String findLower,
      String findUpper) {
    
    boolean found = false;
    
    if (checkTitle) {
      titleBuilder = new StringBuilder(urlToSearch.getTitle());
      if (caseSensitive) {
        titleStart = titleBuilder.indexOf(findString);
      } else {
        titleStart = StringUtils.indexOfIgnoreCase (findLower, findUpper,
            urlToSearch.getTitle(), 0);
      }
      if (titleStart >= 0) {
        found = true;
      }
    }

    if (checkURL) {
      urlBuilder = new StringBuilder(urlToSearch.getURL());
      if (caseSensitive) {
        urlStart = urlBuilder.indexOf(findString);
      } else {
        urlStart = StringUtils.indexOfIgnoreCase (findLower, findUpper,
            urlToSearch.getURL(), 0);
      }
      if (urlStart >= 0) {
        found = true;
      }
    }
    
    if (checkTags) {
      tagsBuilder = new StringBuilder(urlToSearch.getTagsAsString());
      if (caseSensitive) {
        tagsStart = tagsBuilder.indexOf(findString);
      } else {
        tagsStart = StringUtils.indexOfIgnoreCase (findLower, findUpper,
            urlToSearch.getTagsAsString(), 0);
      }
      if (tagsStart >= 0) {
        found = true;
      }
    }

    if (checkComments) {
      commentsBuilder = new StringBuilder(urlToSearch.getComments());
      if (caseSensitive) {
        commentsStart = commentsBuilder.indexOf(findString);
      } else {
        commentsStart = StringUtils.indexOfIgnoreCase (findLower, findUpper,
            urlToSearch.getComments(), 0);
      }
      if (commentsStart >= 0) {
        found = true;
      }
    }
    
    if (found) {
      foundURL = urlToSearch;
    } else {
      foundURL = null;
    }

    return found;
  }
  
  public WebPrefs getWebPrefs() {
    return webPrefs;
  }
  
  public String getLastTextFound() {
    return lastTextFound;
  }
  
  public void noFindInProgress() {
    findButton.setText(FIND);
    replaceWindow.noFindInProgress();
  }
  
  public void findInProgress() {
    findButton.setText(FIND_AGAIN);
    replaceWindow.findInProgress();
  }
  
  public void setFindText(String findString) {
    this.findText.setText(findString);
  }
  
  /**
    Replace the findString in a URL item that has already been found. 
  
    @param replaceString The string to replace the found string. 
  */
  public boolean replaceURL(
      String findString,
      String replaceString,
      boolean checkTitle, 
      boolean checkURL, 
      boolean checkTags,
      boolean checkComments) {
    
    boolean replaced = false;
    if (foundURL != null) {
      if (checkTitle && titleStart >= 0) {
        titleBuilder.replace(titleStart, titleStart + findString.length(), 
            replaceString);
        foundURL.setTitle(titleBuilder.toString());
        replaced = true;
      }

      if (checkURL && urlStart >= 0) {
 
        urlBuilder.replace(urlStart, urlStart + findString.length(), 
            replaceString);
        foundURL.setURL(urlBuilder.toString());
        replaced = true;
      }
      
      if (checkTags && tagsStart >= 0) {
        tagsBuilder.replace(tagsStart, tagsStart + findString.length(), 
            replaceString);
        foundURL.setTags(tagsBuilder.toString());
        replaced = true;
      }
      
      if (checkComments && commentsStart >= 0) {
        commentsBuilder.replace(commentsStart, commentsStart + findString.length(), 
            replaceString);
        foundURL.setComments(commentsBuilder.toString());
        replaced = true;
      }
      if (replaced) {
        positionAndDisplay();
        statusBar.setStatus("Replacement made");
        setUnsavedChanges(true);
      }
    }
    return replaced;
  }

  public void firstURL () {
    modIfChanged();
    noFindInProgress();
    position.setNavigatorToList (collectionTabbedPane.getSelectedIndex() == 0);
    position = urls.first (position);
    positionAndDisplay();
  }

  public void priorURL () {
    modIfChanged();
    noFindInProgress();
    position.setNavigatorToList (collectionTabbedPane.getSelectedIndex() == 0);
    position = urls.prior (position);
    positionAndDisplay();
  }

  public void nextURL() {
    modIfChanged();
    noFindInProgress();
    position.setNavigatorToList (collectionTabbedPane.getSelectedIndex() == 0);
    position = urls.next (position);
    positionAndDisplay();
  }

  public void lastURL() {
    modIfChanged();
    noFindInProgress();
    position.setNavigatorToList (collectionTabbedPane.getSelectedIndex() == 0);
    position = urls.last (position);
    positionAndDisplay();
  }

  private void positionAndDisplay () {
    if (position.getIndex() >= 0
        && position.getIndex() < urls.size()
        && position.getIndex() != urlTable.getSelectedRow()) {
      urlTable.setRowSelectionInterval
          (position.getIndex(), position.getIndex());
      urlTable.scrollRectToVisible
          ((urlTable.getCellRect(position.getIndex(), 0, false)));
    } 
    if (position.getTagsNode() != null
        && position.getTagsNode()
        != urlTree.getLastSelectedPathComponent()) {
      TreePath path = new TreePath(position.getTagsNode().getPath());
      urlTree.setSelectionPath (path);
      urlTree.scrollPathToVisible (path);
    }
    displayURL ();
  }

  /**
   Respond when the user clicks on a row in the URL list.
   */
  private void selectTableRow () {
    int selectedRow = urlTable.getSelectedRow();
    if (selectedRow >= 0 && selectedRow < urls.size()) {
      modIfChanged();
      position = urls.positionUsingListIndex (selectedRow);
      positionAndDisplay();
    }
  }

  /**
   Respond when user selects a url from the tags tree.
   */
  private void selectBranch () {

    TagsNode node = (TagsNode)urlTree.getLastSelectedPathComponent();


    if (node == null) {
      // nothing selected
    }
    else
    if (node == position.getTagsNode()) {
      // If we're already positioned on the selected node, then no
      // need to do anything else (especially since it might set off
      // an endless loop).
    }
    else
    if (node.getNodeType() == TagsNode.ITEM) {
      modIfChanged();
      URLPlus branch = (URLPlus)node.getTaggable();
      int branchIndex = urls.find (branch);
      if (branchIndex >= 0) {
        position = urls.positionUsingListIndex (branchIndex);
        position.setTagsNode (node);
        positionAndDisplay();
      } else {
        System.out.println ("Selected a branch from the tree that couldn't be found in the list");
      }
    }
    else {
      // Do nothing until an item is selected
    }
  }

  public void displayURL () {
    URLPlus url = position.getURLPlus();
    titleText.setText (url.getTitle());
    urlText.setText (url.getURL());
    tagsTextSelector.setText (url.getTagsAsString());
    commentsText.setText (url.getComments());
    lastModDateText.setText (url.getLastModDateStandard());
    statusBar.setPosition(position.getIndexForDisplay(), urls.size());
    modified = false;
  }

  /**
   Check to see if the user has changed anything and take appropriate
   actions if so.
   */
  public void modIfChanged () {

    URLPlus url = position.getURLPlus();
    if (! url.equalsTitle (titleText.getText())) {
      url.setTitle (titleText.getText());
      modified = true;
    }

    if ((urlText.getText().equals (url.getURL()))
        || ((urlText.getText().length() == 0) && url.blankURL())) {
      // No change
    } else {
      url.setURL (urlText.getText());
      modified = true;
    }

    if (! url.equalsTags (tagsTextSelector.getText())) {
      url.setTags (tagsTextSelector.getText());
      modified = true;
    }

    if (! commentsText.getText().equals (url.getComments())) {
      url.setComments (commentsText.getText());
      modified = true;
    }
    
    if (modified) {
      url.setLastModDateToday();
      setUnsavedChanges(true);
      if (position.isNewURL()) {
        if (url.hasURL()) {
          addURL ();
        } // end if we have url worth adding
      } else {
        urls.modify(position);
      }
      urls.fireTableDataChanged();
    } // end if modified
  } // end modIfChanged method

  private void addURL () {
    position = urls.add (position.getURLPlus());
    if (position.hasValidIndex (urls)) {
      positionAndDisplay();
    }
  }

  public void setUnsavedChanges (boolean unsavedChanges) {
    this.unsavedChanges = unsavedChanges;
    xos.setUnsavedChanges(unsavedChanges);
  }

  public void handleOpenApplication() {

  }

  /**
     Standard way to respond to an About Menu Item Selection on a Mac.
   */
  public void handleAbout() {
    displayAuxiliaryWindow(aboutWindow);
  }
  
  /*
   
   This section of the program deals with user preferences.
   
   */
    
  /**
     Standard way to respond to a Preferences Item Selection on a Mac.
   */
  public void handlePreferences() {
    displayPrefs ();
  }

  public void displayPrefs () {
    displayAuxiliaryWindow(prefsWindow);
  }

  public void setSplit (boolean splitPaneHorizontal) {
    int splitOrientation = JSplitPane.VERTICAL_SPLIT;
    if (splitPaneHorizontal) {
      splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
    }
    mainSplitPane.setOrientation (splitOrientation);
  }

  private void savePrefs () {
    if (urlFile != null
        && urlFile.exists()
        && urlFile.isFile()
        && urlFile.canRead()) {
      userPrefs.setPref (FavoritesPrefs.LAST_FILE, urlFile.toString());
    }
    userPrefs.setPref (FavoritesPrefs.PREFS_LEFT, this.getX());
    userPrefs.setPref (FavoritesPrefs.PREFS_TOP, this.getY());
    userPrefs.setPref (FavoritesPrefs.PREFS_WIDTH, this.getWidth());
    userPrefs.setPref (FavoritesPrefs.PREFS_HEIGHT, this.getHeight());
    
    savePreferredCollectionView();
    userPrefs.setPref (CommonPrefs.SPLIT_HORIZONTAL,
        mainSplitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT);
    prefsWindow.savePrefs();
    if (tips != null) {
      tips.savePrefs();
    }
    boolean prefsOK = userPrefs.savePrefs();
    recentFiles.savePrefs();
    tweakerPrefs.savePrefs();
  }

  public void handleOpenFile (FileSpec fileSpec) {
    handleOpenFile (new File(fileSpec.getPath()));
  }
  
  /**      
    Standard way to respond to a document being passed to this application on a Mac.
   
    @param inFile File to be processed by this application, generally
                  as a result of a file or directory being dragged
                  onto the application icon.
   */
  public void handleOpenFile (File inFile) {
    openFile (inFile);
  }

  /**
   Open the passed URI. 
   
   @param inURI The URI to open. 
  */
  public void handleOpenURI(URI inURI) {
    // Not supported
  }

  /**
   Standard way to respond to a print request.
   */
  public void handlePrintFile (File printFile) {
    // not supported
  }

  /**
     We're out of here!
   */
  public void handleQuit() {

    closeFile();

    savePrefs();

    System.exit(0);
  }

  private void reloadFile() {
    modIfChanged();
    saveFile();
    URLPositioned savePosition = position;
    if (urlFile != null
        && urlFile.exists()
        && urlFile.isFile()
        && urlFile.canRead()) {
      openFile (urlFile);
      position = savePosition;
      positionAndDisplay();
    }
  }
  
  private void clearFile() {
    modIfChanged();
    int option = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to delete the entire contents of the current list?");
    if (option == JOptionPane.YES_OPTION) {
      noFindInProgress();
      initCollection();
      collectionWindow.setURLs (urls);
      urls.fireTableDataChanged();
      setPreferredCollectionView();
      addFirstURL();
    }
  }

  public void newFile() {
    closeFile();
    initCollection();
    collectionWindow.setURLs (urls);
    urls.fireTableDataChanged();
    setURLFile (null);
    setPreferredCollectionView();
    // newURL();
    addFirstURL();
  }

  /**
   Let the user choose a file or folder to open.
   */
  private void openFile() {
    closeFile();
    fileChooser.setDialogTitle ("Open URLs");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    if (currentDirectory != null
        && currentDirectory.exists()
        && currentDirectory.isDirectory()
        && currentDirectory.canRead()) {
      fileChooser.setCurrentDirectory (currentDirectory);
    }
    File fileToOpen = null;
    File selectedFile = null;
    selectedFile = fileChooser.showOpenDialog(this);
    if (selectedFile != null) {
      if (selectedFile.isDirectory()) {
        fileToOpen = new File (selectedFile, DEFAULT_FILE_NAME);
      } else {
        fileToOpen = selectedFile;
      }
      if (fileToOpen.exists()
          && fileToOpen.isFile()
          && fileToOpen.canRead()) {
        openFile (fileToOpen);
      } else {
        trouble.report ("Trouble opening file " + fileToOpen.toString(),
            "File Open Error");
      }
    } // end if user approved a file/folder choice
  } // end method openFile

  private void openFile (File fileToOpen) {
    closeFile();
    initCollection();
    setURLFile (fileToOpen);
    readFileContents(urlFile);
    collectionWindow.setURLs (urls);
    urls.fireTableDataChanged();
    position = new URLPositioned ();
    setPreferredCollectionView();
    position = urls.first(position);
    positionAndDisplay();
  }

  private void savePreferredCollectionView () {
    userPrefs.setPref (FavoritesPrefs.LIST_TAB_SELECTED,
        collectionTabbedPane.getSelectedIndex() == 0);
  }

  private void setPreferredCollectionView () {
    boolean listTabSelected =
        userPrefs.getPrefAsBoolean (FavoritesPrefs.LIST_TAB_SELECTED, true);
    if (listTabSelected) {
      collectionTabbedPane.setSelectedComponent (listPanel);
      position.setNavigatorToList(true);
    } else {
      collectionTabbedPane.setSelectedComponent (treePanel);
      position.setNavigatorToList(false);
    }
  }

  private void initCollection () {
    urls = new URLCollection();
    urlTable.setModel(urls);
    tagsTextSelector.setValueList(urls.getTagsList());
    urlTree.setModel (urls.getTagsModel().getModel());
    urlTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
    treeCellRenderer = new TagTreeCellRenderer ();
    urlTree.setCellRenderer (treeCellRenderer);
    urlTree.doLayout();
    setUnsavedChanges(false);
  }

  private void importFile () {

    fileChooser.setDialogTitle ("Import URLs");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    if (currentDirectory != null
        && currentDirectory.exists()
        && currentDirectory.isDirectory()
        && currentDirectory.canRead()) {
      fileChooser.setCurrentDirectory (currentDirectory);
    }
    File selectedFile = fileChooser.showOpenDialog(this);
    if (selectedFile != null) {
      File importFile = selectedFile;
      currentDirectory = importFile.getParentFile();
      readFileContents(importFile);
      collectionWindow.setURLs (urls);
      setUnsavedChanges(true);
    }
    urls.fireTableDataChanged();
    firstURL();
  }
  
  private void exportToNoteNik() {
    fileChooser.setDialogTitle ("Export to NoteNik");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    File selectedFile = fileChooser.showSaveDialog(this);
    if (selectedFile != null
        && selectedFile.isDirectory()
        && selectedFile.canWrite()) {
      MarkupEntityTranslator translator 
          = MarkupEntityTranslator.getSharedInstance();
      File exportFolder = selectedFile;
      DataDictionary dict = new DataDictionary();
      RecordDefinition recDef = new RecordDefinition(dict);
      recDef.addColumn(NoteParms.TITLE_DEF);
      recDef.addColumn(NoteParms.TAGS_DEF);
      recDef.addColumn(NoteParms.LINK_DEF);
      recDef.addColumn(NoteParms.BODY_DEF);
      NoteIO noteIO = new NoteIO(exportFolder, NoteParms.DEFINED_TYPE, recDef);
      URLPlus workURL;
      Note workNote;
      try {
        for (int workIndex = 0; workIndex < urls.size(); workIndex++) {
          workURL = urls.get (workIndex);
          workNote = new Note(recDef);
          String title = translator.translateFromMarkup(workURL.getTitle());
          workNote.setTitle(title);
          workNote.setLink(workURL.getURL());
          workNote.setTags(workURL.getTags().toString());
          String body = translator.translateFromMarkup(workURL.getComments());
          if (body.length() > 0) {
            workNote.setBody(body);
          }
          // System.out.println("- exporting " + title + " to " + workNote.getFileName());
          noteIO.save (workNote, false);
        }
        JOptionPane.showMessageDialog(this,
              String.valueOf(urls.size()) + " URLs exported successfully to"
                + GlobalConstants.LINE_FEED
                + selectedFile.toString(),
              "Export Results",
              JOptionPane.INFORMATION_MESSAGE,
              Home.getShared().getIcon());
          logger.recordEvent (LogEvent.NORMAL, String.valueOf(urls.size()) 
              + " URLs exported as Notes to " 
              + selectedFile.toString(),
              false);
          statusBar.setStatus(String.valueOf(urls.size()) 
            + " URLs exported");
      } catch (IOException e) {
        Trouble.getShared().report(this, 
            "I/O Error exporting to NoteNik format", 
            "I/O Error", 
            JOptionPane.ERROR_MESSAGE);
      }
    } // end if user selected a valid folder
  } // end method export to NoteNik

  private void readFileContents (File inFile) {
    io.read (inFile, urls);
  }

  private void closeFile() {
    if (this.urlFile != null) {
      modIfChanged();
      checkForUnsavedChanges();
      publishWindow.closeSource();
      filePrefs.handleClose();
    }
  }

  private void checkForUnsavedChanges() {
    
    if (unsavedChanges) {
      String[] options = {"Save", "Discard"};
      int saveChangesOption = JOptionPane.showOptionDialog(this,
          "Unsaved Changes", "Unsaved Changes",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
          null, options, options[0]);
      if (saveChangesOption == 0) {
        saveFile();
      } // end if user wants to save changes
    } // end if unsavedChanges
  } // end method checkForUnsavedChanges

  private void saveFile () {
    savePreferredCollectionView();
    modIfChanged();
    if (urlFile == null) {
      saveFileAs();
    } else {
      io.save (urlFile, urls);
      setUnsavedChanges(false);
      publishWindow.saveSource();
    }
  }

  /**
   Save the current URL collection to a location specified by the user.
   */
  private void saveFileAs () {
    fileChooser.setDialogTitle ("Save URLs to File");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    if (currentDirectory != null
        && currentDirectory.exists()
        && currentDirectory.isDirectory()
        && currentDirectory.canRead()) {
      fileChooser.setCurrentDirectory (currentDirectory);
      fileChooser.setSelectedFile (new File (currentDirectory, DEFAULT_FILE_NAME));
    }
    File selectedFile = fileChooser.showSaveDialog (this);
    if(selectedFile != null) {
      File chosenFile = selectedFile;
      if (chosenFile.isDirectory()) {
        chosenFile = new File (selectedFile, DEFAULT_FILE_NAME);
      }
      saveFileAs(chosenFile);
    }
  }
  
  /**
   Save the current collection of URLs to the specified file. 
  
   @param asFile The file to save the urls to.  
  */
  private void saveFileAs(File asFile) {
    savePreferredCollectionView();
    setURLFile (asFile);
    io.save (asFile, urls);
    collectionWindow.setURLs (urls);
    setUnsavedChanges(false);
    publishWindow.saveSource();
  }

  /**
   Save various bits of information about a new URL file that we are
   working with.

   @param file The specific file we are working with that contains a list
   of URLs.

   */
  private void setURLFile (File file) {
    if (file == null) {
      urlFile = null;
      currentFileSpec = null;
      statusBar.setFileName("            ", " ");
    } else {
      urlFile = file;
      if (urls != null) {
        urls.setSource (file);
      }
      currentFileSpec = recentFiles.addRecentFile (file);
      currentDirectory = file.getParentFile();
      userPrefs.setPref (FavoritesPrefs.LAST_FILE, file.toString());
      FileName fileName = new FileName (file);
      statusBar.setFileName(fileName);
      publishWindow.openSource(currentDirectory);
    }
  }

  public void displayPublishWindow() {
    displayAuxiliaryWindow(publishWindow);
  }

  public void displayAuxiliaryWindow(WindowToManage window) {
    window.setLocation(
        this.getX() + 60,
        this.getY() + 60);
    WindowMenuManager.getShared().makeVisible(window);
  }

  /**
   Any pre-processing to do before PublishWindow starts its publication
   process. In particular, make the source data available to the publication
   script.

   @param publishTo The folder to which we are publishing.
   */
  public void prePub(File publishTo) {
    File urlsTab = new File (publishTo, "urls.tab");
    io.exportToTabDelimited(urls, urlsTab, false, "");
    
    File favoritesTab = new File (publishTo, "favorites.tab");
    io.exportToTabDelimited(urls, favoritesTab, true,
        prefsWindow.getFavoritesPrefs().getFavoritesTags());

    urlUnionWritten = false;
    favoritesWritten = false;
    netscapeWritten = false;
    outlineWritten = false;
    indexWritten = false;
  }

  /**
   Perform the requested publishing operation.
   
   @param operand
   */
  public boolean pubOperation(File publishTo, String operand) {
    boolean operationOK = false;
    if (operand.equalsIgnoreCase("urlunion")) {
      operationOK = publishURLUnion(publishTo);
    }
    else
    if (operand.equalsIgnoreCase("favorites")) {
      operationOK = publishFavorites(publishTo);
    }
    else
    if (operand.equalsIgnoreCase("netscape")) {
      operationOK = publishNetscape(publishTo);
    }
    else
    if (operand.equalsIgnoreCase("outline")) {
      operationOK = publishOutline(publishTo);
    }
    else
    if (operand.equalsIgnoreCase("index")) {
      operationOK = publishIndex(publishTo);
    }
    return operationOK;
  }

  /**
   Any post-processing to be done after PublishWindow has completed its
   publication process.

   @param publishTo The folder to which we are publishing.
   */
  public void postPub(File publishTo) {

  }

  private boolean publishURLUnion (File publishTo) {
    urlUnionWritten = false;
    File urlUnionFile = new File (publishTo, DEFAULT_FILE_NAME);
    if (! urlUnionFile.toString().equals(urlFile.toString())) {
      io.save (urlUnionFile, urls);
      urlUnionWritten = true;
    }
    return urlUnionWritten;
  }

  private boolean publishFavorites (File publishTo) {
    
    // Publish selected favorites
    favoritesWritten = false;
    if (! urlFile.getName().equalsIgnoreCase (FAVORITES_FILE_NAME)) {
      favoritesWritten = io.publishFavorites
          (publishTo, urls, prefsWindow.getFavoritesPrefs());
    }
    return favoritesWritten;
  }

  private boolean publishNetscape (File publishTo) {
    // Publish in Netscape bookmarks format
    netscapeWritten = false;
    if (! urlFile.getName().equalsIgnoreCase (NETSCAPE_BOOKMARKS_FILE_NAME)) {
      File netscapeFile = new File (publishTo,
        NETSCAPE_BOOKMARKS_FILE_NAME);
      io.publishNetscape (netscapeFile, urls);
      netscapeWritten = true;
    }
    return netscapeWritten;
  }

  private boolean publishOutline (File publishTo) {
    // Publish in outline form using dynamic html
    outlineWritten = false;
    if (! urlFile.getName().equalsIgnoreCase (OUTLINE_FILE_NAME)) {
      File dynamicHTMLFile = new File (publishTo, OUTLINE_FILE_NAME);
      io.publishOutline(dynamicHTMLFile, urls);
      outlineWritten = true;
    }
    return outlineWritten;
  }

  private boolean publishIndex (File publishTo) {
    // Publish index file pointing to other files
    indexWritten = false;
    if (! urlFile.getName().equalsIgnoreCase (INDEX_FILE_NAME)) {
      File indexFile = new File (publishTo, INDEX_FILE_NAME);
      io.publishIndex(indexFile, urlFile,
          favoritesWritten, FAVORITES_FILE_NAME,
          netscapeWritten, NETSCAPE_BOOKMARKS_FILE_NAME,
          outlineWritten, OUTLINE_FILE_NAME);
      indexWritten = true;
    }
    return indexWritten;
  }
  
  /**
   Backup without prompting the user. 
  
   @return True if backup was successful. 
  */
  public boolean backupWithoutPrompt() {

    boolean backedUp = false;
    
    if (urlFile != null && urlFile.exists()) {
      FileName urlFileName = new FileName (urlFile);
      File backupFolder = getBackupFolder();
      String backupFileName 
          = filePrefs.getBackupFileName(urlFile, urlFileName.getExt());
      File backupFile = new File 
          (backupFolder, backupFileName);
      backedUp = backup (backupFile);
    }

    return backedUp;
    
  }
  
  /**
   Create a backup of the current file. 
   */
  public boolean promptForBackup () {

    boolean backedUp = false;

    fileChooser.setDialogTitle ("Make Backup of URL Union File");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    FileName urlFileName = new FileName (home.getUserHome());
    if (urlFile != null && urlFile.exists()) {
      urlFileName = new FileName (urlFile);
    }
    File backupFolder = getBackupFolder();
    fileChooser.setCurrentDirectory (backupFolder);
    if (urlFile != null && urlFile.exists()) {
      fileChooser.setSelectedFile
          (new File(backupFolder.toString() + File.separator
            + filePrefs.getBackupFileName(urlFile, urlFileName.getExt())));
    }
    File selectedFile = fileChooser.showSaveDialog (this);
    if(selectedFile != null) {
      File backupFile = selectedFile;
      backup (backupFile);
      userPrefs.setPref (FavoritesPrefs.BACKUP_FOLDER,
          fileChooser.getSelectedFile().getParentFile().toString());
      FileSpec fileSpec = recentFiles.get(0);
      fileSpec.setBackupFolder(backupFile);
    }

    return backedUp;

  }
  
  /**
   Backup the data store to the indicated location. 
  
   @param backupFile The backup file to be used. 
  
   @return 
  */
  public boolean backup(File backupFile) {
    boolean backedUp = io.save (backupFile, urls);
    if (backedUp) {
      logger.recordEvent (LogEvent.NORMAL,
          "URLs backed up to " + backupFile.toString(),
            false);
    } else {
      logger.recordEvent (LogEvent.MEDIUM,
          "Problem backing up URLs to " + backupFile.toString(),
            false);
    }
    return backedUp;
  }
  
  /**
   Return the presumptive folder to be used for backups. 
  
   @return The folder we think the user wishes to use for backups,
           based on his past choices, or on the application defaults.
  */
  private File getBackupFolder() {
    File backupFolder = home.getUserHome();
    if (urlFile != null && urlFile.exists()) {    
      FileSpec fileSpec = recentFiles.get(0);
      String backupFolderStr = fileSpec.getBackupFolder();
      File defaultBackupFolder = new File (fileSpec.getFolder(), "backups");
      if (backupFolderStr == null
          || backupFolderStr.length() < 2) {
        backupFolder = defaultBackupFolder;
      } else {
        backupFolder = new File (backupFolderStr);
        if (backupFolder.exists()
            && backupFolder.canWrite()) {
          // leave as-is
        } else {
          backupFolder = defaultBackupFolder;
        }
      }
    }
    return backupFolder;
  }
  
  private void tweakURL() {
    if (urlText.getText().length() > 0) {
      linkTweaker.setLink(urlText.getText());
    }
    displayAuxiliaryWindow(linkTweaker);
  }
  
  /**
   Get the current link so that it can be tweaked. 
  
   @return The Link to be tweaked. 
  */
  public String getLinkToTweak() {
    return urlText.getText();
  }
  
  /**
   Set a link field to a new value after it has been tweaked. 
  
   @param tweakedLink The link after it has been tweaked. 
   @param linkID      A string identifying the link, in case there are more
                      than one. This would be the text used in the label
                      for the link. 
  */
  public void putTweakedLink (String tweakedLink, String linkID) {
    if (tweakedLink.length() > 0) {
      urlText.setText(tweakedLink);
    }
  }

  public void openURL (File file) {
    appster.openURL(file);
  }

  public void openURL (String url) {
    appster.openURL(url);
  }

  private void tagsActionPerformed (java.awt.event.ActionEvent evt) {
    
  }

  /**
    Validate URLs.
   */
  public void validateURLs () {

    modIfChanged();

    // Make sure user is ready to proceed
    Object[] options = { "Continue", "Cancel" };
    int userOption = JOptionPane.showOptionDialog(this,
        "Please ensure your Internet connection is active",
        "Validate Web Pages",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.WARNING_MESSAGE,
        null, options, options[0]);

    // If User is ready, then proceed
    if (userOption == 0) {

      // Prepare Auxiliary List to track invalid URLs
      webPageGroup = new ThreadGroup("WebPage threads");
      urlValidators = new ArrayList();

      // Go through sorted items looking for Web Pages
      URLPlus workURL;
      String address;
      URLValidator validator;
      for (int workIndex = 0; workIndex < urls.size(); workIndex++) {
        workURL = urls.get (workIndex);
        address = workURL.getURLasString();
        if (address.length() > 0) {
          validator = new URLValidator (webPageGroup, workURL, workIndex, this);
          urlValidators.add (validator);
        }
      } // end of list

      // Prepare dialog to show validation progress
      progress = 0;
      progressMax = urlValidators.size();
      progressDialog = new ProgressMonitor (this,
          "Validating "
              + String.valueOf (progressMax)
              + " Web Page URLs...",
          "                                                  ", // Status Note
          0,              // lower bound of range
          progressMax     // upper bound of range
          );
      progressDialog.setProgress(0);
      progressDialog.setMillisToDecideToPopup(500);
      progressDialog.setMillisToPopup(500);

      // Now start threads to check Web pages
      badPages = 0;
      for (int i = 0; i < urlValidators.size(); i++) {
        validator = (URLValidator)urlValidators.get(i);
        validator.start();
      } // end for each page being validated

      // Start timer to give the user a chance to cancel
      if (validateURLTimer == null) {
        validateURLTimer = new javax.swing.Timer (ONE_SECOND, this);
      } else {
        validateURLTimer.setDelay (ONE_SECOND);
      }
      validateURLTimer.start();
    } // continue rather than cancel
  } // end validateURLs method

  /**
    Record the results each time a WebPage checks in to report that
    its URL validation process has been complete.

    @param item   The ToDoItem whose Web Page URL was being validated.
    @param valid  True if the URL was found to be valid.
   */
  public synchronized void registerURLValidationResult
      (ItemWithURL item, boolean valid) {
    progress++;
    progressDialog.setProgress (progress);
    progressDialog.setNote ("Validation complete for "
        + String.valueOf (progress));
    if (! valid) {
      badPages++;
    }
    if (progress >= progressMax) {
      validateURLAllDone();
    } // end if all pages checked
  } // end method validateURLPageDone

  /**
    Handle GUI events, including the firing of various timers.

    @param event The GUI event that fired the action.
   */
  public void actionPerformed (ActionEvent event) {
    Object source = event.getSource();

    // URL Validation Timer
    if (source == validateURLTimer) {
      if (progressDialog.isCanceled()) {
        URLValidator validator;
        for (int i = 0; i < urlValidators.size(); i++) {
          validator = (URLValidator)urlValidators.get(i);
          if (! validator.isValidationComplete()) {
            Logger.getShared().recordEvent (new LogEvent (LogEvent.MEDIUM,
                "URL Validation incomplete for "
                + validator.toString(),
                false));
            validator.interrupt();
          }
        } // end for each page being validated
        validateURLAllDone();
      }
    }

  } // end method

  /**
    Shut down the URL Validation process and report the results.
   */
  private void validateURLAllDone () {
    if (validateURLTimer != null
        && validateURLTimer.isRunning()) {
      validateURLTimer.stop();
    }

    // Add "Invalid URL" tags to invalid URL items
    if (badPages > 0) {
      URLValidator validator;
      URLPositioned workURL;
      for (int i = 0; i < urlValidators.size(); i++) {
        validator = (URLValidator)urlValidators.get(i);
        if (! validator.isValidURL()) {
          workURL = urls.positionUsingListIndex (validator.getIndex());
          if (workURL.getURLPlus().equals (validator.getItemWithURL())) {
            workURL.getURLPlus().getTags().merge (INVALID_URL_TAG);
            setUnsavedChanges(true);
            urls.modify(workURL);
          } // end if we have the right URL
        } // end if URL wasn't validated
      } // end for each page being validated
      urls.fireTableDataChanged();
    } // end if any bad pages found

    // Close progress dialog and show user the final results
    progressDialog.close();
    JOptionPane.showMessageDialog(this,
      String.valueOf (badPages)
          + " Invalid URL(s) Found out of "
          + String.valueOf (urlValidators.size()),
      "URL Validation Results",
      JOptionPane.INFORMATION_MESSAGE);

  } // end method

  public XFileChooser getFileChooser () {
    return fileChooser;
  }

  public File getCurrentDirectory () {
    return currentDirectory;
  }

  /**
   Show the tips.
   */
  public void showTips () {

    if (tips == null) {
      tips = new Tips ();
      tips.noTipsAtStartupOption();
    }
    tips.setVisible (true);
    tips.toFront();
  }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    mainToolBar = new javax.swing.JToolBar();
    urlOKButton = new javax.swing.JButton();
    urlNewButton = new javax.swing.JButton();
    urlDeleteButton = new javax.swing.JButton();
    urlFirstButton = new javax.swing.JButton();
    urlPriorButton = new javax.swing.JButton();
    urlNextButton = new javax.swing.JButton();
    urlLastButton = new javax.swing.JButton();
    launchButton = new javax.swing.JButton();
    findText = new javax.swing.JTextField();
    findButton = new javax.swing.JButton();
    mainSplitPane = new javax.swing.JSplitPane();
    collectionTabbedPane = new javax.swing.JTabbedPane();
    listPanel = new javax.swing.JPanel();
    tableScrollPane = new javax.swing.JScrollPane();
    urlTable = new javax.swing.JTable();
    treePanel = new javax.swing.JPanel();
    treeScrollPane = new javax.swing.JScrollPane();
    urlTree = new javax.swing.JTree();
    linkPanel = new javax.swing.JPanel();
    titleLabel = new javax.swing.JLabel();
    titleText = new javax.swing.JTextField();
    urlScrollPane = new javax.swing.JScrollPane();
    urlText = new javax.swing.JTextArea();
    tagsLabel = new javax.swing.JLabel();
    commentsLabel = new javax.swing.JLabel();
    commentsScrollPane = new javax.swing.JScrollPane();
    commentsText = new javax.swing.JTextArea();
    lastModDateLabel = new javax.swing.JLabel();
    lastModDateText = new javax.swing.JLabel();
    mainMenuBar = new javax.swing.JMenuBar();
    fileMenu = new javax.swing.JMenu();
    fileNewMenuItem = new javax.swing.JMenuItem();
    openMenuItem = new javax.swing.JMenuItem();
    openRecentMenu = new javax.swing.JMenu();
    fileSaveMenuItem = new javax.swing.JMenuItem();
    fileSaveAsMenuItem = new javax.swing.JMenuItem();
    reloadMenuItem = new javax.swing.JMenuItem();
    jSeparator6 = new javax.swing.JPopupMenu.Separator();
    clearMenuItem = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JSeparator();
    propertiesMenuItem = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JSeparator();
    publishWindowMenuItem = new javax.swing.JMenuItem();
    publishNowMenuItem = new javax.swing.JMenuItem();
    jSeparator4 = new javax.swing.JSeparator();
    fileBackupMenuItem = new javax.swing.JMenuItem();
    jSeparator5 = new javax.swing.JSeparator();
    importMenuItem = new javax.swing.JMenuItem();
    exportMenu = new javax.swing.JMenu();
    exportNoteNikMenuItem = new javax.swing.JMenuItem();
    editMenu = new javax.swing.JMenu();
    deleteMenuItem = new javax.swing.JMenuItem();
    listMenu = new javax.swing.JMenu();
    findMenuItem = new javax.swing.JMenuItem();
    replaceMenuItem = new javax.swing.JMenuItem();
    jSeparator10 = new javax.swing.JPopupMenu.Separator();
    addReplaceMenuItem = new javax.swing.JMenuItem();
    flattenTagsMenuItem = new javax.swing.JMenuItem();
    lowerCaseTagsMenuItem = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    validateURLsMenuItem = new javax.swing.JMenuItem();
    URLMenu = new javax.swing.JMenu();
    nextMenuItem = new javax.swing.JMenuItem();
    priorMenuItem = new javax.swing.JMenuItem();
    toolsMenu = new javax.swing.JMenu();
    toolsOptionsMenuItem = new javax.swing.JMenuItem();
    windowMenu = new javax.swing.JMenu();
    helpMenu = new javax.swing.JMenu();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    mainToolBar.setRollover(true);

    urlOKButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
    urlOKButton.setText("OK");
    urlOKButton.setToolTipText("Complete your changes to this item");
    urlOKButton.setFocusable(false);
    urlOKButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    urlOKButton.setMargin(new java.awt.Insets(0, 4, 4, 4));
    urlOKButton.setMaximumSize(new java.awt.Dimension(60, 30));
    urlOKButton.setMinimumSize(new java.awt.Dimension(30, 26));
    urlOKButton.setPreferredSize(new java.awt.Dimension(40, 28));
    urlOKButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    urlOKButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        urlOKButtonActionPerformed(evt);
      }
    });
    mainToolBar.add(urlOKButton);

    urlNewButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
    urlNewButton.setText("+");
    urlNewButton.setToolTipText("Add a new item");
    urlNewButton.setFocusable(false);
    urlNewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    urlNewButton.setMargin(new java.awt.Insets(0, 4, 4, 4));
    urlNewButton.setMaximumSize(new java.awt.Dimension(60, 30));
    urlNewButton.setMinimumSize(new java.awt.Dimension(30, 26));
    urlNewButton.setPreferredSize(new java.awt.Dimension(40, 28));
    urlNewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    urlNewButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        urlNewButtonActionPerformed(evt);
      }
    });
    mainToolBar.add(urlNewButton);

    urlDeleteButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
    urlDeleteButton.setText("-");
    urlDeleteButton.setToolTipText("Delete this item");
    urlDeleteButton.setFocusable(false);
    urlDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    urlDeleteButton.setMargin(new java.awt.Insets(0, 4, 4, 4));
    urlDeleteButton.setMaximumSize(new java.awt.Dimension(60, 30));
    urlDeleteButton.setMinimumSize(new java.awt.Dimension(30, 26));
    urlDeleteButton.setPreferredSize(new java.awt.Dimension(40, 28));
    urlDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    urlDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        urlDeleteButtonActionPerformed(evt);
      }
    });
    mainToolBar.add(urlDeleteButton);

    urlFirstButton.setText("<<");
    urlFirstButton.setToolTipText("Return to beginning of list");
    urlFirstButton.setFocusable(false);
    urlFirstButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    urlFirstButton.setMaximumSize(new java.awt.Dimension(60, 30));
    urlFirstButton.setMinimumSize(new java.awt.Dimension(30, 26));
    urlFirstButton.setPreferredSize(new java.awt.Dimension(40, 28));
    urlFirstButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    urlFirstButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        urlFirstButtonAction(evt);
      }
    });
    mainToolBar.add(urlFirstButton);

    urlPriorButton.setText("<");
    urlPriorButton.setToolTipText("Return to prior item in list");
    urlPriorButton.setFocusable(false);
    urlPriorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    urlPriorButton.setMaximumSize(new java.awt.Dimension(60, 30));
    urlPriorButton.setMinimumSize(new java.awt.Dimension(30, 26));
    urlPriorButton.setPreferredSize(new java.awt.Dimension(40, 28));
    urlPriorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    urlPriorButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        urlPriorButtonAction(evt);
      }
    });
    mainToolBar.add(urlPriorButton);

    urlNextButton.setText(">");
    urlNextButton.setToolTipText("Advance to next item in list");
    urlNextButton.setFocusable(false);
    urlNextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    urlNextButton.setMaximumSize(new java.awt.Dimension(60, 30));
    urlNextButton.setMinimumSize(new java.awt.Dimension(30, 26));
    urlNextButton.setPreferredSize(new java.awt.Dimension(40, 28));
    urlNextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    urlNextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        urlNextButtonAction(evt);
      }
    });
    mainToolBar.add(urlNextButton);

    urlLastButton.setText(">>");
    urlLastButton.setToolTipText("Go to end of list");
    urlLastButton.setFocusable(false);
    urlLastButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    urlLastButton.setMaximumSize(new java.awt.Dimension(60, 30));
    urlLastButton.setMinimumSize(new java.awt.Dimension(30, 26));
    urlLastButton.setPreferredSize(new java.awt.Dimension(40, 28));
    urlLastButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    urlLastButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        urlLastButtonAction(evt);
      }
    });
    mainToolBar.add(urlLastButton);

    launchButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
    launchButton.setText("Launch");
    launchButton.setToolTipText("Open the URL in your Web browser");
    launchButton.setFocusable(false);
    launchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    launchButton.setMargin(new java.awt.Insets(0, 4, 4, 4));
    launchButton.setMaximumSize(new java.awt.Dimension(80, 30));
    launchButton.setMinimumSize(new java.awt.Dimension(30, 26));
    launchButton.setPreferredSize(new java.awt.Dimension(40, 28));
    launchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    launchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        launchButtonActionPerformed(evt);
      }
    });
    mainToolBar.add(launchButton);

    findText.setMargin(new java.awt.Insets(4, 4, 4, 4));
    findText.setMaximumSize(new java.awt.Dimension(240, 30));
    findText.setMinimumSize(new java.awt.Dimension(40, 26));
    findText.setPreferredSize(new java.awt.Dimension(120, 28));
    findText.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        findTextActionPerformed(evt);
      }
    });
    findText.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(java.awt.event.KeyEvent evt) {
        findTextKeyTyped(evt);
      }
    });
    mainToolBar.add(findText);

    findButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
    findButton.setText("Find");
    findButton.setToolTipText("Search for the text entered to the left");
    findButton.setFocusable(false);
    findButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    findButton.setMaximumSize(new java.awt.Dimension(72, 30));
    findButton.setMinimumSize(new java.awt.Dimension(48, 26));
    findButton.setPreferredSize(new java.awt.Dimension(60, 28));
    findButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    findButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        findButtonActionPerformed(evt);
      }
    });
    mainToolBar.add(findButton);

    getContentPane().add(mainToolBar, java.awt.BorderLayout.NORTH);

    mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

    listPanel.setLayout(new java.awt.BorderLayout());

    urlTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    urlTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        urlTableMouseClicked(evt);
      }
    });
    urlTable.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        urlTableKeyReleased(evt);
      }
    });
    tableScrollPane.setViewportView(urlTable);

    listPanel.add(tableScrollPane, java.awt.BorderLayout.CENTER);

    collectionTabbedPane.addTab("List", listPanel);

    treePanel.setLayout(new java.awt.BorderLayout());

    urlTree.getSelectionModel().setSelectionMode
    (TreeSelectionModel.SINGLE_TREE_SELECTION);
    urlTree.addTreeSelectionListener (new TreeSelectionListener() {
      public void valueChanged (TreeSelectionEvent e) {
        selectBranch();
      }
    });
    treeScrollPane.setViewportView(urlTree);

    treePanel.add(treeScrollPane, java.awt.BorderLayout.CENTER);

    collectionTabbedPane.addTab("Tags", treePanel);

    mainSplitPane.setLeftComponent(collectionTabbedPane);

    linkPanel.setLayout(new java.awt.GridBagLayout());

    titleLabel.setLabelFor(titleText);
    titleLabel.setText("Title:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
    linkPanel.add(titleLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    linkPanel.add(titleText, gridBagConstraints);

    urlText.setColumns(20);
    urlText.setLineWrap(true);
    urlText.setRows(3);
    urlScrollPane.setViewportView(urlText);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.2;
    gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
    linkPanel.add(urlScrollPane, gridBagConstraints);

    tagsLabel.setText("Tags:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
    linkPanel.add(tagsLabel, gridBagConstraints);

    commentsLabel.setLabelFor(commentsText);
    commentsLabel.setText("Comments:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
    linkPanel.add(commentsLabel, gridBagConstraints);

    commentsText.setColumns(20);
    commentsText.setLineWrap(true);
    commentsText.setRows(5);
    commentsText.setWrapStyleWord(true);
    commentsScrollPane.setViewportView(commentsText);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.8;
    gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
    linkPanel.add(commentsScrollPane, gridBagConstraints);

    lastModDateLabel.setLabelFor(commentsText);
    lastModDateLabel.setText("Mod Date:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
    linkPanel.add(lastModDateLabel, gridBagConstraints);

    lastModDateText.setLabelFor(commentsText);
    lastModDateText.setText("  ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
    linkPanel.add(lastModDateText, gridBagConstraints);

    mainSplitPane.setRightComponent(linkPanel);

    getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

    fileMenu.setText("File");

    fileNewMenuItem.setText("New");
    fileNewMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fileNewMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(fileNewMenuItem);

    openMenuItem.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    openMenuItem.setText("Open...");
    openMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        openMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(openMenuItem);

    openRecentMenu.setText("Open Recent");
    fileMenu.add(openRecentMenu);

    fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    fileSaveMenuItem.setText("Save");
    fileSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fileSaveMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(fileSaveMenuItem);

    fileSaveAsMenuItem.setText("Save As...");
    fileSaveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fileSaveAsMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(fileSaveAsMenuItem);

    reloadMenuItem.setText("Reload");
    reloadMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        reloadMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(reloadMenuItem);
    fileMenu.add(jSeparator6);

    clearMenuItem.setText("Clear...");
    clearMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(clearMenuItem);
    fileMenu.add(jSeparator1);

    propertiesMenuItem.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    propertiesMenuItem.setText("Get Info");
    propertiesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        propertiesMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(propertiesMenuItem);
    fileMenu.add(jSeparator2);

    publishWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    publishWindowMenuItem.setText("Publish...");
    publishWindowMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        publishWindowMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(publishWindowMenuItem);

    publishNowMenuItem.setText("Publish Now");
    publishNowMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        publishNowMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(publishNowMenuItem);
    fileMenu.add(jSeparator4);

    fileBackupMenuItem.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    fileBackupMenuItem.setText("Backup...");
    fileBackupMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fileBackupMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(fileBackupMenuItem);
    fileMenu.add(jSeparator5);

    importMenuItem.setText("Import...");
    importMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        importMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(importMenuItem);

    exportMenu.setText("Export");

    exportNoteNikMenuItem.setText("NoteNik...");
    exportNoteNikMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exportNoteNikMenuItemActionPerformed(evt);
      }
    });
    exportMenu.add(exportNoteNikMenuItem);

    fileMenu.add(exportMenu);

    mainMenuBar.add(fileMenu);

    editMenu.setText("Edit");

    deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, 0));
    deleteMenuItem.setText("Delete");
    deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteMenuItemActionPerformed(evt);
      }
    });
    editMenu.add(deleteMenuItem);

    mainMenuBar.add(editMenu);

    listMenu.setText("List");

    findMenuItem.setText("Find");
    findMenuItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_F,
      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
  findMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      findMenuItemActionPerformed(evt);
    }
  });
  listMenu.add(findMenuItem);

  replaceMenuItem.setText("Replace...");
  replaceMenuItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_R,
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
replaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
  public void actionPerformed(java.awt.event.ActionEvent evt) {
    replaceMenuItemActionPerformed(evt);
  }
  });
  listMenu.add(replaceMenuItem);
  listMenu.add(jSeparator10);

  addReplaceMenuItem.setText("Add/Replace Tag");
  addReplaceMenuItem.setToolTipText("Add/Replace a tag on all items on which it occurs");
  addReplaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      addReplaceMenuItemActionPerformed(evt);
    }
  });
  listMenu.add(addReplaceMenuItem);

  flattenTagsMenuItem.setText("Flatten Levels");
  flattenTagsMenuItem.setToolTipText("Remove levels from all tags for each URL, making each level a separate tag, and eliminating any duplicates. ");
  flattenTagsMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      flattenTagsMenuItemActionPerformed(evt);
    }
  });
  listMenu.add(flattenTagsMenuItem);

  lowerCaseTagsMenuItem.setText("Lower Case");
  lowerCaseTagsMenuItem.setToolTipText("Change all capital letters in tags to lower case");
  lowerCaseTagsMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      lowerCaseTagsMenuItemActionPerformed(evt);
    }
  });
  listMenu.add(lowerCaseTagsMenuItem);
  listMenu.add(jSeparator3);

  validateURLsMenuItem.setText("Validate URLs...");
  validateURLsMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      validateURLsMenuItemActionPerformed(evt);
    }
  });
  listMenu.add(validateURLsMenuItem);

  mainMenuBar.add(listMenu);

  URLMenu.setText("URL");

  nextMenuItem.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_CLOSE_BRACKET, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
  nextMenuItem.setText("Go to Next URL");
  nextMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      nextMenuItemActionPerformed(evt);
    }
  });
  URLMenu.add(nextMenuItem);

  priorMenuItem.setAccelerator(KeyStroke.getKeyStroke (KeyEvent.VK_OPEN_BRACKET, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
  priorMenuItem.setText("Go to Previous URL");
  priorMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      priorMenuItemActionPerformed(evt);
    }
  });
  URLMenu.add(priorMenuItem);

  mainMenuBar.add(URLMenu);

  toolsMenu.setText("Tools");

  toolsOptionsMenuItem.setText("Options...");
  toolsOptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      toolsOptionsMenuItemActionPerformed(evt);
    }
  });
  toolsMenu.add(toolsOptionsMenuItem);

  mainMenuBar.add(toolsMenu);

  windowMenu.setText("Window");
  mainMenuBar.add(windowMenu);

  helpMenu.setText("Help");
  mainMenuBar.add(helpMenu);

  setJMenuBar(mainMenuBar);

  pack();
  }// </editor-fold>//GEN-END:initComponents

private void launchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_launchButtonActionPerformed
  openURL (urlText.getText());
}//GEN-LAST:event_launchButtonActionPerformed

private void fileNewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNewMenuItemActionPerformed
  newFile();
}//GEN-LAST:event_fileNewMenuItemActionPerformed

private void urlNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlNewButtonActionPerformed
  newURL();
}//GEN-LAST:event_urlNewButtonActionPerformed

private void urlDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlDeleteButtonActionPerformed
  removeURL();
}//GEN-LAST:event_urlDeleteButtonActionPerformed

private void urlFirstButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlFirstButtonAction
  firstURL();
}//GEN-LAST:event_urlFirstButtonAction

private void urlPriorButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlPriorButtonAction
  priorURL();
}//GEN-LAST:event_urlPriorButtonAction

private void urlNextButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlNextButtonAction
  nextURL();
}//GEN-LAST:event_urlNextButtonAction

private void urlLastButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlLastButtonAction
  lastURL();
}//GEN-LAST:event_urlLastButtonAction

private void fileSaveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSaveAsMenuItemActionPerformed
  saveFileAs ();
}//GEN-LAST:event_fileSaveAsMenuItemActionPerformed

private void fileSaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSaveMenuItemActionPerformed
  saveFile();
}//GEN-LAST:event_fileSaveMenuItemActionPerformed

private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
  openFile();
}//GEN-LAST:event_openMenuItemActionPerformed

private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
  importFile();
}//GEN-LAST:event_importMenuItemActionPerformed

private void urlTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_urlTableMouseClicked
  selectTableRow();
}//GEN-LAST:event_urlTableMouseClicked

private void urlTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_urlTableKeyReleased
  selectTableRow();
}//GEN-LAST:event_urlTableKeyReleased

private void fileBackupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileBackupMenuItemActionPerformed
  if (urlFile != null && urlFile.exists()) {
    promptForBackup();
  }
}//GEN-LAST:event_fileBackupMenuItemActionPerformed

private void findTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findTextActionPerformed
    findURL();
}//GEN-LAST:event_findTextActionPerformed

private void findTextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_findTextKeyTyped
    if (! findText.getText().equals (lastTextFound)) {
      noFindInProgress();
    }
}//GEN-LAST:event_findTextKeyTyped

private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
  findURL();
}//GEN-LAST:event_findButtonActionPerformed

private void findMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findMenuItemActionPerformed
  findURL();
}//GEN-LAST:event_findMenuItemActionPerformed

private void urlOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlOKButtonActionPerformed
    modIfChanged();
    positionAndDisplay();
}//GEN-LAST:event_urlOKButtonActionPerformed

private void flattenTagsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenTagsMenuItemActionPerformed
  flattenTags();
}//GEN-LAST:event_flattenTagsMenuItemActionPerformed

private void lowerCaseTagsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowerCaseTagsMenuItemActionPerformed
  lowerCaseTags();
}//GEN-LAST:event_lowerCaseTagsMenuItemActionPerformed

private void addReplaceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addReplaceMenuItemActionPerformed
  checkTags();
}//GEN-LAST:event_addReplaceMenuItemActionPerformed

private void validateURLsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateURLsMenuItemActionPerformed
  validateURLs();
}//GEN-LAST:event_validateURLsMenuItemActionPerformed

private void propertiesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertiesMenuItemActionPerformed
  setUnsavedChanges (true);
  displayAuxiliaryWindow(collectionWindow);
}//GEN-LAST:event_propertiesMenuItemActionPerformed

private void reloadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadMenuItemActionPerformed
  reloadFile();
}//GEN-LAST:event_reloadMenuItemActionPerformed

private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
  removeURL();
}//GEN-LAST:event_deleteMenuItemActionPerformed

private void nextMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextMenuItemActionPerformed
  nextURL();
}//GEN-LAST:event_nextMenuItemActionPerformed

private void priorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priorMenuItemActionPerformed
  priorURL();
}//GEN-LAST:event_priorMenuItemActionPerformed

private void toolsOptionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolsOptionsMenuItemActionPerformed
  this.handlePreferences();
}//GEN-LAST:event_toolsOptionsMenuItemActionPerformed

private void publishWindowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publishWindowMenuItemActionPerformed
  displayPublishWindow();
}//GEN-LAST:event_publishWindowMenuItemActionPerformed

  private void publishNowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publishNowMenuItemActionPerformed
    publishWindow.publishNow();
  }//GEN-LAST:event_publishNowMenuItemActionPerformed

  private void replaceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceMenuItemActionPerformed
    startReplace();
  }//GEN-LAST:event_replaceMenuItemActionPerformed

  private void clearMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuItemActionPerformed
    clearFile();
  }//GEN-LAST:event_clearMenuItemActionPerformed

  private void exportNoteNikMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportNoteNikMenuItemActionPerformed
    exportToNoteNik();
  }//GEN-LAST:event_exportNoteNikMenuItemActionPerformed



  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenu URLMenu;
  private javax.swing.JMenuItem addReplaceMenuItem;
  private javax.swing.JMenuItem clearMenuItem;
  private javax.swing.JTabbedPane collectionTabbedPane;
  private javax.swing.JLabel commentsLabel;
  private javax.swing.JScrollPane commentsScrollPane;
  private javax.swing.JTextArea commentsText;
  private javax.swing.JMenuItem deleteMenuItem;
  private javax.swing.JMenu editMenu;
  private javax.swing.JMenu exportMenu;
  private javax.swing.JMenuItem exportNoteNikMenuItem;
  private javax.swing.JMenuItem fileBackupMenuItem;
  private javax.swing.JMenu fileMenu;
  private javax.swing.JMenuItem fileNewMenuItem;
  private javax.swing.JMenuItem fileSaveAsMenuItem;
  private javax.swing.JMenuItem fileSaveMenuItem;
  private javax.swing.JButton findButton;
  private javax.swing.JMenuItem findMenuItem;
  private javax.swing.JTextField findText;
  private javax.swing.JMenuItem flattenTagsMenuItem;
  private javax.swing.JMenu helpMenu;
  private javax.swing.JMenuItem importMenuItem;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JPopupMenu.Separator jSeparator10;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JSeparator jSeparator4;
  private javax.swing.JSeparator jSeparator5;
  private javax.swing.JPopupMenu.Separator jSeparator6;
  private javax.swing.JLabel lastModDateLabel;
  private javax.swing.JLabel lastModDateText;
  private javax.swing.JButton launchButton;
  private javax.swing.JPanel linkPanel;
  private javax.swing.JMenu listMenu;
  private javax.swing.JPanel listPanel;
  private javax.swing.JMenuItem lowerCaseTagsMenuItem;
  private javax.swing.JMenuBar mainMenuBar;
  private javax.swing.JSplitPane mainSplitPane;
  private javax.swing.JToolBar mainToolBar;
  private javax.swing.JMenuItem nextMenuItem;
  private javax.swing.JMenuItem openMenuItem;
  private javax.swing.JMenu openRecentMenu;
  private javax.swing.JMenuItem priorMenuItem;
  private javax.swing.JMenuItem propertiesMenuItem;
  private javax.swing.JMenuItem publishNowMenuItem;
  private javax.swing.JMenuItem publishWindowMenuItem;
  private javax.swing.JMenuItem reloadMenuItem;
  private javax.swing.JMenuItem replaceMenuItem;
  private javax.swing.JScrollPane tableScrollPane;
  private javax.swing.JLabel tagsLabel;
  private javax.swing.JLabel titleLabel;
  private javax.swing.JTextField titleText;
  private javax.swing.JMenu toolsMenu;
  private javax.swing.JMenuItem toolsOptionsMenuItem;
  private javax.swing.JPanel treePanel;
  private javax.swing.JScrollPane treeScrollPane;
  private javax.swing.JButton urlDeleteButton;
  private javax.swing.JButton urlFirstButton;
  private javax.swing.JButton urlLastButton;
  private javax.swing.JButton urlNewButton;
  private javax.swing.JButton urlNextButton;
  private javax.swing.JButton urlOKButton;
  private javax.swing.JButton urlPriorButton;
  private javax.swing.JScrollPane urlScrollPane;
  private javax.swing.JTable urlTable;
  private javax.swing.JTextArea urlText;
  private javax.swing.JTree urlTree;
  private javax.swing.JMenuItem validateURLsMenuItem;
  private javax.swing.JMenu windowMenu;
  // End of variables declaration//GEN-END:variables

}
