package com.example.fevertracker;

import java.util.ArrayList;

public class convertor {
    ArrayList<Integer> size1=new ArrayList<>(),size2=new ArrayList<>();
    public void add(int printedLoc,int allLoc){
        size1.add(printedLoc);
        size2.add(allLoc);
    }
    public void clear(){
        size1.clear();
        size2.clear();
    }
    public int convert(int printedLoc){
        return size2.get(printedLoc);
    }
}
