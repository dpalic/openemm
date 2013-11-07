/***********************************************
 * IFrame SSI script II- � Dynamic Drive DHTML code library (http://www.dynamicdrive.com)
 * Visit DynamicDrive.com for hundreds of original DHTML scripts
 * This notice must stay intact for legal use
 ***********************************************/

var iframeid = "editorFrame";

//Should script hide iframe from browsers that don't support this script (non IE5+/NS6+ browsers. Recommended):
var iframehide = "no";

var getFFVersion = navigator.userAgent.substring(navigator.userAgent.indexOf("Firefox")).split("/")[1];
var FFextraHeight = parseFloat(getFFVersion) >= 0.1 ? 16 : 0; //extra height in px to add to iframe in FireFox 1.0+ browsers

function resizeCaller() {
    if(document.getElementById) {
        resizeIframe(iframeid);
    }
    //reveal iframe for lower end browsers? (see var above):
    if((document.all || document.getElementById) && iframehide == "no") {
        var tempobj = document.all ? document.all[iframeid] : document.getElementById(iframeid);
        tempobj.style.display = "block";
    }
}

function resizeIframe(frameid) {
    var currentfr = document.getElementById(frameid);
    if(currentfr && !window.opera) {
        currentfr.style.display = "block";
        if(currentfr.contentDocument && currentfr.contentDocument.body.offsetHeight) {//ns6 syntax
            currentfr.width = '100%';
            currentfr.height = currentfr.contentDocument.body.offsetHeight + FFextraHeight;
            currentfr.width = currentfr.contentDocument.body.scrollWidth;
        } else if(currentfr.Document && currentfr.Document.body.scrollHeight) { //ie5+ syntax
            currentfr.width = '100%';
            currentfr.height = currentfr.Document.body.scrollHeight + 50;
            currentfr.width = currentfr.Document.body.scrollWidth;
        }

        if(currentfr.addEventListener) {
            currentfr.addEventListener("load", readjustIframe, false);
        } else if(currentfr.attachEvent) {
            currentfr.detachEvent("onload", readjustIframe); // Bug fix line
            currentfr.attachEvent("onload", readjustIframe);
        }
    }
}

function editCM(cmId) {
    var editCmLink = document.getElementById("edit-CM-link");
    window.location = editCmLink.href + cmId;
}

function readjustIframe(loadevt) {
    resizeIframe(iframeid);
}

if(window.addEventListener) {
    window.addEventListener("load", resizeCaller, false);
} else if(window.attachEvent) {
    window.attachEvent("onload", resizeCaller);
} else {
    window.onload = resizeCaller;
}
