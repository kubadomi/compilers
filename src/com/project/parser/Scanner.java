package com.project.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Stack;

public class Scanner {
    static boolean list = false;
    public static void parser(ParserHandler handler, String filename) throws FileNotFoundException, Exception {
        Stack<Tag> stack = new Stack();
        HashMap<String, String> attributes = new HashMap<>();
        String tagName;
        int line = 1;
        int col = 1;
        int depth = 0;
        int list = 0;
        boolean isEmoticon = false;
        int emoticon = 0;
        StringBuffer sb = new StringBuffer();
        Tag mode = Tag.TEXT;
        Tag modeToTable = Tag.TEXT;
        boolean isTable = false;
        boolean isTableRow = false;
        boolean eol = false;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {

            int currentChar;

            while ((currentChar = bufferedReader.read()) != -1) {
                if (mode == Tag.START_NEW_LINE && (currentChar == '\n' || currentChar == '\r')) {
                    if (currentChar == '\r') {
                        continue;
                    } else if (currentChar == '\n') {
                        sb.append('\r');
                        sb.append('\n');
                        continue;
                    }
                }else if (mode == Tag.START_NEW_LINE) {
                    if (currentChar == ' ') {
                        mode = Tag.START_LIST;
                        if(isTableRow == true){
                              handler.startEndRow(true);
                            isTableRow = false;
                        }
                        if(isTable == true){
                              handler.startEndTable(true);
                            isTable = false;
                        }

                        //TABELA
                    } else if (currentChar == '^') {
                        if(isTableRow == true){
                              handler.startEndRow(true);
                              handler.startEndRow((false));
                        }
                        mode = Tag.START_TABLE_HEADING;
                        modeToTable = Tag.START_TABLE_HEADING;
                        continue;
                    } else if (currentChar == '|') {
                        if(isTableRow == true){
                              handler.startEndRow(true);
                              handler.startEndRow((false));
                        }
                        mode = Tag.START_TABLE_COLUMN;
                        modeToTable = Tag.START_TABLE_COLUMN;
                    } else {
                        mode = Tag.TEXT;
                        modeToTable = Tag.TEXT;
                        if(isTableRow == true){
                              handler.startEndRow(true);
                            isTableRow = false;
                        }
                        if(isTable == true){
                              handler.startEndTable(true);
                            isTable = false;
                        }
                    }
                }
                    if (mode == Tag.DONE) {
//                        handler.endDocument();
                        return;
                    } else if (mode == Tag.TEXT) {
                        if (currentChar == '\r') { //new line?
                            continue;
                        }


                        else if (currentChar == '\n') {
                            if(modeToTable == Tag.START_TABLE_COLUMN || modeToTable == Tag.TABLE_COLUMN || modeToTable == Tag.START_TABLE_HEADING || modeToTable == Tag.TABLE_HEADING){
                                sb.delete(0, sb.length());
                            }
                            mode = Tag.START_NEW_LINE;
                            continue;
                        } else if (currentChar == '\\') {
                            if(modeToTable == Tag.START_TABLE_COLUMN || modeToTable == Tag.TABLE_COLUMN || modeToTable == Tag.START_TABLE_HEADING || modeToTable == Tag.TABLE_HEADING){
                                sb.delete(0, sb.length());
                            }
                            mode = Tag.START_NEW_LINE_TAG;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        } else if (currentChar == '[') {
                            mode = Tag.START_HYPERLINK;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        } else if (currentChar == '{') {
                            mode = Tag.START_IMAGE;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        } else if (currentChar == '*' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //bold
                            mode = Tag.START_BOLD;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        } else if (currentChar == '/' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //italic
                            mode = Tag.START_ITALIC;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        } else if (currentChar == '_' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //underline
                            mode = Tag.START_UNDERLINE;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;

                        } else if (currentChar == '\'' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //monospaced
                            mode = Tag.START_MONOSPACED;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        } else if (currentChar == '<' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //special tags
                            mode = Tag.START_TAG;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        } else if (currentChar == '(' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //special tags
                            mode = Tag.START_FOOTNOTE;
//                            if (sb.length() > 0) {
//                                handler.text(sb.toString());
//                                sb.setLength(0);
//                            }
                            continue;
                        } else if (currentChar == ')' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //special tags
                            mode = Tag.END_FOOTNOTE;
//                            if (sb.length() > 0) {
//                                handler.text(sb.toString());
//                                sb.setLength(0);
//                            }
                            continue;
                        }
                        else if (currentChar == '=' && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN) { //start headline
                            mode = Tag.START_HEADLINE;
                            if (sb.length() > 0) {
                                  handler.text(sb.toString());
                                sb.setLength(0);
                            }
                            continue;
                        }

                        else if(currentChar == '^'){
                            if(modeToTable == Tag.START_TABLE_HEADING){
                                if(isTable == false){
                                      handler.startEndTable(false);
                                    isTable = true;
                                }
                                if(isTableRow == false){
                                      handler.startEndRow(false);
                                    isTableRow = true;
                                }
                                  handler.tableHeadlineFound(sb.toString());
                                sb.setLength(0);
                            }
                        }

                        else if(currentChar == '|'){
                            if (modeToTable == Tag.START_TABLE_COLUMN){
                                  handler.tableColumnFound(sb.toString());
                                sb.setLength(0);
                            }
                        }

                        else if (currentChar == ' ' || isEmoticon) {
                            if(emoticon == 3){
                                if(CheckTags.isEmoticon(sb.toString())) {
                                      handler.emoticonFind(sb.toString());
                                      handler.text(" ");
                                    sb.setLength(0);
                                }
                                else{
                                    sb.append((char)currentChar);
                                }

                            if(isEmoticon)
                                sb.append((char)currentChar);

                                if(sb.length() > 0) {
                                      handler.text(sb.toString());
                                    sb.setLength(0);
                                }
                                emoticon = 0;
                                isEmoticon = false;
                                continue;
                            }else{
                                emoticon = 0;
                                isEmoticon = false;
                                sb.append((char)currentChar);
                                if(sb.length() > 0 && modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN){
                                      handler.text(sb.toString());
                                    sb.setLength(0);
                                }
                            }
                        } else {
                            sb.append((char) currentChar);
                            emoticon++;
                        }
                    } else if (mode == Tag.START_NEW_LINE_TAG) {
                        if (currentChar == '\\') {
                            mode = Tag.NEW_LINE_TAG;
                        } else {
                            sb.append('\\');
                            sb.append((char) currentChar);
                        }
                    } else if (mode == Tag.NEW_LINE_TAG) {
                        if (currentChar == ' ') {
                            sb.append('\n');
                        }else if(currentChar == '\r' || currentChar == '\n') {
                            sb.append('\n');
                        }
                        else {
                            sb.append("\\\\");
                            sb.append((char) currentChar);
                        }
                        mode = Tag.TEXT;



                    } else if (mode == Tag.START_BOLD) {
                        if (currentChar == '*') {
//                            if(modeToTable == Tag.START_TABLE_HEADING || modeToTable == Tag.START_TABLE_COLUMN) {
//                                System.out.print("BwH");
//                                handler.elementFound(Tag.BOLD);
//                            }else{
                            if(modeToTable != Tag.START_TABLE_HEADING && modeToTable != Tag.START_TABLE_COLUMN){
                                  handler.elementFound(Tag.BOLD);
                                sb.setLength(0);
                            }

                            mode = Tag.TEXT;
                        } else {
                            sb.append('*');
                            sb.append((char) currentChar);
                            mode = Tag.TEXT;
                        }
                    } else if (mode == Tag.START_ITALIC) {
                        if (currentChar == '/') {
                              handler.elementFound(Tag.ITALIC);
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            sb.append('/');
                            sb.append((char) currentChar);
                            mode = Tag.TEXT;
                        }
                    } else if (mode == Tag.START_UNDERLINE) {
                        if (currentChar == '_') {
                              handler.elementFound(Tag.UNDERLINE);
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            sb.append('_');
                            sb.append((char) currentChar);
                            mode = Tag.TEXT;
                        }
                    } else if (mode == Tag.START_MONOSPACED) {
                        if (currentChar == '\'') {
                            handler.elementFound(Tag.MONOSPACED);
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            sb.append('\'');
                            sb.append((char) currentChar);
                        }
                    } else if (mode == Tag.START_TAG) {
                        if (currentChar == '/') {
                            mode = Tag.END_TAG;
                            continue;
                        } else {
                            if (currentChar == '>') {
                                mode = Tag.TEXT;
                                  handler.text("<>");
                                sb.setLength(0);
                                continue;
                            } else {
                                mode = Tag.IN_TAG;
                                sb.append((char) currentChar);
                            }
                        }
                    } else if (mode == Tag.IN_TAG) {
                        if (currentChar == '>') {
                            if (CheckTags.isEmail(sb.toString())) {
                                  handler.emailFind(sb.toString());
                            } else if (CheckTags.checkTagExist(sb.toString()))
                                  handler.startElement(CheckTags.getTagFromString(sb.toString()));
                            else
                                  handler.text("<" + sb.toString() + ">");
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            sb.append((char) currentChar);
                        }
                    } else if (mode == Tag.END_TAG) {
                        if (currentChar == '>') {
                            if (CheckTags.checkTagExist(sb.toString()))
                                  handler.endElement(CheckTags.getTagFromString(sb.toString()));
                            else
                                  handler.text("</" + sb.toString() + ">");
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            sb.append((char) currentChar);
                        }
                    } else if (mode == Tag.START_HYPERLINK) {
                        if (currentChar == '[') {
                            mode = Tag.INNER_HYPERLINK;
                            continue;
                        } else {
                            sb.append('[');
                            sb.append((char) currentChar);
                            mode = Tag.TEXT;
                            continue; //?
                        }
                    } else if (mode == Tag.INNER_HYPERLINK) {
                        if (currentChar == ']') {
                            mode = Tag.END_HYPERLINK;
                            continue;
                        } else {
                            sb.append((char) currentChar);
                        }
                    } else if (mode == Tag.END_HYPERLINK) {
                        if (currentChar == ']') {
                              handler.hyperlinkFind(sb.toString());
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            sb.append(']');
                            sb.append((char) currentChar);
                            StringBuffer tmp = new StringBuffer();
                            tmp.append(sb);
                            sb.setLength(0);
                            sb.append("[[");
                            sb.append(tmp);
                            mode = Tag.INNER_HYPERLINK;
                            continue;
                        }
                    } else if (mode == Tag.START_IMAGE) {
                        if (currentChar == '{') {
                            mode = Tag.INNER_IMAGE;
                            continue;
                        } else {
                            sb.append('{');
                            sb.append((char) currentChar);
                            mode = Tag.TEXT;
                            continue; //?
                        }
                    } else if (mode == Tag.INNER_IMAGE) {
                        if (currentChar == '}') {
                            mode = Tag.END_IMAGE;
                            continue;
                        } else {
                            sb.append((char) currentChar);
                        }
                    } else if (mode == Tag.END_IMAGE) {
                        if (currentChar == '}') {
                              handler.imageFind(sb.toString());
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            sb.append('}');
                            sb.append((char) currentChar);
                            StringBuffer tmp = new StringBuffer();
                            tmp.append(sb);
                            sb.setLength(0);
                            sb.append("{{");
                            sb.append(tmp);
                            mode = Tag.INNER_IMAGE;
                            continue;
                        }
                    } else if (mode == Tag.START_FOOTNOTE) {
                        if (currentChar == '(') {
                              handler.text(sb.toString());
                              handler.elementFound(Tag.FOOTNOTE);
                            sb.setLength(0);
                            mode = Tag.TEXT;
                        } else {
                            if(currentChar == ' ') {
                                isEmoticon = true;
                                sb.append('(');
                                emoticon++;
                            }else{
                                sb.append('(');
                                sb.append((char) currentChar);
                            }
                            mode = Tag.TEXT;
                            continue;
                        }
                    } else if (mode == Tag.END_FOOTNOTE) {
                        if (currentChar == ')') {
                              handler.text(sb.toString());
                              handler.elementFound(Tag.FOOTNOTE);
                            sb.setLength(0);
                            mode = Tag.TEXT;
                            continue;
                        } else {
                            if(currentChar == ' '){
                                isEmoticon = true;
                                sb.append(')');
                                emoticon++;
                            }else{
                                sb.append(")");
                                sb.append((char) currentChar);
                            }
                                mode = Tag.TEXT;
                            continue;
                        }
                    } else if (mode == Tag.START_LIST) {
                        if (sb.length() > 0) {
                              handler.text(sb.toString());
                            sb.setLength(0);
                        }

                        if (list == 2) {
                            if (currentChar == '*') {
                                   handler.listFind(Tag.LIST_1_1);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            } else if (currentChar == '-') {
                                  handler.listFind(Tag.LIST_2_1);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            } else if (currentChar == ' ') {
                                list++;
                            }
                        } else if (list == 4) {
                            if (currentChar == '*') {
                                  handler.listFind(Tag.LIST_1_2);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            } else if (currentChar == '-') {
                                  handler.listFind(Tag.LIST_2_2);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            } else if (currentChar == ' ') {
                                list++;
                            }
                        } else if (list == 6) {
                            if (currentChar == '*') {
                                  handler.listFind(Tag.LIST_1_3);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            } else if (currentChar == '-') {
                                  handler.listFind(Tag.LIST_2_3);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            }
                        } else if (list > 6) {
                            if (currentChar == '*') {
                                  handler.listFind(Tag.LIST_1_3);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            } else if (currentChar == '-') {
                                  handler.listFind(Tag.LIST_2_3);
                                list = 0;
                                mode = Tag.IN_LIST;
                                continue;
                            }
                        } else if (currentChar == ' ') {
                            list++;
//                        sb.append((char)currentChar);
                            continue;
                        } else {
                            list = 0;
                            sb.append(' ');
                            sb.append((char) currentChar);
                            mode = Tag.TEXT;
                            continue;
                        }
                    } else if (mode == Tag.IN_LIST) {
                        if (currentChar == '\r') {
                            if (sb.length() > 0) {
                                Scanner.list = true;
//                                  handler.text(sb.toString());
                                  checkTagsIn(sb.toString(),handler);

                                sb.setLength(0);
                            }
                              handler.listFind(Tag.END_LIST);
                            Scanner.list = false;
                            mode = Tag.TEXT;
                        } else {
                            sb.append((char) currentChar);
                        }
                    }

                    else if(modeToTable == Tag.START_TABLE_HEADING){


;                        if(currentChar == '^'){
                            if(isTable == false){
                                  handler.startEndTable(false);
                                isTable = true;
                            }
                            if(isTableRow == false){
                                  handler.startEndRow(false);
                                isTableRow = true;
                            }
                              handler.tableHeadlineFound(sb.toString());
                        }else{
                            sb.append('^');
                            sb.append((char) currentChar);
                        }
                        mode = Tag.TEXT;

                    }

                    else if(modeToTable == Tag.START_TABLE_COLUMN){
                        if(currentChar != '|'){
                            sb.append('|');
                            sb.append((char) currentChar);
                        }
                        mode = Tag.TEXT;

                    }

                    else if (mode == Tag.START_HEADLINE) {
                        if (currentChar == '=') {
                            mode = Tag.START_HEADLINE5;
//                        System.out.println("HEADLINE5");
                            continue;
                        } else {
                            sb.append('=');
                            sb.append((char) currentChar);
                        }
                        mode = Tag.TEXT;
                    } else if (mode == Tag.START_HEADLINE5) {
                        if (currentChar == '=') {
                            mode = Tag.START_HEADLINE4;
//                        System.out.println("HEADLINE4");
                            continue;
                        } else {
                              handler.elementFound(Tag.HEADLINE5);
                        }
                        mode = Tag.TEXT;
                    } else if (mode == Tag.START_HEADLINE4) {
                        if (currentChar == '=') {
                            mode = Tag.START_HEADLINE3;
//                        System.out.println("HEADLINES3");
                            continue;
                        } else {
                              handler.elementFound(Tag.HEADLINE4);
                        }
                        mode = Tag.TEXT;
                    } else if (mode == Tag.START_HEADLINE3) {
                          handler.elementFound(Tag.HEADLINE3);
                        mode = Tag.TEXT;
                    }


                }
            }
            if (sb.length() > 0)
                if(modeToTable != Tag.START_TABLE_HEADING) {
                      handler.text(sb.toString());
                }

            handler.saveToHTML();
        }


    public static String checkTagsIn(String textToCheck, ParserHandler handler)throws Exception{
        String context = "";
        boolean isEmoticon = false;
        int emoticon = 0;
        StringBuffer sb = new StringBuffer();
        Tag mode = Tag.TEXT;
        char[] charsString = textToCheck.toCharArray();

        for(char currentChar : charsString) {
            if (mode == Tag.TEXT) {
                if (currentChar == '[') {
                    mode = Tag.START_HYPERLINK;
                    if (sb.length() > 0) {
                          handler.text(sb.toString());
                        sb.setLength(0);
                    }
                    continue;
                } else if (currentChar == '{') {
                    mode = Tag.START_IMAGE;
                    if (sb.length() > 0) {
                          handler.text(sb.toString());
                        sb.setLength(0);
                    }
                    continue;
                } else if (currentChar == '*') { //bold
                    mode = Tag.START_BOLD;
                    if (sb.length() > 0) {
                          handler.text(sb.toString());
                        sb.setLength(0);
                    }
                    continue;
                } else if (currentChar == '/') { //italic
                    mode = Tag.START_ITALIC;
                    if (sb.length() > 0) {
                           handler.text(sb.toString());
                        sb.setLength(0);
                    }
                    continue;
                } else if (currentChar == '_') { //underline
                    mode = Tag.START_UNDERLINE;
                    if (sb.length() > 0) {
                          handler.text(sb.toString());
                        sb.setLength(0);
                    }
                    continue;

                } else if (currentChar == '\'') { //monospaced
                    mode = Tag.START_MONOSPACED;
                    if (sb.length() > 0) {
                          handler.text(sb.toString());
                        sb.setLength(0);
                    }
                    continue;
                } else if (currentChar == '<') { //special tags
                    mode = Tag.START_TAG;
                    if (sb.length() > 0) {
                          handler.text(sb.toString());
                        sb.setLength(0);
                    }
                    continue;
                } else if (currentChar == '(') { //special tags
                    mode = Tag.START_FOOTNOTE;
                    continue;
                } else if (currentChar == ')') { //special tags
                    mode = Tag.END_FOOTNOTE;
                    continue;
                } else if (currentChar == ' ' || isEmoticon) {
                    if (emoticon == 3) {
                        if (CheckTags.isEmoticon(sb.toString())) {
                              handler.emoticonFind(sb.toString());
                              handler.text(" ");
                            sb.setLength(0);
                        } else {
                            sb.append(currentChar);
                        }

                        if (isEmoticon)
                            sb.append(currentChar);

                        if (sb.length() > 0) {
                              handler.text(sb.toString());
                            sb.setLength(0);
                        }
                        emoticon = 0;
                        isEmoticon = false;
                        continue;
                    } else {
                        emoticon = 0;
                        isEmoticon = false;
                        sb.append(currentChar);
                        if (sb.length() > 0) {
                              handler.text(sb.toString());
                            sb.setLength(0);
                        }
                    }
                } else {
                    sb.append( currentChar);
                    emoticon++;
                }
            } else if (mode == Tag.START_BOLD) {
                if (currentChar == '*') {
                       handler.elementFound(Tag.BOLD);
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append('*');
                    sb.append(currentChar);
                    mode = Tag.TEXT;
                }
            } else if (mode == Tag.START_ITALIC) {
                if (currentChar == '/') {
                      handler.elementFound(Tag.ITALIC);
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append('/');
                    sb.append( currentChar);
                    mode = Tag.TEXT;
                }
            } else if (mode == Tag.START_UNDERLINE) {
                if (currentChar == '_') {
                      handler.elementFound(Tag.UNDERLINE);
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append('_');
                    sb.append(currentChar);
                    mode = Tag.TEXT;
                }
            } else if (mode == Tag.START_MONOSPACED) {
                if (currentChar == '\'') {
                      handler.elementFound(Tag.MONOSPACED);
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append('\'');
                    sb.append( currentChar);
                }
            } else if (mode == Tag.START_TAG) {
                if (currentChar == '/') {
                    mode = Tag.END_TAG;
                    continue;
                } else {
                    if (currentChar == '>') {
                        mode = Tag.TEXT;
                          handler.text("<>");
                        sb.setLength(0);
                        continue;
                    } else {
                        mode = Tag.IN_TAG;
                        sb.append( currentChar);
                    }
                }
            } else if (mode == Tag.IN_TAG) {
                if (currentChar == '>') {
                    if (CheckTags.isEmail(sb.toString())) {
                          handler.emailFind(sb.toString());
                    } else if (CheckTags.checkTagExist(sb.toString()))
                          handler.startElement(CheckTags.getTagFromString(sb.toString()));
                    else
                          handler.text("<" + sb.toString() + ">");
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append( currentChar);
                }
            } else if (mode == Tag.END_TAG) {
                if (currentChar == '>') {
                    if (CheckTags.checkTagExist(sb.toString()))
                          handler.endElement(CheckTags.getTagFromString(sb.toString()));
                    else
                          handler.text("</" + sb.toString() + ">");
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append(currentChar);
                }
            } else if (mode == Tag.START_HYPERLINK) {
                if (currentChar == '[') {
                    mode = Tag.INNER_HYPERLINK;
                    continue;
                } else {
                    sb.append('[');
                    sb.append( currentChar);
                    mode = Tag.TEXT;
                    continue; //?
                }
            } else if (mode == Tag.INNER_HYPERLINK) {
                if (currentChar == ']') {
                    mode = Tag.END_HYPERLINK;
                    continue;
                } else {
                    sb.append(currentChar);
                }
            } else if (mode == Tag.END_HYPERLINK) {
                if (currentChar == ']') {
                      handler.hyperlinkFind(sb.toString());
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append(']');
                    sb.append(currentChar);
                    StringBuffer tmp = new StringBuffer();
                    tmp.append(sb);
                    sb.setLength(0);
                    sb.append("[[");
                    sb.append(tmp);
                    mode = Tag.INNER_HYPERLINK;
                    continue;
                }
            } else if (mode == Tag.START_IMAGE) {
                if (currentChar == '{') {
                    mode = Tag.INNER_IMAGE;
                    continue;
                } else {
                    sb.append('{');
                    sb.append( currentChar);
                    mode = Tag.TEXT;
                    continue; //?
                }
            } else if (mode == Tag.INNER_IMAGE) {
                if (currentChar == '}') {
                    mode = Tag.END_IMAGE;
                    continue;
                } else {
                    sb.append(currentChar);
                }
            } else if (mode == Tag.END_IMAGE) {
                if (currentChar == '}') {
                      handler.imageFind(sb.toString());
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    sb.append('}');
                    sb.append(currentChar);
                    StringBuffer tmp = new StringBuffer();
                    tmp.append(sb);
                    sb.setLength(0);
                    sb.append("{{");
                    sb.append(tmp);
                    mode = Tag.INNER_IMAGE;
                    continue;
                }
            } else if (mode == Tag.START_FOOTNOTE) {
                if (currentChar == '(') {
                      handler.text(sb.toString());
                      handler.elementFound(Tag.FOOTNOTE);
                    sb.setLength(0);
                    mode = Tag.TEXT;
                } else {
                    if (currentChar == ' ') {
                        isEmoticon = true;
                        sb.append('(');
                        emoticon++;
                    } else {
                        sb.append('(');
                        sb.append(currentChar);
                    }
                    mode = Tag.TEXT;
                    continue;
                }
            } else if (mode == Tag.END_FOOTNOTE) {
                if (currentChar == ')') {
                      handler.text(sb.toString());
                      handler.elementFound(Tag.FOOTNOTE);
                    sb.setLength(0);
                    mode = Tag.TEXT;
                    continue;
                } else {
                    if (currentChar == ' ') {
                        isEmoticon = true;
                        sb.append(')');
                        emoticon++;
                    } else {
                        sb.append(")");
                        sb.append(currentChar);
                    }
                    mode = Tag.TEXT;
                    continue;
                }
            }

        }
        if(sb.length() > 0)
              handler.text(sb.toString());
    return context;
    }







}

