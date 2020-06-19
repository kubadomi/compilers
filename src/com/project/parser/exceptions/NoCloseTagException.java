package com.project.parser.exceptions;

public class NoCloseTagException extends Exception{
    int manyNoCloseTag;
    public NoCloseTagException(int manyNoCloseTag){
        super();
        this.manyNoCloseTag = manyNoCloseTag;
    }
}
