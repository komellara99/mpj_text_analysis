package com.company;

import java.io.IOException;

public class Worker extends Thread {
    int start1, finish1;
    public Worker(int start){
       start1 = start;

    }
    @Override
    public void run() {
       String rez="";
        String [] z = Main.splitVSubstring(Main.nGram,start1);

        //print out rezultat namesto v datoteko

            /*System.out.print("Ponovitev ");
            print(splitVSubstring(nGram,i));
            System.out.print(": ");
            System.out.print(stPonovitev(splitVSubstring(nGram,i)));

            System.out.print(" ,Verjetnost, da '"+ z[0]+"' sledi '");
            printod1(z);
            System.out.println("' je: "+ (double)(stPonovitev(z))/stNgramovStarting(z[0]));

            System.out.println();*/

        rez = rez.concat("Ponovitev "+ Main.ret(z) +": "+
                Main.stPonovitev(z)+" ,Verjetnost, da '"+ z[0]+"' sledi '"+
                Main.retod1(z)+"' je: "+ (double)(Main.stPonovitev(z))/Main.stNgramovStarting(z[0])+"\n");
        try {
            Main.filewriter.write(rez);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
