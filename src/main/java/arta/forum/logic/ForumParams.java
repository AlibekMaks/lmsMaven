package arta.forum.logic;

import arta.common.logic.util.DataExtractor;

import javax.servlet.http.HttpServletRequest;

public class ForumParams {

    String title;
    String body;

    public void saveMsgParams(HttpServletRequest request, DataExtractor extractor) {

        title = extractor.getRequestString(request.getParameter("theme"));
        body = extractor.getRequestString(request.getParameter("body"));
    }


    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
