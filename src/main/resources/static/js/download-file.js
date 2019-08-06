function downloadFile(data, type, fileName) {
    if (window.navigator.msSaveOrOpenBlob) {
        downloadFileMSExplorer(data, type, fileName);
    } else {
        downloadFileChromeFirefox(data, type, fileName);
    }
}

function downloadFileChromeFirefox(data, type, fileName) {
    var link = document.createElement('a');
    document.body.appendChild(link);
    link.setAttribute("href", "data:" + type + "," + encodeURIComponent(data));
    link.setAttribute("download", fileName);
    link.setAttribute("class", "hidden");
    link.setAttribute("target", "_self");
    link.click();
}

function downloadFileMSExplorer(data, type, fileName) {
    var blob = new Blob([data], { type: type });
    window.navigator.msSaveOrOpenBlob(blob, fileName);
}