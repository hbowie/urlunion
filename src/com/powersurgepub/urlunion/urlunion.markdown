<h1 id="url_union_user_guide">URL Union User Guide</h1>

<ul>
<li><a href="#introduction">Introduction</a></li>
<li><a href="#sysrqmts">System Requirements</a></li>
<li><a href="#license">License</a></li>
<li><a href="#installation">Installation</a></li>
<li><a href="#createnew">Creating a New File</a></li>
<li><a href="#import">Importing URLs</a></li>
<li><a href="#save">Saving a URL Collection</a></li>
<li><a href="#openrecent">Opening Recent Files</a></li>
<li><a href="#data">The Four Data Fields</a></li>
<li><a href="#toolbar">The Tool Bar</a></li>
<li><a href="#list">The List</a></li>
<li><a href="#tags">Tags</a></li>
<li><a href="#publish">Publish</a></li>
<li><a href="#prefs">Preferences</a></li>
</ul>

<h2 id="introduction">Introduction</h2>

URL Union is a desktop software program for storing URLs (aka bookmarks). 

URL Union was written with the following design goals in mind.

1. **Storing URLs in HTML.** It just seems perverse to me to store URLs in a proprietary, binary format, since HTML is the native, natural language for such links. URL Union stores its bookmarks in an HTML file that can be easily read by any text editor, and any Web browser. 

2. **Supporting both tags and folders.** It seems that many bookmark managers allow you to assign multiple, single-level tags to each URL. Other bookmark managers allow you to organize your URLs into nested folders, but typically allow each URL to appear in only one folder. URL Union allows you to assign multiple tags to each URL, and each tag can be nested up to 10 levels deep. 

3. **Intelligent import and merge capabilities.** Many bookmark managers allow you to add additional bookmarks to a central file via an import function, but simply add the new bookmarks to your existing list. URL Union will allow only one record for each unique URL, and will intelligently merge duplicates when they are added via an import. 

4. **Import from multiple sources.** URL Union supports import capabilities for multiple file formats, essentially allowing users to import their URLs from most existing sources in which they might be stored. 

5. **Viewing URLs via HTML.** URL Union can publish your URLs in a variety of convenient HTML formats that allow you to view and use your URLs from any Web browser, including those found on an iPad, iPhone or other mobile device.

6. **Export.** URL Union can easily publish your URLs in the traditional Netscape bookmarks format, which can be imported by almost any Web browser or other URL manager. 

<h2 id="sysrqmts">System Requirements</h2>

URL Union is written in Java and can run on any reasonably modern operating system, including Mac OS X, Windows and Linux. URL Union requires a Java Runtime Environment (JRE), also known as a Java Virtual Machine (JVM). The version of this JRE/JVM must be at least 1.5. Visit [www.java.com](http://www.java.com) to download a recent version for most operating systems. The JVM is pre-installed as part of Mac OS X, and may be updated from the Software Update panel. 

<h2 id="license">License</h2>

When running in demonstration mode, URL Union will store no more than 20 URLs. Please visit our [store](http://www.powersurgepub.com/store.html) to purchase a license and remove the demo restrictions.

Copyright &copy; 2009 - 2011, Herb Bowie   
[PowerSurgePub.com](http://www.powersurgepub.com/)   
All rights reserved.

Redistribution and use in binary forms, without modification, is permitted provided that the software retains attribution using the above copyright notice.

This software is provided "as is" and any express or implied warranties, including, but not limited to, the implied warranties of merchantability and fitness for a particular purpose, are disclaimed. In no event shall the copyright owner or contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential damages (including, but not limited to, procurement of substitute goods or services; loss of use, data, or profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this software, even if advised of the possibility of such damage.

<h2 id="installation">Installation</h2>

Download the latest version from [PowerSurgePub.com](http://www.powersurgepub.com/). Decompress the downloaded file. Drag the resulting file or folder into the location where you normally store your applications. 

<h2 id="createnew">Creating a New File</h2>

The first time you launch URL Union, or after selecting *New* from the *File* menu, you will see a fresh collection of URLs, containing only a single entry, for PowerSurge Publishing. 

<h2 id="import">Importing URLs</h2>

Select **Import** from the **File** menu to import existing bookmarks. 

You must specify an existing bookmark file to import. The bookmark file to be imported should be an HTML file. You may need to export your URLs from some other program, into an HTML file, before importing them into URL Union. URL Union can import bookmarks from an HTML file in the traditional [Netscape format](http://msdn.microsoft.com/en-us/library/aa753582(VS.85).aspx), as well as from an HTML file formatted by the [Delicious](http://www.delicious.com) social bookmarking service. 

URL Union comes with an AppleScript file that can be used to export bookmarks from [Yojimbo](http://www.barebones.com/products/Yojimbo/) into an HTML file that is acceptable for import by URL Union. Simply open the AppleScript file using AppleScript Editor, and then click the Run button. Be patient when exporting from Yojimbo. It may take several minutes for the AppleScript to complete its execution. 

<h2 id="save">Saving a URL Collection</h2>

After selecting **Save** from the **File** menu with a new collection, or after selecting **Save As** from the **File** menu, you will be prompted to select a location at which to store your collection. 

The default file name will be urlunion.html, and it is recommended (although not required) that you use this default name. 

You may pick any folder you like in which to store your URL Union file, but it is recommended that you store your file in a new, empty folder especially created for this purpose. When you later use the Publish menu command, other files will be created for you, optionally in the same folder, and it will generally be cleaner if this folder is devoted to storage of URL Union files. 

If you are a subscriber to Mobile Me, then you may wish to store your URL Union files in a sub-folder within the Sites folder on your iDisk, since doing so will allow you to access your URLs from any Web browser with an Internet connection. 

<h2 id="openrecent">Opening Recent Files</h2>

On subsequent launches, URL Union will automatically open the last URL Union collection you used. You may also open a recent file by selecting **Open Recent** from the **File** menu. 

<h2 id="data">The Four Data Fields</h2>

For each URL, you may enter the following fields. 

* **Title** -- A brief title for the Web site or page referenced by the URL. 

* **URL** -- The URL itself.

* **Tags** -- Use a period to nest one tag within another. Use a comma to separate multiple tags. The "Favorites" tag identifies URLs you wish to appear in your favorites file (see the **Publish** section below). The "Startup" tag identifies URLs you wish to have launched in your favorite Web Browser whenever URL Union launches, so long as this is requested in your Preferences. 

* **Comments** -- Enter whatever you like here, or leave it blank. 

<h2 id="toolbar">The Tool Bar</h2>

A toolbar with multiple buttons appears at the top of the user interface. 

* **OK** -- Indicates that you have completed adding/editing the fields for the current URL. 
* **+** -- Clear the data fields and prepare to add a new URL to the collection. 
* **-** -- Delete the current URL.
* **&lt;&lt;** -- Display the first URL in the collection. 
* **&lt;** -- Display the next URL in the collection. 
* **>** -- Display the next URL in the collection. 
* **>>** -- Display the last URL in the collection. 
* **Launch** -- Launch the current URL in your Web browser. (This may also be accomplished by clicking the arrow that appears just to the left of the URL itself.)
* **Find** -- Looks for the text entered in the field just to the left of this button, and displays the first URL containing this text in any field, ignoring case. After finding the first occurrence, this button&#8217;s text changes to **Again**, to allow you to search again for the next URL containing the specified text. 

<h2 id="list">The List</h2>

On the first half of the main screen, you'll see two tabs. The first of these displays the **List**. This is just a simple list of all your URLs. The URL column appears first, and the list is sorted by domain name ("Google", etc.). You can rearrange/resize columns. You can't sort by other columns. Click on a row to select that URL for display on the other half of the main screen. 

<h2 id="tags">Tags</h2>

The second Tab on the first half of the main screen displays the **Tags**. This is an indented list of all your tags, with URLs appearing under as many tags as have been assigned to them, and with URLs with no tags displaying at the very top. Click to the left of a tag to expand it, showing URLs and/or sub-tags contained within it. 

Note that tags that were once used, but that used no more, will stick around until you close the URL Union file and re-open it. If you wish, you may accelerate this process by selecting **Reload** from the **File** menu. 

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
					<li><p><strong>index.html</strong> -- This file is an index file with links to the other files. You can browse this locally by selecting <strong>Browse local index&#8230;</strong> from the <strong>File</strong> menu. </p></li>
					<li><p><strong>favorites.html</strong> -- This file tries to arrange all of the URLs you have tagged as "Favorites" into a four-column format that will fit on a single page.</p></li>
					<li><p><strong>bookmark.html</strong> -- This file formats your URLs in the time-honored Netscape bookmarks format, suitable for import into almost any Web browser or URL manager.  </p></li>
					<li><p><strong>outline.html</strong> -- This is a dynamic html file that organizes your URLs within your tags, allowing you to reveal/disclose selected tags.</p></li>
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

### General Prefs ###

* **SplitPane: Horizontal Split?** --  Check the box to have the **List** and **Tags** appear on the left of the main screen, rather than the top.

* **Deletion: Confirm Deletes?** -- Check the box to have a confirmation dialog shown whenever you attempt to delete a URL. 

* **Software Updates: Check Automatically?** -- Check the box to have URL Union check for newer versions whenever it launches. 

* **Check Now** -- Click this button to check for a new version immediately. 

* **File Chooser** -- If running on a Mac, you may wish to select AWT rather than Swing, to make your Open and Save dialogs appear more Mac-like. 

* **Look and Feel** -- Select from one of the available options to change the overall look and feel of the application. 

* **Menu Location** -- If running on a Mac, you may wish to have the menus appear at the top of the screen, rather than at the top of the window. 

### Favorites Prefs ###

* **Open Startup Tags?** -- Indicate whether you want URLs tagged with "Startup" launched within your Web browser whenever URL Union starts.

* **Favorites Columns** -- Specify the number of columns you wish to appear on the Favorites page. 

* **Favorites Rows** -- Specify the maximum number of rows you wish to appear on the Favorites page. 
