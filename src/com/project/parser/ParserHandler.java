package com.project.parser;

public interface ParserHandler {
    void elementFound(Tag tag) throws Exception;
    int getAmountNoCloseTag();
    void listFind(Tag tag) throws Exception;
    void startElement(Tag tag) throws Exception;
    void emoticonFind(String emoticon) throws Exception;
    void endElement(Tag tag) throws Exception;
    void hyperlinkFind(String hyperlink) throws Exception;
    void imageFind(String image) throws Exception;
    void emailFind(String email) throws Exception;
    void text(String str) throws Exception;
    void tableHeadlineFound(String str) throws Exception;
    void tableColumnFound(String str) throws Exception;
    void startEndTable(boolean isTable) throws Exception;
    void startEndRow(boolean isTableRow) throws Exception;
    public void saveToHTML();
}
