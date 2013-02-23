/*
 * Copyright 1999 - 2013 Herb Bowie
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
  import java.io.*;
  import java.util.*;
  import javax.swing.table.*;

/**
 The storage repository for a URL collection.
 */
public class URLCollection
    extends AbstractTableModel
    implements TaggableList {

  private String          title = "URL Union Collection";
  private List            urls = new ArrayList();

  private int             findIndex = -1;
  private boolean         findMatch = false;

  private TagsList        tagsList = new TagsList();
  private TagsModel       tagsModel = new TagsModel();

  public URLCollection () {
    tagsList.registerValue("");
  }

  public TagsList getTagsList () {
    return tagsList;
  }

  public TagsModel getTagsModel () {
    return tagsModel;
  }

  public File getSource () {
    return tagsModel.getSource();
  }

  public void setSource (File source) {
    tagsModel.setSource(source);
  }

  /**
   Add a new url to the list, or merge it with an existing one if another
   with the same url already exists. Maintain the list in ascending sequence
   by the url key.
   
   @param newURL
   @return A positioned url composed of the resulting url and an index
           pointing to its resulting position in the list.
   */
  public URLPositioned add (URLPlus newURL) {
    // System.out.println ("adding: " + newURL.toString());
    // System.out.println ("URLCollection add " + newURL.toString());
    // newURL.getTags().displayTags();
    URLPlus resultingURL = newURL;
    boolean merged = false;
    if (urls.isEmpty()) {
      // If this is the first url being added to the collection, simply add it
      urls.add (newURL);
      findIndex = 0;
    }
    else
    if (get(urls.size() - 1).compareToUsingUniqueKey(newURL) < 0) {
      // If the new URL has a key higher than the highest item in the
      // collection, simply add the new URL to the end
      // (more efficient if an input file happens to be pre-sorted).
      findIndex = urls.size();
      urls.add (newURL);
    } else {
      findInternal (newURL);
      if (findMatch) {
        get(findIndex).merge(newURL);
        resultingURL = get(findIndex);
        merged = true;
      } else {
        urls.add (findIndex, newURL);
      }
    }
    
    if (merged) {
      tagsList.modify  (resultingURL);
      tagsModel.modify (resultingURL);
    } else {
      tagsList.add  (resultingURL);
      tagsModel.add (resultingURL);
    }

    return new URLPositioned (resultingURL, findIndex);
  } // end add method

  public URLPositioned modify (URLPositioned modURL) {
    tagsList.modify(modURL.getURLPlus());
    tagsModel.modify(modURL.getURLPlus());
    return modURL;
  }

  /**
   Removes the passed url, if it exists in the collection.

   @param position A position containing the url to be removed.
   
   @return A position for the next URL following the one just removed.
   */
  public URLPositioned remove (URLPositioned position) {
    int oldIndex = find (position.getURLPlus());
    URLPositioned newPosition = position;
    if (findMatch) {
      newPosition = next (position);
      tagsModel.remove (position.getURLPlus());
      tagsList.remove (position.getURLPlus());
      urls.remove(oldIndex);
    }
    return newPosition;
  }

  public int find (URLPlus findURL) {
    findInternal (findURL);
    if (findMatch) {
      return findIndex;
    } else {
      return -1;
    }
  }

  /**
   Find the appropriate insertion point or match point in the url list,
   and use findIndex and findMatch to return the results.

   @param findURL URL we are looking for.
   */
  private void findInternal (URLPlus findURL) {
    int low = 0;
    int high = urls.size() - 1;
    findIndex = 0;
    findMatch = false;
    while (high >= low
        && findMatch == false
        && findIndex < urls.size()) {
      int diff = high - low;
      int split = diff / 2;
      findIndex = low + split;
      int compare = get(findIndex).compareToUsingUniqueKey(findURL);
      if (compare == 0) {
        // found an exact match
        findMatch = true;
      }
      else
      if (compare < 0) {
        // url from list is less than the one we're looking for
        findIndex++;
        low = findIndex;
      } else {
        // url from list is greater than the one we're looking for
        if (high > findIndex) {
          high = findIndex;
        } else {
          high = findIndex - 1;
        }
      }
    } // end while looking for right position
  } // end find method
  
  public URLPositioned first (URLPositioned position) {
    if (position.navigateUsingList()) {
      return firstUsingList ();
    } else {
      return firstUsingTree ();
    }
  }
  
  public URLPositioned last (URLPositioned position) {
    if (position.navigateUsingList()) {
      return lastUsingList ();
    } else {
      return lastUsingTree ();
    }
  }

  public URLPositioned next (URLPositioned position) {
    URLPositioned nextPosition = null;
    if (position.navigateUsingList()) {
      nextPosition = nextUsingList (position);
    } else {
      nextPosition = nextUsingTree (position);
    }
    if (nextPosition == null) {
      return first(position);
    } else {
      return nextPosition;
    }
  }

  public URLPositioned prior (URLPositioned position) {
    if (position.navigateUsingList()) {
      return priorUsingList (position);
    } else {
      return priorUsingTree (position);
    }
  }

  public URLPositioned firstUsingList () {
    return positionUsingListIndex (0);
  }

  public URLPositioned lastUsingList () {
    return positionUsingListIndex (size() - 1);
  }

  public URLPositioned nextUsingList (URLPositioned position) {
    return (positionUsingListIndex (position.getIndex() + 1));
  }

  public URLPositioned priorUsingList (URLPositioned position) {
    return (positionUsingListIndex (position.getIndex() - 1));
  }

  public URLPositioned positionUsingListIndex (int index) {
    if (index < 0) {
      index = 0;
    }
    if (index >= size()) {
      index = size() - 1;
    }
    URLPositioned position = new URLPositioned();
    position.setIndex (index);
    position.setNavigator (URLPositioned.NAVIGATE_USING_LIST);
    if (index >= 0) {
      position.setURLPlus (get (index));
      position.setTagsNode (position.getURLPlus().getTagsNode());
    }
    return position;
  }

  public URLPositioned firstUsingTree () {
    return positionUsingNode (tagsModel.firstItemNode());
  }

  public URLPositioned lastUsingTree () {
    return positionUsingNode (tagsModel.lastItemNode());
  }

  public URLPositioned nextUsingTree (URLPositioned position) {
    if (position.getTagsNode() == null) {
      return null;
    } else {
      return positionUsingNode
          (tagsModel.nextItemNode(position.getTagsNode()));
    }
  }

  public URLPositioned priorUsingTree (URLPositioned position) {
    if (position.getTagsNode() == null) {
      return null;
    } else {
      return positionUsingNode
          (tagsModel.priorItemNode(position.getTagsNode()));
    }
  }

  public URLPositioned positionUsingNode (TagsNode node) {
    if (node == null) {
      return null;
    } else {
      URLPositioned position = new URLPositioned();
      position.setURLPlus ((URLPlus)node.getTaggable());
      position.setTagsNode (node);
      findInternal (position.getURLPlus());
      position.setIndex (findIndex);
      position.setNavigator (URLPositioned.NAVIGATE_USING_TREE);
      return position;
    }
  }

  public int getColumnCount () {
    return 3;
  }

  public String getColumnName (int columnIndex) {
    switch (columnIndex) {
      case 0: return "URL";
      case 1: return "Title";
      case 2: return "Tags";
      default: return "?";
    }
  }

  public Class getColumnClass (int columnIndex) {
    return String.class;
  }

  public String getValueAt (int rowIndex, int columnIndex) {
    URLPlus row = get(rowIndex);
    if (row == null) {
      return "";
    } else {
      switch (columnIndex) {
        case 0:
          return row.getURL();
        case 1:
          return row.getTitle();
        case 2:
          return row.getTagsAsString();
        default:
          return "Column " + String.valueOf(columnIndex);
      } // end switch
    } // end if good row
  } // end method getValueAt

  public URLPlus get (int index) {
    if (index >= 0 && index < urls.size()) {
      return (URLPlus)urls.get(index);
    } else {
      return null;
    }
  } // end method get (int)
  
  public void setTitle (String title) {
    this.title = title;
  }

  public String getTitle () {
    return title;
  }

  public int size() {
    return urls.size();
  }

  public int getRowCount() {
    return urls.size();
  }

} // end URLCollection class
