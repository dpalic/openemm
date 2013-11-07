function showPopups() {
    // get ecs-frame document
    var frameDocument = document;

    var nullColorElement = frameDocument.getElementById('info-null-color');
    if (nullColorElement == null) {
        return;
    }
    var nullColor = nullColorElement.value;
    var initialWidth = document.body.scrollWidth;

    // iterate through all links of document
    var links = frameDocument.getElementsByTagName('a');
    if (links != null && links.length > 0) {
        for (var i = 0; i < links.length; i++) {
            var linkUrl = links[i].getAttribute('href');
            if (!linkUrl)
                continue;

            if (linkUrl.lastIndexOf("http") == -1)
                continue;

            var lastPointIndex = linkUrl.lastIndexOf(".");
            if (lastPointIndex != -1) {
                var urlWithoutSignature = linkUrl.substr(0, lastPointIndex);
                var preLastPointIndex = urlWithoutSignature.lastIndexOf(".");
                if (preLastPointIndex != -1) {
                    var codedUrlId = urlWithoutSignature.substring(preLastPointIndex + 1);
                    // get stats info for the URL from hidden field
                    var infoElId = "info-" + codedUrlId;
                    var linkInfo = frameDocument.getElementById(infoElId);
                }
            }
            else {
                continue;
            }
            // if there is stats for the URL - create stats-label and put it near link
            // in other case create default stat-label with zero-value
            var clickValue = "0 (0%)";
            var bgColor = nullColor;
            if (linkInfo != null) {
                clickValue = linkInfo.value;
                bgColor = linkInfo.name;
            }
            var statLabel = createStatLabel(clickValue, bgColor, frameDocument);
            adjusPosition(links[i], statLabel, frameDocument, initialWidth);
        }
    }
}

function createStatLabel(labelValue, bgColor, frameDocument) {
    var myDiv = frameDocument.createElement('div');
    myDiv.innerHTML = labelValue;
    myDiv.style.backgroundColor = bgColor;
    myDiv.style.padding = '1px';
    myDiv.style.border = '1px solid #777777';
    myDiv.style.fontFamily = 'Tahoma, Arial, Helvetica, sans-serif';
    myDiv.style.fontSize = '11px';
    frameDocument.body.appendChild(myDiv);
    return myDiv;
}

function adjusPosition(linkElement, popup, frameDocument, documentWidth) {
    popup.style.position = 'absolute';
    popup.style.left = 10;
    popup.style.top = 10;
    var popupWidth = popup.offsetWidth + 4;

    // get absolute position of link
    var posX = linkElement.offsetLeft;
    var posY = linkElement.offsetTop;
    var tmp = linkElement;

    while (tmp.offsetParent) {
        posX = posX + tmp.offsetParent.offsetLeft;
        posY = posY + tmp.offsetParent.offsetTop;
        if (tmp == frameDocument.getElementsByTagName('body')[0]) {
            break;
        }
        else {
            tmp = tmp.offsetParent;
        }
    }

    // set popup position
    if (linkElement.offsetWidth < popupWidth) {
        popup.style.left = posX;
    }
    else {
        popup.style.left = posX + linkElement.offsetWidth - popupWidth;
    }
    popup.style.width = popupWidth - 4; // 4 = border + margin
    popup.style.textAlign = "center";
    popup.style.top = posY + linkElement.offsetHeight - popup.offsetHeight;
    var frameGap = 20;
    if (parseInt(popup.style.left) + popupWidth > documentWidth - frameGap) {
        popup.style.left = posX;
        popup.style.top = posY;
        if (parseInt(popup.style.left) + popupWidth > documentWidth - frameGap) {
            popup.style.left = documentWidth - frameGap - popupWidth;
        }
    }
    if (popup.offsetWidth < popupWidth) {
        popup.style.width = popupWidth;
    }

}

if (window.addEventListener) {
    window.addEventListener('load', showPopups, false);
} else if (window.attachEvent) {
    window.attachEvent('onload', showPopups);
} else {
    window.onload = showPopups;
}