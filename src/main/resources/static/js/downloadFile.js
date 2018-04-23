function downloadFile(data, type, title) {
    if (window.navigator.msSaveOrOpenBlob) {
        downloadFileMSExplorer(data, type, title);
    } else {
        downloadFileChromeFirefox(data, type, title);
    }
}

function downloadFileChromeFirefox(data, type, title) {
    var link = document.createElement('a');
    document.body.appendChild(link);
    link.setAttribute("href", "data:" + type + "," + encodeURIComponent(data));
    link.setAttribute("download", title);
    link.setAttribute("class", "hidden");
    link.setAttribute("target", "_self");
    link.click();
}

function downloadFileMSExplorer(data, type, title) {
    var blob = new Blob([ data ], {type : type});
    window.navigator.msSaveOrOpenBlob(blob, title);
}