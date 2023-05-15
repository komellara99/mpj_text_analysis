package com.company;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import mpi.*;

import javax.crypto.spec.PSource;

public class Main {

    //Analysis of ngrams- Lara Komel (89191056)
    //text files za testiranje so shranjeni/se shranijo v mapo datotekeZaTestiranje


    public static int n = 2;
    public static String [] nGram;



    //izbira verzije programa

    public static boolean sequential = false; //sekvencno
    public static boolean distrubuted = true; //porazdeljeno
    public static boolean parallel = false; //vzporedno z delavci za vsak avail. processor



    public static boolean vzp = false; //vzporedno z delavcem za vsak ngram
    public static int numThreads;
    public static FileWriter filewriter;
    public static Scanner scanner;
    public static int piece;
    public static int numwords1;
    public static String [] proba = {"hi", "hi", "nki", "hi", "neki", "bla", "bla", "bla", "bk", "bess", "nkf", "nki", "bk", "bess", "nkf", "nki", "wg", "jke", "jjjj", "nki"};


    /**
     * @param args
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //numThreads = Runtime.getRuntime().availableProcessors();
        //proba = ["hi", "hi", "nki", "hi", "neki", "bla", "bla", "bla", "bk", "bess", "nkf", "nki", "bk", "bess", "nkf", "nki", "wg", "jke", "jjjj", "nki"];

        String bes = "";

        String rez = "";
        String rez2 = "";
        Object[] ob;
        int [] stbesed = new int[1];



        if(distrubuted){
            double zacetnicasporazdeljeno = System.currentTimeMillis();

            MPI.Init(args);
            int me = MPI.COMM_WORLD.Rank();
            int size = MPI.COMM_WORLD.Size();
            FileWriter filewriter1 = new FileWriter("../DatotekeZaTestiranje/rezultat2.txt");
            //piece = (int)(Math.floor((double)(nGram.length)/size)); //numthread - 1 teh, zadnj pa z vsemi ki ostanejo
            //int lastpiece = nGram.length - (piece*(size-1)); //velikost zadnjega dela

            if (me==0) {
                //testiranje
                File _1000b = new File("../DatotekeZaTestiranje/1000b-file.txt");
                File _10000b = new File("../DatotekeZaTestiranje/10000b-file.txt");
                File _100000b = new File("../DatotekeZaTestiranje/100000b-file.txt");
                File _1000000b = new File("../DatotekeZaTestiranje/1000000b-file.txt");
                File _10000000b = new File("../DatotekeZaTestiranje/10000000b-file.txt");
                File _20mb = new File("../DatotekeZaTestiranje/20mb.txt");

                File file100 = new File("../DatotekeZaTestiranje/sample3.txt");//okoli 4000b
                File file100d = new File("../DatotekeZaTestiranje/sample3.txt");//okoli 4000b
                Scanner scanner1 = new Scanner(file100);

                //rezultat se shrani v
                filewriter1 = new FileWriter("../DatotekeZaTestiranje/rezultat2.txt");

                while(scanner1.hasNextLine()){

                    bes = bes + scanner1.nextLine();

                }
                scanner1.close();
                nGram = bes.split(" ");
                stbesed[0]=nGram.length;
            }
            MPI.COMM_WORLD.Bcast(stbesed, 0, 1, MPI.INT, 0);
            ob = new Object[stbesed[0]];

            if (me==0){
                for (int i = 0; i < nGram.length; i++) {
                    ob[i]=nGram[i]; //spremeni v object array
                }
            }
            piece = (int)(Math.floor((double)(stbesed[0])/size)); //numthread - 1 teh, zadnj pa z vsemi ki ostanejo
            int lastpiece = stbesed[0] - (piece*(size-1)); //velikost zadnjega dela
            //System.out.println("st besed: "+stbesed[0]+", prvi kosi "+piece+", teh je "+(size-1)+", zadnji pa "+lastpiece);
            MPI.COMM_WORLD.Bcast(ob, 0, stbesed[0], MPI.OBJECT, 0);

            String[]arr = new String[stbesed[0]];
            for (int i = 0; i < arr.length; i++) {
                arr[i]= ob[i].toString();
            }

            nGram = arr;

            if (me!=(size-1)){

                for (int i = (me*piece); i <= ((me*piece)+piece); i++) {
                    String[] z = Main.splitVSubstring(arr, i);
                    rez2 = rez2.concat("Ponovitev " + Main.ret(z) + ": " +
                            Main.stPonovitev(z) + " ,Verjetnost, da '" + z[0] + "' sledi '" +
                            Main.retod1(z) + "' je: " + (double) (Main.stPonovitev(z)) / Main.stNgramovStarting(z[0]) + "\n");

                }
                //MPI.COMM_WORLD.Bcast(stev, 0, 1, MPI.INT, 0);
            }else {

                for (int i = ob.length-lastpiece; i <= ob.length-n+1; i++) {

                    String[] z = Main.splitVSubstring(arr, i);
                    rez2 = rez2.concat("Ponovitev " + Main.ret(z) + ": " +
                            Main.stPonovitev(z) + " ,Verjetnost, da '" + z[0] + "' sledi '" +
                            Main.retod1(z) + "' je: " + (double) (Main.stPonovitev(z)) / Main.stNgramovStarting(z[0]) + "\n");

                }

                for (int i = lastpiece; i < piece+1; i++) {
                    rez2 = rez2.concat("."+"\n");

                }
               // MPI.COMM_WORLD.Bcast(stev2, 0, 1, MPI.INT, size-1);
            }
            String [] koncniarr = rez2.split("\n");
            Object [] koncniobj = new Object[koncniarr.length];
            Object [] rezultat = new Object[size*koncniobj.length];
            for (int i = 0; i < koncniarr.length; i++) {
                koncniobj[i]=koncniarr[i];
            }
            MPI.COMM_WORLD.Gather(koncniobj, 0, koncniarr.length, MPI.OBJECT,
                    rezultat, 0, koncniobj.length, MPI.OBJECT, 0);
            if (me==0){

                for (int i = 0; i < rezultat.length; i++) {
                    filewriter1.write(rezultat[i].toString());
                    filewriter1.write("\n");
                }
                //filewriter1.write(koncnitext);
            }


            System.out.println("done");
            System.out.println("cas porazd: "+(System.currentTimeMillis()-zacetnicasporazdeljeno));
            MPI.Finalize();
            //System.out.println("cas porazd: "+(System.currentTimeMillis()-zacetnicasporazdeljeno));

        }else{

        //testiranje
        File _1000b = new File("../DatotekeZaTestiranje/1000b-file.txt");
        File _10000b = new File("../DatotekeZaTestiranje/10000b-file.txt");
        File _100000b = new File("../DatotekeZaTestiranje/100000b-file.txt");
        File _1000000b = new File("../DatotekeZaTestiranje/1000000b-file.txt");
        File _10000000b = new File("../DatotekeZaTestiranje/10000000b-file.txt");
        File _20mb = new File("../DatotekeZaTestiranje/20mb.txt");

        File file100 = new File("../DatotekeZaTestiranje/sample3.txt");//okoli 4000b
        scanner = new Scanner(file100);

        //rezultat se shrani v
        filewriter = new FileWriter("../DatotekeZaTestiranje/rezultat2.txt");

        while(scanner.hasNextLine()){

                bes = bes + scanner.nextLine();

        }
        scanner.close();

        nGram = bes.split(" ");
        numwords1 = nGram.length;

////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////
        if(parallel){
            Worker2 [] delavci = new Worker2[numThreads];

            piece = (int)(Math.floor((double)(nGram.length)/numThreads)); //numthread - 1 teh, zadnj pa z vsemi ki ostanejo
            int lastpiece = nGram.length - (piece*(numThreads-1)); //velikost zadnjega dela

            double zacetnicasthree = System.currentTimeMillis();
            for (int i = 0; i < numThreads; i++) {
                if(i!=numThreads-1) {
                    Worker2 w = new Worker2(i * piece, (i * piece) + piece);
                    w.start();
                    delavci[i] = w;
                    //System.out.println("startan worker " + i);
                }else {
                    Worker2 wx = new Worker2(nGram.length-lastpiece, nGram.length-1);
                    wx.start();
                    delavci[i] = wx;
                    //System.out.println("startan worker " + i);
                }
            }

            for (int i = 0; i < numThreads; i++) {
                delavci[i].join();
            }
            double koncnicasthree = System.currentTimeMillis();
            System.out.println("Paralelni čas porabljen za stbesed iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasthree-zacetnicasthree)+" milisekund");
            String rez1 = "Paralelni čas porabljen za stbesed iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasthree-zacetnicasthree)+" milisekund";
            filewriter.write(rez1);
            filewriter.close();



        }else{
        double zacetnicasseq = System.currentTimeMillis();
        if (sequential){
        //nGram = stbesed.split(" ");
        for (int i = 0; i < nGram.length-(n-1); i++) {
            rez="";
            String [] z = splitVSubstring(nGram,i);

            //print out rezultat namesto v datoteko

            /*System.out.print("Ponovitev ");
            print(splitVSubstring(nGram,i));
            System.out.print(": ");
            System.out.print(stPonovitev(splitVSubstring(nGram,i)));

            System.out.print(" ,Verjetnost, da '"+ z[0]+"' sledi '");
            printod1(z);
            System.out.println("' je: "+ (double)(stPonovitev(z))/stNgramovStarting(z[0]));

            System.out.println();*/

            rez = rez.concat("Ponovitev "+ ret(z) +": "+
                    stPonovitev(splitVSubstring(nGram,i))+" ,Verjetnost, da '"+ z[0]+"' sledi '"+
                    retod1(z)+"' je: "+ (double)(stPonovitev(z))/stNgramovStarting(z[0])+"\n");
            filewriter.write(rez);
        }
        double koncnicasseq = System.currentTimeMillis();
        System.out.println("Sekvenčni čas porabljen za stbesed iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasseq-zacetnicasseq)+" milisekund");
        String rez1 = "Sekvenčni čas porabljen za stbesed iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasseq-zacetnicasseq)+" milisekund";
         filewriter.write(rez1);
         filewriter.close();

    }
    else if (vzp){
        Worker [] delavci = new Worker[nGram.length-(n-1)];
            double zacetnicaspar = System.currentTimeMillis();
            for (int i = 0; i < nGram.length-(n-1); i++) {
                Worker w = new Worker(i);
                w.start();
                delavci[i] = w;
            }
            for (int i = 0; i < nGram.length-(n-1); i++) {
                delavci[i].join();
            }
            double koncnicaspar = System.currentTimeMillis();
            System.out.println("Paralelni čas porabljen za stbesed iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicaspar-zacetnicaspar)+" milisekund");
            String rez1 = "Paralelni čas porabljen za stbesed iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicaspar-zacetnicaspar)+" milisekund";
            filewriter.write(rez1);
            filewriter.close();



        }}


    }}
    //vrne array od indexa k besedila za n besed naprej
    public static String[] splitVSubstring(String [] bes, int k){
        String [] zacasni = new String[n];
        if (bes.length-k >= n){
        for (int i = 0; i < n ; i++) {
            zacasni[i]=bes[k];
            k=k+1;
        }}
        return zacasni;
    }


    //ideja- iz besedila naredi vsa mozen podstring v zacasnega, primerja z
    //parametrom, ce je isti poveca ponovitve
    public static int stPonovitev(String [] b){
        int ponovitve = 0;
        int mesto;
        String [] zac = new String[n];
        for (int i = 0; i < nGram.length-(n-1); i++) {
            mesto = 0;
            for (int j = i; j < i+n; j++) {
                zac[mesto]=nGram[j];
                mesto++;
            }
            //System.out.print("Pomozni: ");
            //print(zac);
            if (checkEquality(zac,b)){
                ponovitve++;
            }
        }
        return ponovitve;
    }

    //vrne stevilo n gramov v besedilu ki se zacnejo z podano besedo
    public static int stNgramovStarting(String s){
        int st = 0;
        for (int i = 0; i < nGram.length-(n-1); i++) {
            String[] zac = splitVSubstring(nGram,i);
            if (zac[0].equals(s)){
                st++;
            }
        }
        return st;
    }
    //print array
    public static void print(String [] gram){
        for (int i = 0; i < gram.length; i++) {
            System.out.print(gram[i]+" ");
        }
    }
    //print array razen 1.besede
    public static void printod1(String [] gram){
        for (int i = 1; i < gram.length; i++) {
            System.out.print(gram[i]+" ");
        }
    }

    public static String ret(String [] gram){
        String zac = "";
        for (int i = 0; i < gram.length; i++) {
            zac = zac.concat(gram[i]+" ");
        }
        return zac;
    }

    public static String retod1(String [] gram){
        String zac = "";
        for (int i = 1; i < gram.length; i++) {
            zac = zac.concat(gram[i]+" ");
        }
        return zac;
    }



    //ali sta polji enaki
    public static boolean checkEquality(String[] s1, String[] s2) {
        if (s1 == s2)
            return true;

        if (s1 == null || s2 == null)
            return false;

        int n = s1.length;
        if (n != s2.length)
            return false;

        for (int i = 0; i < n; i++) {
            if (!s1[i].equals(s2[i]))
                return false;
        }

        return true;
    }

}
