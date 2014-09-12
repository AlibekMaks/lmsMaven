function showFileUploadWindow(width, height){
        tmpLeft = (window.screen.availWidth-width)/2;
        tmpTop = (window.screen.availHeight-height)/2;   
        window.open("pasteObj", "", "height="+height+", width="+width+", left="+tmpLeft+", top="+tmpTop+", resizable=1, scrollbars=1");
}