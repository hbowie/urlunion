URL Union User Guide
====================

<ul>
<li><a href="#introduction">Introduction</a></li>
<li><a href="#sysrqmts">System Requirements</a></li>
<li><a href="#rights">Rights</a></li>
<li><a href="#installation">Installation</a></li>
<li><a href="#createnew">Creating a New File</a></li>
<li><a href="#import">Importing URLs</a></li>
<li><a href="#save">Saving a URL Collection</a></li>
<li><a href="#openrecent">Opening Recent Files</a></li>
<li><a href="#data">The Four Data Fields</a></li>
<li><a href="#toolbar">The Tool Bar</a></li>
<li><a href="#list">The List</a></li>
<li><a href="#tags">Tags</a></li>
<li><a href="#replace">Find and Replace</a></li>
<li><a href="#publish">Publish</a></li>
<li><a href="#prefs">Preferences</a></li>
</ul>

<h2 id="introduction">Introduction</h2>

URL Union is a desktop software program for storing and publishing URLs (aka bookmarks).

URL Union was written with the following design goals in mind.

1. **One set of bookmarks for all browsers and all devices.**  URL Union can publish your URLs in a variety of convenient HTML formats that allow you to view and use your URLs from any Web browser, including those found on an iPad, iPhone or other mobile device.

2. **Storing URLs in HTML.** It just seems perverse to me to store URLs in a proprietary, binary format, since HTML is the native, natural language for such links. URL Union stores its bookmarks in an HTML file that can be easily read by any text editor, and any Web browser. 

3. **Supporting both tags and folders.** It seems that many bookmark managers allow you to assign multiple, single-level tags to each URL. Other bookmark managers allow you to organize your URLs into nested folders, but typically allow each URL to appear in only one folder. URL Union allows you to assign multiple tags to each URL, and each tag can be nested up to 10 levels deep. 

4. **Intelligent import and merge capabilities.** Many bookmark managers allow you to add additional bookmarks to a central file via an import function, but simply add the new bookmarks to your existing list. URL Union will allow only one record for each unique URL, and will intelligently merge duplicates when they are added via an import. 

5. **Import from multiple sources.** URL Union supports import capabilities for multiple file formats, essentially allowing users to import their URLs from most existing sources in which they might be stored. 

6. **Export.** URL Union can easily publish your URLs in the traditional Netscape bookmarks format, which can be imported by almost any Web browser or other URL manager.

<h2 id="sysrqmts">System Requirements</h2>

URL Union is written in Java and can run on any reasonably modern operating system, including Mac OS X, Windows and Linux. URL Union requires a Java Runtime Environment (JRE), also known as a Java Virtual Machine (JVM). The version of this JRE/JVM must be at least 6. Visit [www.java.com][java] to download a recent version for most operating systems. Installation happens a bit differently under Mac OS X, but generally will occur fairly automatically when you try to launch a Java app for the first time. 

<h2 id="rights">Rights</h2>

URL Union Copyright &copy; 2009 - 2013 Herb Bowie

As of version 2.00, URL Union is [open source software][osd]. 

Licensed under the Apache License, Version 2.0 (the &#8220;License&#8221;); you may not use this file except in compliance with the License. You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

URL Union also incorporates or adapts the following open source software libraries. 

* BrowserLauncher2 &#8212; Copyright 2004 - 2007 Markus Gebhard, Jeff Chapman, used under the terms of the [GNU General Public License][gnu]. 

* JExcelAPI &#8212; Copyright 2002 Andrew Khan, used under the terms of the [GNU General Public License][gnu]. 

<h2 id="installation">Installation</h2>

Download the latest version from [PowerSurgePub.com](http://www.powersurgepub.com/). Decompress the downloaded file. Drag the resulting file or folder into the location where you normally store your applications.

<h2 id="createnew">Creating a New File</h2>

The first time you launch URL Union, or after selecting <em>New</em> from the <em>File</em> menu, you will see a fresh collection of URLs, containing only a single entry, for PowerSurge Publishing. New users will  have their first set of bookmarks saved automatically for them in a default location, in a "URL Union" folder within their Documents folder.

<h2 id="import">Importing URLs</h2>

Select **Import** from the **File** menu to import existing bookmarks.

You must specify an existing file to import. This file may be one of two types. 

### HTML

This should be a bookmark file. You may need to export your URLs from some other program, into an HTML file, before importing them into URL Union. URL Union can import bookmarks from an HTML file in the traditional <a href="http://msdn.microsoft.com/en-us/library/aa753582(v=VS.85).aspx">Netscape format</a>), as well as from an HTML file formatted by the <a href="http://www.delicious.com">Delicious</a> social bookmarking service. 

URL Union comes with an AppleScript file that can be used to export bookmarks from [Yojimbo](http://www.barebones.com/products/Yojimbo/) into an HTML file that is acceptable for import by URL Union. Simply open the AppleScript file using AppleScript Editor, and then click the Run button. Be patient when exporting from Yojimbo. It may take several minutes for the AppleScript to complete its execution.

### List

You may also import URLs in a file containing columns and rows, in either tab-delimited or xls format. The columns may be in any order, and other columns may be present, but URL Union will look for the first column headings containing the following words, and then import the contents of those columns into their corresponding fields in URL Union. 

* title
* url
* tags
* comments or notes

<h2 id="save">Saving a URL Collection</h2>

After selecting **Save** from the **File** menu with a new collection, or after selecting **Save As** from the **File** menu, you will be prompted to select a location at which to store your collection.

The default file name will be urlunion.html, and it is recommended (although not required) that you use this default name.

You may pick any folder you like in which to store your URL Union file, but it is recommended that you store your file in a new, empty folder especially created for this purpose. When you later use the Publish menu command, other files will be created for you, optionally in the same folder, and it will generally be cleaner if this folder is devoted to storage of URL Union files.

<h2 id="openrecent">Opening Recent Files</h2>

On subsequent launches, URL Union will automatically open the last URL Union collection you used. You may also open a recent file by selecting **Open Recent** from the **File** menu.

<h2 id="data">The Four Data Fields</h2>

For each URL, you may enter the following fields.

* **Title** -- A brief title for the Web site or page referenced by the URL.

* **URL** -- The URL itself.

* **Tags** -- Use a period to nest one tag within another. Use a comma to separate multiple tags. The "Favorites" tag by default identifies URLs you wish to appear in your favorites file (see the **Publish** section below). The "Startup" tag identifies URLs you wish to have launched in your favorite Web Browser whenever URL Union launches, so long as this is requested in your Preferences. 

* **Comments** -- Enter whatever you like here, or leave it blank.

<h2 id="toolbar">The Tool Bar</h2>

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

<h2 id="list">The List</h2>

On the first half of the main screen, you'll see two tabs. The first of these displays the **List**. This is just a simple list of all your URLs. The URL column appears first, and the list is sorted by domain name ("Google", etc.). You can rearrange/resize columns. You can't sort by other columns. Click on a row to select that URL for display on the other half of the main screen.

<h2 id="tags">Tags</h2>

The second Tab on the first half of the main screen displays the **Tags**. This is an indented list of all your tags, with URLs appearing under as many tags as have been assigned to them, and with URLs with no tags displaying at the very top. Click to the left of a tag to expand it, showing URLs and/or sub-tags contained within it.

Note that tags that were once used, but that are used no more, will stick around until you close the URL Union file and re-open it. If you wish, you may accelerate this process by selecting **Reload** from the **File** menu. 

<h2 id="replace">Find and Replace</h2>

A **Replace** option can be found under the **List** menu. Selecting this menu item opens a Replace Window. The window allows the user to specify which fields to search, and whether case sensitivity is desired. From this window the user has buttons to **Find**, **Replace**, **Replace & Find** or **Replace All**. 

<h2 id="publish">Publish</h2>

The publish option allows you to easily publish your URLs in a variety of useful formats.

To begin the publication process, select the **Publish...** command from the **File** menu.

You will then see a window with the following fields available to you.

<dl>
	<dt>Publish to:</dt>

	<dd>You may use the Browse button above and to the right to select a folder on your computer to which you wish to publish your URLs. You may also enter or modify the path directly in the text box. When modifying this field, you will be prompted to specify whether you wish to update the existing publication location, or add a new one. By specifying that you wish to add a new one, you may create multiple publications, and then later select the publication of interest by using the drop-down arrow to the right of this field. </dd>

	<dt>Equivalent URL:</dt>
	<dd>If the folder to which you are publishing will be addressable from the World-Wide Web, then enter its Web address here. </dd>
	<dt>Templates:</dt>
	<dd>This is the address of a folder containing one or more publishing templates. This will default to the location of the templates provided along with the application executable. You may use the Browse button above and to the right to pick a different location, if you have your own templates you wish to use for publishing. </dd>
	<dt>Select:</dt>
	<dd>Use the drop-down list to select the template you wish to use.
		<ul>
			<li>Favorites Plus -- This template will produce the following files and formats.
				<ul>
                    <li><strong>index.html</strong> -- This file is an index file with links to the other files. You can browse this locally by selecting <strong>Browse local index...</strong> from the <strong>File</strong> menu. </li>
                    <li><strong>favorites.html</strong> -- This file tries to arrange all of the URLs you have tagged as "Favorites" into a four-column format that will fit on a single page.</li>
                    <li><strong>bookmark.html</strong> -- This file formats your URLs in the time-honored Netscape bookmarks format, suitable for import into almost any Web browser or URL manager.  </li>
                    <li><strong>outline.html</strong> -- This is a dynamic html file that organizes your URLs within your tags, allowing you to reveal/disclose selected tags.</li>
				</ul>
			 </li>
		</ul>
	</dd>
	<dt>Apply</dt>
	<dd>Press this button to apply the selected template. This will copy the contents of the template folder to the location specified above as the Publish to location. </dd>
	<dt>Publish Script:</dt>
	<dd>Specify the location of the script to be used. The <a href="http://www.powersurgepub.com/products/pstextmerge.html">PSTextMerge</a> templating system is the primary scripting language used for publishing. A PSTextMerge script will usually end with a '.tcz' file extension.  </dd>
	<dt>Publish when:</dt>
	<dd>You may specify publication 'On Close' (whenever you Quit the application or close a data collection), 'On Save' (whenever you save the data collection to disk), or 'On Demand'.  </dd>
	<dt>Publish Now</dt>
	<dd>Press this button to publish to the currently displayed location. Note that, if you've specified 'On Demand', then this is the only time that publication will occur. </dd>
	<dt>View:</dt>
	<dd>Select the local file location or the equivalent URL location.</dd>
	<dt>View Now</dt>
	<dd>Press this button to view the resulting Web site in your Web browser.</dd>
</dl>

<h2 id="prefs">Preferences</h2>

The following preference tabs are available.

<h3 id="general_prefs">General Prefs</h3>

* **SplitPane: Horizontal Split?** --  Check the box to have the **List** and **Tags** appear on the left of the main screen, rather than the top.
* **Deletion: Confirm Deletes?** -- Check the box to have a confirmation dialog shown whenever you attempt to delete a URL.

* **Software Updates: Check Automatically?** -- Check the box to have URL Union check for newer versions whenever it launches.

* **Check Now** -- Click this button to check for a new version immediately.
* **File Chooser** -- If running on a Mac, you may wish to select AWT rather than Swing, to make your Open and Save dialogs appear more Mac-like. However, Swing dialogs may still appear to handle options that can't be handled by the native AWT chooser. 
* **Look and Feel** -- Select from one of the available options to change the overall look and feel of the application.

* **Menu Location** -- If running on a Mac, you may wish to have the menus appear at the top of the screen, rather than at the top of the window.

<h3 id="favorites_prefs">Favorites Prefs</h3>

* **Open Startup Tags at Program Launch?** -- Indicate whether you want URLs tagged with "Startup" launched within your Web browser whenever URL Union starts.
* **Favorites Tags** -- Specify the tags that you'd like Favorites pages to be generated for. The default is 'Favorites', but you may specify whatever you'd like here, separating separate tags with commas. Each tag identified here will have a separate page generated with a name matching the tag. 
* **Favorites Columns** -- Specify the number of columns you wish to appear on the Favorites page.

* **Favorites Rows** -- Specify the maximum number of rows you wish to appear on the Favorites page.

[java]:  http://www.java.com/

[pspub]:     http://www.powersurgepub.com/
[downloads]: http://www.powersurgepub.com/downloads.html
[store]:     http://www.powersurgepub.com/store.html

[markdown]:  http://daringfireball.net/projects/markdown/
[pegdown]:   https://github.com/sirthias/pegdown/blob/master/LICENSE
[parboiled]: https://github.com/sirthias/parboiled/blob/master/LICENSE
[Mathias]:   https://github.com/sirthias

[club]:         clubplanner.html
[filedir]:      filedir.html
[metamarkdown]: metamarkdown.html
[template]:     template.html

[osd]:				http://opensource.org/osd
[gnu]:        http://www.gnu.org/licenses/
[apache]:			http://www.apache.org/licenses/LICENSE-2.0.html
