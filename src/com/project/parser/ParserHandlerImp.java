package com.project.parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class ParserHandlerImp implements ParserHandler {
    private List<Tag> tagsInScope;
    private List<Tag> listInScope;
    String content = "";


    public void saveToHTML(){
        String allContentHTML = "<html>\n<head>\n<title>PROJEKT</title>\n</head>\n<body>\n" + content + "\n</body>\n</html>";

        try {
            PrintWriter zapis = new PrintWriter("index.html");
            zapis.println(allContentHTML);
            zapis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public ParserHandlerImp(){
        tagsInScope = new LinkedList<>();
        listInScope = new LinkedList<>();

    }

    private String checkClosingTag(Tag tag){
        if(tagsInScope.remove(tag))
            return CheckTags.endTag.get(tag);
        else {
            tagsInScope.add(tag);
            return CheckTags.startTag.get(tag);
        }
    }

    public boolean isTheSameLevelList(Tag tag){
        if(listInScope.size() > 0 && listInScope.get(listInScope.size() - 1) == tag){
            return true;
        }else{
            int last = -1;

            for(int i = listInScope.size() - 1; i >= 0; i--){
                if(listInScope.get(i) == tag){
                    last = i;
                    break;
                }
            }

            if(last != -1){
//                    System.out.println("To close:");
                for(int i = listInScope.size() - 1; i > last; i--) {
                    listInScope.remove(i);
                    content += "</ul>\n";
                    System.out.println(ANSI_GREEN + "</ul>" + ANSI_RESET);
                }
                return true;
            }else{
                listInScope.add(tag);
                return false;
            }
        }
    }

    public int getAmountNoCloseTag(){
        return tagsInScope.size();
    }


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    @Override
    public void elementFound(Tag tag) throws Exception {
        String element = checkClosingTag(tag);
        content += element;
        System.out.print(ANSI_GREEN  + element+ ANSI_RESET);

    }

    @Override
    public void listFind(Tag tag) throws Exception {

        if(tag == Tag.END_LIST) {
            System.out.print(ANSI_GREEN + "</li>\n" + ANSI_RESET);
            content += "</li>\n";
        }
        else {
            if(!isTheSameLevelList(tag)){
                System.out.println(ANSI_GREEN + "<ul>" + ANSI_RESET);
                content += "<ul>\n";
            }
            System.out.print("<li>");
            content += "<li>";
        }
    }

    @Override
    public void startElement(Tag tag) throws Exception {
        System.out.print(ANSI_YELLOW  + "<" +  tag.toString().toLowerCase() +">" + ANSI_RESET);
        content += "<" +  tag.toString().toLowerCase() +">";
    }

    @Override
    public void emoticonFind(String emoticon) throws Exception {
        System.out.print(ANSI_BLACK  + " " + emoticon + " " + ANSI_RESET);
        content += " " + emoticon + " ";
    }

    @Override
    public void endElement(Tag tag) throws Exception {
        System.out.print(ANSI_RED  + "</" +  tag.toString().toLowerCase() +">" + ANSI_RESET);
        content += "</" +  tag.toString().toLowerCase() +">";
    }

    @Override
    public void hyperlinkFind(String hyperlink) throws Exception {

        String [] hyperSplit = hyperlink.split("\\|");

        if(CheckTags.isImage(hyperSplit[1])){
            String imageName = (hyperSplit[1].substring(2)).substring(0,hyperSplit[1].length() -4);
            System.out.print(ANSI_PURPLE + "<a href=\"" + ANSI_BLUE + hyperSplit[0] +"\">" + ANSI_CYAN + "<img src=\"" + imageName + "\"/>" + ANSI_PURPLE + "</a>"+ ANSI_RESET);
            content += "<a href=\"" + hyperSplit[0] +"\">" + "<img src=\"" + imageName + "\"/>" + "</a>";
        }

        else {
            System.out.print(ANSI_PURPLE + "<a href=\"" + ANSI_BLUE + hyperSplit[0] + "\">" + ANSI_CYAN + hyperSplit[1] + ANSI_PURPLE + "</a>" + ANSI_RESET);
            content += "<a href=\"" + hyperSplit[0] + "\">" + hyperSplit[1] + "</a>";
        }
    }

    @Override
    public void imageFind(String imageLink) throws Exception {
        System.out.print(ANSI_PURPLE + "<img src=\"" + imageLink + "\"/>" + ANSI_PURPLE + "</a>"+ ANSI_RESET);
        content += "<img src=\"" + imageLink + "\"/>" + "</a>";
    }
    @Override
    public void emailFind(String email) throws Exception {
        System.out.print(ANSI_BLUE + "<a href=\" mailto:" + email + "\">" + email + "</a>" + ANSI_RESET);
        content += "<a href=\" mailto:" + email + "\">" + email + "</a>";
    }

    @Override
    public void text(String str) throws Exception {
        checkCloseList();
        System.out.print(ANSI_CYAN + str + ANSI_RESET);
        content += str;
    }

    private void checkCloseList() {
        if(!Scanner.list){
            for(int i = listInScope.size() - 1; i >= 0; i--){
                listInScope.remove(i);
                System.out.println(ANSI_GREEN + "</ul>" + ANSI_RESET);
                content += "</ul>\n";
            }

        }
    }

    public void tableHeadlineFound(String str) throws Exception {

        int countFalse = 0;

        char[] strArray = str.toCharArray();

        for(int i = 0; i < strArray.length; i++){
            if(strArray[i] == '^'){
                countFalse = i+1;
                break;
            }
        }

        ParserHandler handler = new ParserHandlerImp();
        System.out.print(ANSI_GREEN + "<th>");
        String finishContent = Scanner.checkTagsIn(str.substring(countFalse), handler);
        System.out.print(ANSI_GREEN + "</th> \n" + ANSI_RESET);
        content += "<th>" + finishContent + "</th> \n";
    }

    public void tableColumnFound(String str) throws Exception {

        int countFalse = 0;

        char[] strArray = str.toCharArray();

        for(int i = 0; i < strArray.length; i++){
            if(strArray[i] == '^'){
                countFalse = i+1;
                break;
            }
        }

        ParserHandler handler = new ParserHandlerImp();
        System.out.print(ANSI_GREEN + "<td>");
        String finishContent = Scanner.checkTagsIn(str.substring(countFalse), handler);
        System.out.print(ANSI_GREEN + "</td> \n" + ANSI_RESET);
        content += "<td>" + finishContent + "</td> \n";
    }


    public void startEndTable(boolean isTable) throws Exception {
        if(isTable == true){
            System.out.print(ANSI_GREEN + "</table>" + ANSI_RESET + "\n");
            content += "</table>\n";
        }else{
            System.out.print("\n" + ANSI_GREEN + "<table>" + ANSI_RESET + "\n");
            content += "\n" + "<table>" + "\n";
        }
    }

    public void startEndRow(boolean isTableRow) throws Exception {
        if(isTableRow == true){
            System.out.print(ANSI_GREEN + "</tr>" + ANSI_RESET + "\n");
            content += "</tr>" + "\n";
        }else{
            System.out.print(ANSI_GREEN + "<tr>" + ANSI_RESET + "\n");
            content +="<tr>" + "\n";
        }
    }
}
