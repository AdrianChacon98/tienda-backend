package com.store.example.exceptionHandler;

public class ExceptionHandlerUnchecked {


    public static RuntimeException handlerException(Exception exception){
        ExceptionHandlerUnchecked.<RuntimeException>throwTheException(exception);

        throw new AssertionError("This line never going to throw but java doesnt know it");
    }

    private static <T extends Exception> void throwTheException(Exception toThrow)throws T{
        throw (T) toThrow;
    }


}
