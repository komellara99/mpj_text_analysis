package com.company;

import java.io.IOException;
import mpi.*;

public class Worker2 extends Thread {
    private int start1, finish1;
    public Worker2(int start, int finish){
        start1 = start;
        finish1 = finish;

    }
    @Override
    public void run() {
        String rez="";

        for (int i = start1; i <= finish1; i++) {
            String [] z = Main.splitVSubstring(Main.nGram,start1);
            rez = rez.concat("Ponovitev "+ Main.ret(z) +": "+
                    Main.stPonovitev(z)+" ,Verjetnost, da '"+ z[0]+"' sledi '"+
                    Main.retod1(z)+"' je: "+ (double)(Main.stPonovitev(z))/Main.stNgramovStarting(z[0])+"\n");
            //System.out.println("dela worker "+(start1/Main.piece)+" in dela na: "+rez);
            try {
                Main.filewriter.write(rez);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //print out rezultat namesto v datoteko

            /*System.out.print("Ponovitev ");
            print(splitVSubstring(nGram,i));
            System.out.print(": ");
            System.out.print(stPonovitev(splitVSubstring(nGram,i)));

            System.out.print(" ,Verjetnost, da '"+ z[0]+"' sledi '");
            printod1(z);
            System.out.println("' je: "+ (double)(stPonovitev(z))/stNgramovStarting(z[0]));

            System.out.println();*/



    }
}
