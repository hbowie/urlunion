<!-- Generated using template product-user-guide-template.mdtoc -->
<!-- Generated using template product-user-guide-template.md -->
<h1 id="url-union-user-guide">URL Union User Guide</h1>


<h2 id="table-of-contents">Table of Contents</h2>

<div id="toc">
  <ul>
    <li>
      <a href="#introduction">Introduction</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li>
          <a href="#system-requirements">System Requirements</a>
        </li>
        <li>
          <a href="#rights">Rights</a>
        </li>
        <li>
          <a href="#installation">Installation</a>
        </li>
      </ul>

    </li>
    <li>
      <a href="#data-fields">Data Fields</a>
    </li>
    <li>
      <a href="#file-operations">File Operations</a>
      <ul>
        <li>
          <a href="#creating-a-new-collection">Creating a New Collection</a>
        </li>
        <li>
          <a href="#importing-urls">Importing URLs</a>
        </li>
        <li>
          <a href="#saving-a-collection">Saving a Collection</a>
        </li>
        <li>
          <a href="#opening-recent-files">Opening Recent Files</a>
        </li>
      </ul>

    </li>
    <li>
      <a href="#user-interface">User Interface</a>
      <ul>
        <li>
          <a href="#the-tool-bar">The Tool Bar</a>
        </li>
        <li>
          <a href="#main-window">Main Window</a>
        </li>
      </ul>

    </li>
    <li>
      <a href="#tips-tricks-and-special-functions">Tips, Tricks and Special Functions</a>
      <ul>
        <li>
          <a href="#find-and-replace">Find and Replace</a>
        </li>
        <li>
          <a href="#publish">Publish</a>
        </li>
      </ul>

    </li>
    <li>
      <a href="#preferences">Preferences</a>
      <ul>
        <li>
          <a href="#general-prefs">General Prefs</a>
        </li>
        <li>
          <a href="#favorites-prefs">Favorites Prefs</a>
        </li>
      </ul>

    </li>
  </ul>

</div>


<h2 id="introduction">Introduction</h2>


URL Union is a desktop software application for storing and publishing URLs (aka bookmarks).

URL Union was written with the following design goals in mind.

1. **One set of bookmarks for all browsers and all devices.**  URL Union can publish your URLs in a variety of convenient HTML formats that allow you to view and use your URLs from any Web browser, including those found on an iPad, iPhone or other mobile device.

2. **Storing URLs in HTML.** It just seems perverse to me to store URLs in a proprietary, binary format, since HTML is the native, natural language for such links. URL Union stores its bookmarks in an HTML file that can be easily read by any text editor, and any Web browser. 

3. **Supporting both tags and folders.** It seems that many bookmark managers allow you to assign multiple, single-level tags to each URL. Other bookmark managers allow you to organize your URLs into nested folders, but typically allow each URL to appear in only one folder. URL Union allows you to assign multiple tags to each URL, and each tag can be nested up to 10 levels deep. 

4. **Intelligent import and merge capabilities.** Many bookmark managers allow you to add additional bookmarks to a central file via an import function, but simply add the new bookmarks to your existing list. URL Union will allow only one record for each unique URL, and will intelligently merge duplicates when they are added via an import. 

5. **Import from multiple sources.** URL Union supports import capabilities for multiple file formats, essentially allowing users to import their URLs from most existing sources in which they might be stored. 

6. **Export.** URL Union can easily publish your URLs in the traditional Netscape bookmarks format, which can be imported by almost any Web browser or other URL manager.


<h2 id="getting-started">Getting Started</h2>


<h3 id="system-requirements">System Requirements</h3>


URL Union is written in Java and can run on any reasonably modern operating system, including Mac OS X, Windows and Linux. URL Union requires a Java Runtime Environment (JRE), also known as a Java Virtual Machine (JVM). The version of this JRE/JVM must be at least 6. Visit [www.java.com](http://www.java.com) to download a recent version for most operating systems. Installation happens a bit differently under Mac OS X, but generally will occur fairly automatically when you try to launch a Java app for the first time.

Because URL Union may be run on multiple platforms, it may look slightly different on different operating systems, and will obey slightly different conventions (using the CMD key on a Mac, vs. an ALT key on a PC, for example).

<h3 id="rights">Rights</h3>


URL Union Copyright 2009 - 2014 by Herb Bowie

URL Union is [open source software](http://opensource.org/osd). Source code is available at [GitHub](http://github.com/hbowie/urlunion).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

  [www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

URL Union also incorporates or adapts the following open source software libraries.

* JExcelAPI — Copyright 2002 Andrew Khan, used under the terms of the [GNU General Public License](http://www.gnu.org/licenses/).


<h3 id="installation">Installation</h3>


Download the latest version from [PowerSurgePub.com](http://www.powersurgepub.com/downloads.html). Decompress the downloaded file. Drag the resulting file or folder into the location where you normally store your applications. Double-click on the jar file (or the application, if you've downloaded the Mac app) to launch.


<h2 id="data-fields">Data Fields</h2>


URL Union works with bookmarks (aka hyperlinks, aka URLs).

URL Union maintains the following fields for each URL.

Title
:    A brief title for the Web site or page referenced by the URL.

URL
:    The URL itself.

Tags
:    Use a period to nest one tag within another. Use a comma to separate multiple tags. The "Favorites" tag by default identifies URLs you wish to appear in your favorites file (see the **Publish** section below). The "Startup" tag identifies URLs you wish to have launched in your favorite Web Browser whenever URL Union launches, so long as this is requested in your Preferences.

Comments
:    Enter whatever you like here, or leave it blank.

<h2 id="file-operations">File Operations</h2>


File operations may be accessed via the File menu.

<h3 id="creating-a-new-collection">Creating a New Collection</h3>


The first time you launch URL Union, or after selecting **New** from the **File** menu, you will see a fresh collection of URLs, containing only a single entry, for PowerSurge Publishing. New users will  have their first set of URLs saved automatically for them in a default location, in a "URL Union" folder within their Documents folder.

<h3 id="importing-urls">Importing URLs</h3>


Select **Import** from the **File** menu to import existing bookmarks.

You must specify an existing file to import. This file may be one of two types.

HTML
:    This should be a bookmark file. You may need to export your URLs from some other program, into an HTML file, before importing them into URL Union. URL Union can import bookmarks from an HTML file in the traditional <a href="http://msdn.microsoft.com/en-us/library/aa753582(v=VS.85).aspx">Netscape format</a>), as well as from an HTML file formatted by the <a href="http://www.delicious.com">Delicious</a> social bookmarking service.

    URL Union comes with an AppleScript file that can be used to export bookmarks from [Yojimbo](http://www.barebones.com/products/Yojimbo/) into an HTML file that is acceptable for import by URL Union. Simply open the AppleScript file using AppleScript Editor, and then click the Run button. Be patient when exporting from Yojimbo. It may take several minutes for the AppleScript to complete its execution.

List
:    You may also import URLs in a file containing columns and rows, in either tab-delimited or xls format. The columns may be in any order, and other columns may be present, but URL Union will look for the first column headings containing the following words, and then import the contents of those columns into their corresponding fields in URL Union.

	* title
	* url
	* tags
	* comments or notes

<h3 id="saving-a-collection">Saving a Collection</h3>


After selecting **Save** from the **File** menu with a new collection, or after selecting **Save As** from the **File** menu, you will be prompted to select a location at which to store your collection.

The default file name will be urlunion.html, and it is recommended (although not required) that you use this default name.

You may pick any folder you like in which to store your URL Union file, but it is recommended that you store your file in a new, empty folder especially created for this purpose. When you later use the Publish menu command, other files will be created for you, optionally in the same folder, and it will generally be cleaner if this folder is devoted to storage of URL Union files.

<h3 id="opening-recent-files">Opening Recent Files</h3>


On subsequent launches, URL Union will automatically open the last URL Union collection you used. You may also open a recent file by selecting **Open Recent** from the **File** menu.

<h2 id="user-interface">User Interface</h2>



<h3 id="the-tool-bar">The Tool Bar</h3>


A toolbar with multiple buttons appears at the top of the user interface.

* **OK** -- Indicates that you have completed adding/editing the fields for the current URL.
* **+** -- Clear the data fields and prepare to add a new URL to the collection.
* **-** -- Delete the current URL.
* **&lt;&lt;** -- Display the first URL in the collection.
* **&lt;** -- Display the next URL in the collection.
* **&gt;** -- Display the next URL in the collection.
* **&gt;&gt;** -- Display the last URL in the collection.
* **Launch** -- Launch the current URL in your Web browser. (This may also be accomplished by clicking the arrow that appears just to the left of the URL itself.)
* **Find** -- Looks for the text entered in the field just to the left of this button, and displays the first URL containing this text in any field, ignoring case. After finding the first occurrence, this button's text changes to **Again**, to allow you to search again for the next URL containing the specified text.

<h3 id="main-window">Main Window</h3>


The main window contains three different panes.

<h4 id="the-list">The List</h4>


On the first half of the main window, you'll see two tabs. The first of these displays the **List**. This is just a simple list of all your URLs. You can rearrange/resize columns. You can't sort by other columns. Click on a row to select that URL for display on the other half of the main window. Use the entries on the **View** menu to select a different sorting/filtering option. Use the **View Preferences** to modify your view options.

<h4 id="tags">Tags</h4>


The second Tab on the first half of the main window displays the **Tags**. This is an indented list of all your Tags, with URLs appearing under as many Tags as have been assigned to them, and with URLs with no Tags displaying at the very top. Click to the left of a Tag to expand it, showing URLs and/or sub-tags contained within it.

Note that Tags that were once used, but that are used no more, will stick around until you close the URL Union file and re-open it. If you wish, you may accelerate this process by selecting **Reload** from the **File** menu.

<h4 id="details">Details</h4>


The detailed data for the currently selected URL appears on the second half of the main window.


<h2 id="tips-tricks-and-special-functions">Tips, Tricks and Special Functions</h2>


<h3 id="find-and-replace">Find and Replace</h3>


A **Replace** option can be found under the **List** menu. Selecting this menu item opens a Replace Window. The window allows the user to specify which fields to search, and whether case sensitivity is desired. From this window the user has buttons to **Find**, **Replace**, **Replace & Find** or **Replace All**.

<h3 id="publish">Publish</h3>


The publish option allows you to easily publish your URLs in a variety of useful formats.

To begin the publication process, select the **Publish...** command from the **File** menu.

You will then see a window with the following fields available to you.

Publish to
:    You may use the Browse button above and to the right to select a folder on your computer to which you wish to publish your URLs. You may also enter or modify the path directly in the text box. When modifying this field, you will be prompted to specify whether you wish to update the existing publication location, or add a new one. By specifying that you wish to add a new one, you may create multiple publications, and then later select the publication of interest by using the drop-down arrow to the right of this field.

Equivalent URL
:    If the folder to which you are publishing will be addressable from the World-Wide Web, then enter its Web address here.

Templates
:    This is the address of a folder containing one or more publishing templates. This will default to the location of the templates provided along with the application executable. You may use the Browse button above and to the right to pick a different location, if you have your own templates you wish to use for publishing.

Select
:    Use the drop-down list to select the template you wish to use.

	**Favorites Plus**: This template will produce the following files and formats.

	1. index.html -- This file is an index file with links to the other files. You can browse this locally by selecting **Browse local index** from the **File** menu.
	 2. favorites.html -- This file tries to arrange all of the URLs you have tagged as "Favorites" into a four-column format that will fit on a single page.
	 3. bookmark.html -- This file formats your URLs in the time-honored Netscape bookmarks format, suitable for import into almost any Web browser or URL manager.
	 4. outline.html -- This is a dynamic html file that organizes your URLs within your tags, allowing you to reveal/disclose selected tags.

Apply
:    Press this button to apply the selected template. This will copy the contents of the template folder to the location specified above as the Publish to location.

Publish Script
:    Specify the location of the script to be used. The PSTextMerge templating system is the primary scripting language used for publishing. A PSTextMerge script will usually end with a '.tcz' file extension.

Publish when
:    You may specify publication 'On Close' (whenever you Quit the application or close a data collection), 'On Save' (whenever you save the data collection to disk), or 'On Demand'.

Publish Now
:    Press this button to publish to the currently displayed location. Note that, if you've specified 'On Demand', then this is the only time that publication will occur.

View
:    Select the local file location or the equivalent URL location.

View Now
:    Press this button to view the resulting Web site in your Web browser.

<h2 id="preferences">Preferences</h2>


The following preference tabs are available.

<h3 id="general-prefs">General Prefs</h3>


The program's General Preferences contain a number of options for modifying the program's look and feel. Feel free to experiment with these to find your favorite configuration. Some options may require you to quit and re-launch URL Union before the changes will take effect.

SplitPane: Horizontal Split?
:    Check the box to have the **List** and **Tags** appear on the left of the main screen, rather than the top.

Deletion: Confirm Deletes?
:    Check the box to have a confirmation dialog shown whenever you attempt to delete the selected URL.

Software Updates: Check Automatically?
:    Check the box to have URL Union check for newer versions whenever it launches.

Check Now
:    Click this button to check for a new version immediately.

File Chooser
:    If running on a Mac, you may wish to select AWT rather than Swing, to make your Open and Save dialogs appear more Mac-like. However, Swing dialogs may still appear to handle options that can't be handled by the native AWT chooser.

Look and Feel
:    Select from one of the available options to change the overall look and feel of the application.

Menu Location
:    If running on a Mac, you may wish to have the menus appear at the top of the screen, rather than at the top of the window.

<h3 id="favorites-prefs">Favorites Prefs</h3>


Open Startup Tags at Program Launch?
:    Indicate whether you want URLs tagged with "Startup" launched within your Web browser whenever URL Union starts.

Favorites Tags
:    Specify the tags that you'd like Favorites pages to be generated for. The default is 'Favorites', but you may specify whatever you'd like here, separating separate tags with commas. Each tag identified here will have a separate page generated with a name matching the tag.

Home Link
:    Specify the desired link from the Favorites page to a Home page.

Favorites Columns
:    Specify the number of columns you wish to appear on the Favorites page.

Favorites Rows
:    Specify the maximum number of rows you wish to appear on the Favorites page.



[java]:       http://www.java.com/
[pspub]:      http://www.powersurgepub.com/
[downloads]:  http://www.powersurgepub.com/downloads.html
[osd]:		  http://opensource.org/osd
[gnu]:        http://www.gnu.org/licenses/
[apache]:	     http://www.apache.org/licenses/LICENSE-2.0.html
[markdown]:		http://daringfireball.net/projects/markdown/
[multimarkdown]:  http://fletcher.github.com/peg-multimarkdown/

[wikiq]:     http://www.wikiquote.org
[support]:   mailto:support@powersurgepub.com
[fortune]:   http://en.wikipedia.org/wiki/Fortune_(Unix)
[opml]:      http://en.wikipedia.org/wiki/OPML
[textile]:   http://en.wikipedia.org/wiki/Textile_(markup_language)
[pw]:        http://www.portablewisdom.org

[store]:     http://www.powersurgepub.com/store.html

[pegdown]:   https://github.com/sirthias/pegdown/blob/master/LICENSE
[parboiled]: https://github.com/sirthias/parboiled/blob/master/LICENSE
[Mathias]:   https://github.com/sirthias

[club]:         clubplanner.html
[filedir]:      filedir.html
[metamarkdown]: metamarkdown.html
[template]:     template.html

[mozilla]:    http://www.mozilla.org/MPL/2.0/


