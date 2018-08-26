package com.example.lance.ht6.utils;

import java.util.ArrayList;

public class ReportData {

    String word;
    ArrayList<Posn> posns;

    public ReportData(String word, ArrayList<Posn> posns){
        this.word = word;
        this.posns = posns;
    }

    public ArrayList<Posn> getPosns() {
        return this.posns;
    }

    public String getWord() {
        return this.word;
    }

}
