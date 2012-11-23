// TODO: Tip of the hat to http://developers.technorati.com/wiki/XOXO

// TODO: Document, document, document

// TODO: Fix any memory leaks.  There're bound to be LOTS.

// TODO: Utils in its own JS file?
// TODO: Revise to use Prototype or MochiKit when available?
// TODO: Make outliner transformations reversible
// TODO: XmlHTTPRequest outline saving
// TODO: Keyboard repeat?
// TODO: Any premature optimizations come to mind?

// TODO: Stop trying to detect expansion state from className tests -- use handle.collapsed.

if (!window.Node) {
   var Node = {
      ELEMENT_NODE : 1,
      ATTRIBUTE_NODE : 2,
      TEXT_NODE : 3,
      CDATA_SECTION_NODE : 4,
      ENTITY_REFERENCE_NODE : 5,
      ENTITY_NODE : 6,
      PROCESSING_INSTRUCTIONS_NODE : 7,
      COMMENT_NODE : 8,
      DOCUMENT_NODE : 9,
      DOCUMENT_TYPE_NODE : 10,
      DOCUMENT_FRAGMENT_NODE : 11,
      NOTATION_NODE : 12
   }
}

var Keys = {
    BS:      8,
    TAB:     9,
    RETURN:  13,
    ESC:     27,
    UP:      38,
    LEFT:    37,
    RIGHT:   39,
    DOWN:    40
};

var Utils = {

    id: function(id) {
        return document.getElementById(id);
    },

	addLoadEvent : function(func){
		var oldonload = window.onload;
		
		if (typeof window.onload != 'function') {
			window.onload = func;
		} else {
			window.onload = function() {
				oldonload();
				func();
			}
		}
	},

    // http://www.quirksmode.org/js/findpos.html
    findPosX: function(obj) {
        var curleft = 0;
        if (obj.offsetParent) {
            while (obj.offsetParent) {
                curleft += obj.offsetLeft
                obj = obj.offsetParent;
            }
        }
        else if (obj.x)
            curleft += obj.x;
        return curleft;
    },

    // http://www.quirksmode.org/js/findpos.html
    findPosY: function(obj) {
        var curtop = 0;
        if (obj.offsetParent) {
            while (obj.offsetParent) {
                curtop += obj.offsetTop
                obj = obj.offsetParent;
            }
        }
        else if (obj.y)
            curtop += obj.y;
        return curtop;
    },

    // http://www.quirksmode.org/js/events_order.html
    captureEvent: function(e) {
        if (!e) e = window.event;
        e.cancelBubble = true;
        if (e.stopPropagation) e.stopPropagation();
        return e;
    },
    
    format: function(tmpl, tmpl_map) {
        var parts = tmpl.split(/(\0.*?\f)/);
        var i, p, m, out="";
        for (i=0; i<parts.length; i++) {
            p = parts[i];
            m = p.match(/^\0(.*?)\f$/);
            out += (!m) ? p : tmpl_map[m[1]];
        }
        return out;
    },
    
    trim: function(sInString) {
        if (!sInString) return '';
        sInString = sInString.replace( /^\s+/g, "" );// strip leading
        return sInString.replace( /\s+$/g, "" );// strip trailing
    },

    childrenByTagName: function(ele, tn) {
        var rv = [];
        var cn = ele.childNodes;
        tn = tn.toLowerCase();
        for (var i=0; i<cn.length; i++)
            if (cn[i].tagName && cn[i].tagName.toLowerCase() == tn)
                rv[rv.length] = cn[i];
        return rv;
    },

    parentNodeByTagName: function(ele, tn, top) {
        var curr = ele;
        tn = tn.toLowerCase();
        while ( (curr = curr.parentNode) && (curr != top) ) 
            if (curr.tagName && curr.tagName.toLowerCase() == tn)
                return curr;
        return null;
    },
    
    firstChildByTagName: function(ele, tn) {
        var curr = ele.firstChild;
        tn = tn.toLowerCase();
        while (curr) {
            if (curr.tagName && curr.tagName.toLowerCase() == tn)
                return curr;
            curr = curr.nextSibling;
        }
    },
    
    lastChildByTagName: function(ele, tn) {
        var curr = ele.lastChild;
        tn = tn.toLowerCase();
        while (curr) {
            if (curr.tagName && curr.tagName.toLowerCase() == tn)
                return curr;
            curr = curr.previousSibling;
        }
    },
    
    nextSiblingByTagName: function(ele, tn) {
        var curr = ele;
        tn = tn.toLowerCase();
        while (curr = curr.nextSibling)
            if (curr.tagName && curr.tagName.toLowerCase() == tn)
                return curr;
        return null;
    },

    previousSiblingByTagName: function(ele, tn) {
        var curr = ele;
        tn = tn.toLowerCase();
        while (curr = curr.previousSibling)
            if (curr.tagName && curr.tagName.toLowerCase() == tn)
                return curr;
        return null;
    }

}

function Outliner(id) {
    var outliner = this;

    // TODO: Replace this with Prototype stuff.
    this.options = {
        collapsed:     false,
        root_css:      'outliner'
    }
    var source = arguments[1] || {};
    for (property in source)
        this.options[property] = source[property];
    
    var root        = Utils.id(id);
    root.className += " "+this.options.root_css;
    root.onclick = function(e) {
        var e = Utils.captureEvent(e);
        outliner.editor.cancel();
    };
    this.root = root;
    
    var items = root.getElementsByTagName("li");
    for (var i=0; i<items.length; i++)
        items[i].outline_handle = new OutlineHandle(this, items[i]);

    this.editor = new OutlineEditor(this);
}

Outliner.prototype = {
    
    getChildListForItem: function(ele) {
        return Utils.firstChildByTagName(ele, "ul") || 
               Utils.firstChildByTagName(ele, "ol");
    },
    
    toggleItem: function(ele) {
        if (/itemleaf/.test(ele.className)) return;

        var child = this.getChildListForItem(ele);
        if (/itemcollapsed/.test(child.className)) 
            this.expandItem(ele);
        else
            this.collapseItem(ele);
    },

    collapseItem: function(ele) {
        if (/itemleaf/.test(ele.className)) return;

        var child = this.getChildListForItem(ele);
        var handle = ele.outline_handle;
        
        handle.collapseHandle();
        child.className  = "itemcollapsed";
    },

    expandItem: function(ele) {
        if (/itemleaf/.test(ele.className)) return;
        
        var child = this.getChildListForItem(ele);
        var handle = ele.outline_handle;

        handle.expandHandle();
        child.className  = "itemexpanded";
    },

    collapseChildren: function(ele) {
        if (/itemleaf/.test(ele.className)) return;
        this.collapseItem(ele);
        
        var child = this.getChildListForItem(ele);
        if (!child) return;
        
        var childItems = Utils.childrenByTagName(child, "li");
        for (var i=0; i<childItems.length; i++)
            if (/parent/.test(childItems[i].className))
                this.collapseChildren(childItems[i]);
    },

    expandChildren: function(ele) {
        if (/itemleaf/.test(ele.className)) return;
        this.expandItem(ele);
        
        var child = this.getChildListForItem(ele);
        if (!child) return;
        
        var childItems = Utils.childrenByTagName(child, "li");
        for (var i=0; i<childItems.length; i++)
            if (/parent/.test(childItems[i].className))
                this.expandChildren(childItems[i]);
    },

    findNextItem: function(ele) {
        // First, look for first item of an expanded child list.
        var sl = this.getChildListForItem(ele);
        if (sl && !/collapsed/.test(sl.className)) {
            var li = Utils.firstChildByTagName(sl, "li");
            if (li) return li;
        }
        
        // Next, look for the next sibling list item.
        var after = Utils.nextSiblingByTagName(ele, "li");
        if (after) return after;
        
        // Finally, pop up to through parents, looking for next list item.
        else {
            for (var curr=ele.parentNode; curr && curr!=this.root; curr=curr.parentNode) {
                if (curr.tagName.toLowerCase() == "li") {
                    var next = Utils.nextSiblingByTagName(curr, "li");
                    if (next) return next;
                }
            }
        }
        
        return null;
    },

    findPrevItem: function(ele) {
        // Look for prev sibling
        var prev = Utils.previousSiblingByTagName(ele, "li");
        
        // If no previous sibling, pop up to parent
        if (!prev) return Utils.parentNodeByTagName(ele, "li", this.root);

        // Dig down through any expanded children of prev node, to last leaf node.
        else {
            while(true) {
                var cl = this.getChildListForItem(prev);
                if (!cl || /collapsed/.test(cl.className)) return prev;
                prev = Utils.lastChildByTagName(cl, "li");
            }
        }
    }
    
};

function OutlineHandle(outliner, item) {
    var handle      = this;
    var handle_node = this.createNode();
    
    this.outliner    = outliner;
    this.item        = item;
    this.handle_node = handle_node;
    // TODO: Fix list-dictated expansion state
    this.collapsed   = (item.getAttribute("compact")) || 
                       (outliner.options.collapsed==true);
    this.is_parent   = null;

    this.updateParentage();
    
    item.insertBefore(handle_node, item.firstChild);

    // WIre up the editor click event for this item.
    item.onclick = function(e) {
        var e = Utils.captureEvent(e);
        outliner.editor.edit(this);
    };
}
OutlineHandle.prototype = {
    
    createNode: function() {
        var handle = document.createElement("span");
        handle.appendChild(document.createTextNode("\u00a0\u00a0"));
        return handle;
    },

    collapseHandle: function() {
        this.collapsed = true;
        this.handle_node.className = "handlecollapsed";
    },

    expandHandle: function() {
        this.collapsed = false;
        this.handle_node.className = "handleexpanded";
    },

    updateParentage: function() {
        var cl = this.outliner.getChildListForItem(this.item);
        if (cl) {
            if (Utils.firstChildByTagName(cl, "li")) {
                if (this.is_parent == true) return;
                return this.becomeParent();
            } else {
                // HACK: Remove the empty child list.  Should be done here?
                this.item.removeChild(cl);
            }
        }
        if (this.is_parent == false) return;
        return this.becomeLeaf();
    },

    becomeParent: function() {
        if (this.is_parent == true) return;

        var outliner    = this.outliner;
        var item        = this.item;
        var handle_node = this.handle_node;
        var child_list  = outliner.getChildListForItem(this.item);
        var collapsed   = this.collapsed;
        
        this.is_parent  = true;
        this.child_list = child_list;
        
        item.className        = "itemparent";
        child_list.className  = (collapsed) ?  
            "itemcollapsed" : "itemexpanded";
        handle_node.className = (collapsed) ?  
            "handlecollapsed" : "handleexpanded";

        var toggle_func = function(e) {
            var e = Utils.captureEvent(e);
            outliner.toggleItem(item);
            outliner.editor.cancel();
        };
        handle_node.onclick    = toggle_func;
        handle_node.ondblclick = toggle_func;
    },
    
    becomeLeaf: function() {
        if (this.is_parent == false) return;
        
        var outliner    = this.outliner;
        var item        = this.item;
        var handle_node = this.handle_node;

        this.is_parent        = false;
        this.child_list       = null;
        handle_node.className = "handleleaf";
        item.className        = "itemleaf";

        var handle_edit = function (e) {
            var e = Utils.captureEvent(e);
            outliner.editor.edit(item);
        };
        handle_node.onclick    = handle_edit;
        handle_node.ondblclick = handle_edit;
    }

};

function OutlineEditor(outliner) {
    var editor = this;

    var fld = document.createElement("input");
    fld.setAttribute("type",  "text");
    fld.className = "itemeditor";
    fld.style.display = 'none';
    outliner.root.insertBefore(fld, outliner.root.firstChild);
    
    fld.onclick   = function(e) 
        { var e = Utils.captureEvent(e); };
    fld.onkeydown = function(e) 
        { return editor.keydown(this, e); }
    
    this.outliner     = outliner;
    this.fld          = fld;
    
    window.setTimeout(function() { editor.reset(); }, 20);
}

OutlineEditor.prototype = {
    
    reset: function() {
        this.item_node    = null;
        this.content_node = null;
        this.is_new       = false;
        this.fld.style.display = 'none';
    },
    
    cancel: function() {
        var fld  = this.fld;
        var curr = this.item_node;
        
        fld.style.display = 'none';
        
        // If cancelling an edit on a new item, remove it.
        if (this.is_new) curr.parentNode.removeChild(curr);
        
        this.reset();
    },
    
    edit: function(ele, is_new) {
        this.commit();
        if (ele == null) return;

        var handle_node = ele.outline_handle.handle_node;
        var fld    = this.fld;

        // HACK: Need to twiddle with the editor positioning by browser.
        // Who knows how well this works with varied font sizes?
        var bOffsetX,bOffsetY;
        if (browser == "Safari") {
            // Safari's placement of the editor is wack.
            bOffsetX = 5; bOffsetY = 4;
        } else if (browser == "Internet Explorer") {
            // Internet Explorer is wack in its own way.
            bOffsetX = 0; bOffsetY = -3;
        } else {
            // Assuming Firefox-ish semi-wack for everyone else.
            bOffsetX = 0; bOffsetY = -1;
        };
        
        // Place & size the editor with appropriate wackiness.
        fld.style.position = "absolute";
        fld.style.left = 
            bOffsetX + Utils.findPosX(ele) + handle_node.offsetWidth + "px";
        fld.style.top = 
            bOffsetY + Utils.findPosY(ele) + "px";
        fld.style.width = 
            ele.offsetWidth + "px";

        var content_node = node_after(handle_node);
        switch (content_node.nodeType) {
            
            case Node.ELEMENT_NODE:
                fld.value = data_of(content_node.firstChild);
                break;
                
            case Node.TEXT_NODE:
                fld.value = Utils.trim(content_node.nodeValue);
                break;
                
            default:
                break;
        }
        
        this.item_node    = ele;
        this.content_node = content_node;
        this.is_new       = is_new;
        
        fld.style.display = "block";
        
        // HACK: Tweak to prevent page jump with focus.
        window.setTimeout(function() { fld.focus() }, 20);
    },
    
    commit: function() {
        var fld            = this.fld;
        var curr           = this.item_node;
        var content_node   = this.content_node;
        
        fld.style.display = 'none';
        
        if (curr) {
            var val = fld.value;
            if (val != "") {
                switch (content_node.nodeType) {
                    case Node.ELEMENT_NODE:
                        content_node.firstChild.nodeValue = fld.value;
                        break;
                        
                    case Node.TEXT_NODE:
                        content_node.nodeValue = fld.value;
                        break;
                        
                    default:
                        break;
                }
                // Does this need to happen, really?
                //curr.outline_handle.updateParentage();
            } else {
                var parent = curr.parentNode;
                parent.removeChild(curr);
                parent.outline_handle.updateParentage();
            }
        }

        this.reset();
    },

    keydown: function(ele, e) {
        if (!e) e = window.event;
        
        // e.cancelBubble = true;
        // if (e.stopPropagation) e.stopPropagation();

        var fld      = this.fld;
        var curr     = this.item_node;
        var content  = this.content_node;
        var outliner = this.outliner;
        
        var keycode = e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;

        switch(keycode) {
        
            case Keys.ESC:
                // ESC: Cancel editing, abandon changes.
                this.cancel();
                break;

            case Keys.BS:
                if (e.shiftKey) {
                    // SHIFT-BACKSPACE: Delete current item, navigate to prev.
                    var prev   = outliner.findPrevItem(curr);
                    var parent = curr.parentNode;
                    this.cancel();
                    parent.removeChild(curr);
                    if (prev) this.edit(prev);
                    return false;
                }
                break;
                
            case Keys.RETURN:
                if (e.shiftKey) {
                    // SHIFT-RETURN: Create a new blank outline subitem and edit.
                    if (this.is_new) return false;
                    this.commit();
                    
                    var new_parent = document.createElement("ul");
                    curr.appendChild(new_parent);
                    
                    var new_item = document.createElement("li");
                    new_item.appendChild(document.createTextNode("\u00a0"));
                    new_parent.appendChild(new_item);
                    new_item.outline_handle = new OutlineHandle(outliner, new_item);
                    
                    this.edit(new_item, true);

                    return false;
                }
                else if (e.ctrlKey) { 
                    alert("CTRL!"); 
                } 
                else {
                    this.commit();

                    // Create a new blank outline item and edit.
                    var parent   = curr.parentNode;
                    var new_item = document.createElement("li");
                    new_item.appendChild(document.createTextNode("\u00a0"));
                    parent.insertBefore(new_item, curr.nextSibling);
                    new_item.outline_handle = new OutlineHandle(outliner, new_item);
                    this.edit(new_item, true);

                    return false;
                } 
                break;
            
            case Keys.TAB:
                if (e.shiftKey) {
                    // SHIFT-TAB: Shift current item to become parent sibling.
                    var parent    = curr.parentNode;
                    if (parent == outliner.root) return false;

                    var parent_li = parent.parentNode;
                    var parent_ol = parent_li.parentNode;

                    this.cancel();
                    parent.removeChild(curr);
                    parent_ol.insertBefore(curr, parent_li.nextSibling);
                    parent_li.outline_handle.updateParentage();
                    this.edit(curr);
                } else {
                    // TAB: Shift current item to child of prev sibling.
                    var prev   = Utils.previousSiblingByTagName(curr, "li");
                    if (!prev) return false;
                    outliner.expandItem(prev);
                    
                    var parent = curr.parentNode;
                    var cl     = outliner.getChildListForItem(prev);
                    if (!cl) {
                        // Create a new blank outline subitem and edit.
                        cl = document.createElement("ul");
                        prev.appendChild(cl);
                    }
                    
                    this.cancel();
                    parent.removeChild(curr);
                    cl.appendChild(curr);
                    prev.outline_handle.updateParentage();
                    this.edit(curr);
                }
                break;
                
            case Keys.UP:
                if (e.shiftKey) {
                    // SHIFT-UP: Shift current node up amongst siblings.
                    var prev   = Utils.previousSiblingByTagName(curr, "li");
                    if (prev) {
                        var parent = curr.parentNode;
                        this.cancel();
                        parent.removeChild(curr);
                        parent.insertBefore(curr, prev);
                        this.edit(curr);
                    }
                    return false;
                }
                else if (e.ctrlKey) { 
                    // CTRL-UP: Collapse item and all children.
                    if (/parent/.test(curr.className)) {
                        outliner.collapseChildren(curr);
                        return false;
                    }
                } 
                else {
                    // UP: Navigate editor up through outline.
                    var prev = outliner.findPrevItem(curr);
                    if (prev) this.edit(prev);
                    return false;
                } 
                break;
                
            case Keys.DOWN:
                if (e.shiftKey) {
                    // SHIFT-DOWN: Shift current node down amongst siblings.
                    var next = Utils.nextSiblingByTagName(curr, "li");
                    if (next) {
                        var parent = curr.parentNode;
                        this.cancel();
                        parent.removeChild(next);
                        parent.insertBefore(next, curr);
                        this.edit(curr);
                    }
                    return false;
                }
                else if (e.ctrlKey) { 
                    // CTRL-DOWN: Expand item and all children.
                    if (/parent/.test(curr.className)) {
                        outliner.expandChildren(curr);
                        return false;
                    }
                } 
                else {
                    // DOWN: Navigate editor down through outline.
                    var next = outliner.findNextItem(curr);
                    if (next) this.edit(next);
                    return false;
                } 
                break;
                
            case Keys.LEFT:
                if (e.shiftKey) {
                    // SHIFT-LEFT: Shift current item to become parent sibling.
                    var parent    = curr.parentNode;
                    if (parent == outliner.root) return false;
                    
                    var parent_li = parent.parentNode;
                    var parent_ol = parent_li.parentNode;

                    this.cancel();
                    parent.removeChild(curr);
                    parent_ol.insertBefore(curr, parent_li.nextSibling);
                    parent_li.outline_handle.updateParentage();
                    this.edit(curr);
                }
                else if (e.ctrlKey) { 
                    // CTRL-LEFT: Collapse the current parent item
                    if (/parent/.test(curr.className)) {
                        outliner.collapseItem(curr);
                        return false;
                    }
                } 
                else {
                } 
                break;
                
            case Keys.RIGHT:
                if (e.shiftKey) {
                    // SHIFT-RIGHT: Shift current item to child of prev sibling.
                    var prev   = Utils.previousSiblingByTagName(curr, "li");
                    if (!prev) return false;
                    outliner.expandItem(prev);
                    
                    var parent = curr.parentNode;
                    var cl     = outliner.getChildListForItem(prev);
                    if (!cl) {
                        // Create a new blank outline subitem and edit.
                        cl = document.createElement("ul");
                        prev.appendChild(cl);
                    }
                    
                    this.cancel();
                    parent.removeChild(curr);
                    cl.appendChild(curr);
                    prev.outline_handle.updateParentage();
                    this.edit(curr);
                }
                else if (e.ctrlKey) { 
                    // CTRL-RIGHT: Expand the current parent item
                    if (/parent/.test(curr.className)) {
                        outliner.expandItem(curr);
                        return false;
                    }
                } 
                else {
                } 
                break;

            default:
                return true;
            
        }

        return true;
                
    }

};


