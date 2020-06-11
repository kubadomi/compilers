package com.project.parser.exceptions;

/**
 * Created by Konrad on 2015-05-18.
 */
public class NoCloseTagException extends Exception{
    int manyNoCloseTag;
    public NoCloseTagException(int manyNoCloseTag){
        super();
        this.manyNoCloseTag = manyNoCloseTag;
    }
}
