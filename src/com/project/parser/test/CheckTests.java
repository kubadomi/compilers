package com.project.parser.test;

import com.project.parser.ParserHandler;
import com.project.parser.exceptions.NoCloseTagException;
import com.project.parser.exceptions.NoPropertyListException;

public class CheckTests {
    public static void checkCloseTags(ParserHandler parserHandler) throws NoCloseTagException{
        if(parserHandler.getAmountNoCloseTag() > 0)
            throw new NoCloseTagException(parserHandler.getAmountNoCloseTag());
    }

    public static void checkList(ParserHandler parserHandler) throws NoPropertyListException {
    }
}
