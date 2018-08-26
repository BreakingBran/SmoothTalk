package com.example.lance.ht6;

class PhilData {

    String word;
    Posn[] posns;

    public PhilData(String word, Posn[] posns){
        this.word = word;
        this.posns = posns;
    }

    public Posn[] getPosns() {
        return this.posns;
    }

    public String word() {
        return this.word;
    }

}
