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


}
