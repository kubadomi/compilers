package com.project.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckTags {
    public static final Map<String, Tag> tags;
    public static final Map<Tag,String> startTag;
    public static final Map<Tag,String> endTag;
    public static final LinkedList<String> emoticons;

    static {
        tags = new HashMap<>();

        tags.put("del", Tag.DEL);
        tags.put("sub", Tag.SUB);
        tags.put("sup", Tag.SUP);
        tags.put("nowiki", Tag.NOWIKI);
        tags.put("code", Tag.CODE);
//        tags.put("sup", Tag.SUP);
//        tags.put("sup", Tag.SUP);

        emoticons = new LinkedList<>();
        emoticons.add("8-)");
        emoticons.add("8-O");
        emoticons.add(":-(");
        emoticons.add(":-)");
        emoticons.add(":-/");
        emoticons.add(":-\\");
        emoticons.add(":-?");
        emoticons.add(":-D");
        emoticons.add(":-P");
        emoticons.add(":-O");
        emoticons.add(":-X");
        emoticons.add(":-|");
        emoticons.add(";-)");
        emoticons.add("^_^");
        emoticons.add(":?:");
        emoticons.add(":!:");
        emoticons.add("LOL");
        emoticons.add("FIX");
        emoticons.add("-->");
        emoticons.add("<->");
        emoticons.add("<=>");
        emoticons.add("---");
        emoticons.add("(c)");
        emoticons.add("(t)");
        emoticons.add("(r)");

        startTag = new HashMap<>();
        startTag.put(Tag.BOLD, "<b>");
        startTag.put(Tag.ITALIC, "<i>");
        startTag.put(Tag.UNDERLINE, "<u>");
        startTag.put(Tag.MONOSPACED, "<tt>");
        startTag.put(Tag.SUB, "<sub>");
        startTag.put(Tag.SUP, "<sup>");
        startTag.put(Tag.DEL, "<del>");
//        startTag.put(Tag.FOOTNOTE, "");
        startTag.put(Tag.NOWIKI, "<nowiki>");
        startTag.put(Tag.CODE, "<code>");
        startTag.put(Tag.HEADLINE3, "<h3>");
        startTag.put(Tag.HEADLINE4, "<h2>");
        startTag.put(Tag.HEADLINE5, "<h1>");
        startTag.put(Tag.FOOTNOTE, "((");

        endTag = new HashMap<>();
        endTag.put(Tag.BOLD, "</b>");
        endTag.put(Tag.ITALIC, "</i>");
        endTag.put(Tag.UNDERLINE, "</u>");
        endTag.put(Tag.MONOSPACED, "</tt>");
        endTag.put(Tag.SUB, "</sub>");
        endTag.put(Tag.SUP, "</sup>");
        endTag.put(Tag.DEL, "</del>");
//        endTag.put(Tag.FOOTNOTE, "");
        endTag.put(Tag.NOWIKI, "</nowiki>");
        endTag.put(Tag.CODE, "</code>");
        endTag.put(Tag.HEADLINE3, "</h3>");
        endTag.put(Tag.HEADLINE4, "</h2>");
        endTag.put(Tag.HEADLINE5, "</h1>");
        endTag.put(Tag.FOOTNOTE, "))");
    }

    public static boolean checkTagExist(String tag) {
        for (String s : tags.keySet()) {
            if (s.equals(tag))
                return true;
        }
        return false;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean isImage(String address){
        char [] chars = address.toCharArray();
        if(chars[0] == '{' && chars[1] == '{' &&  chars [chars.length - 2] == '}' && chars [chars.length -1] == '}')
            return true;
        return false;
    }
    public static boolean isEmoticon(String emoticon){
        for(String s : emoticons)
            if(s.equals(emoticon))
                return true;
        return false;
    }

    public static Tag getTagFromString(String tagString){
        return tags.get(tagString);
    }
}
