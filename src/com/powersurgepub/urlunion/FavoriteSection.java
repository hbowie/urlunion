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

  import java.util.*;

/**
 One section of the Favorites. 
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
