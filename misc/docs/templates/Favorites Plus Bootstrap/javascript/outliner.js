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
    };
    this.root = root;
    
    var items = root.getElementsByTagName("li");
    for (var i=0; i<items.length; i++)
        items[i].outline_handle = new OutlineHandle(this, items[i]);
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

    item.onclick = function(e) {
        var e = Utils.captureEvent(e);
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
        };
        handle_node.onclick    = handle_edit;
        handle_node.ondblclick = handle_edit;
    }

};



