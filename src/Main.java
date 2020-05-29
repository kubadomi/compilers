import com.project.parser.Scanner;
import com.project.parser.ParserHandler;
import com.project.parser.ParserHandlerImp;
import com.project.parser.exceptions.NoCloseTagException;
import com.project.parser.test.CheckTests;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        ParserHandler parserHandler = new ParserHandlerImp();
        try {
            Scanner.parser(parserHandler, "resources/test.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("TESTY");

        try{
            CheckTests.checkCloseTags(parserHandler);
            System.out.println("TEST PASSED!! All tags are closed.");
        } catch (NoCloseTagException e) {
            System.out.println("TEST FAIL!! Some tags are not closed.");
        }

    }
}
