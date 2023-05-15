package com.company;

import java.io.*;
import java.util.Scanner;

public class Main {

    //Analysis of ngrams- Lara Komel (89191056)
    //text files za testiranje so shranjeni/se shranijo v mapo datotekeZaTestiranje


    public static int n = 3;
    public static String [] nGram;
    public static boolean seq = false;
    public static boolean parallel = true;
    public static int numThreads;
    public static FileWriter filewriter;
    public static Scanner scanner;
    public static int piece;
    //public static String[] arr;
    //public static List<String> itemsSchool;
    public static void main(String[] args) throws IOException, InterruptedException {
        numThreads = Runtime.getRuntime().availableProcessors();


        String bes = "";
        String rez = "";

        //100MB za test


        File _1000b = new File("../DatotekeZaTestiranje/1000b-file.txt");
        File _10000b = new File("../DatotekeZaTestiranje/10000b-file.txt");
        File _100000b = new File("../DatotekeZaTestiranje/100000b-file.txt");
        File _1000000b = new File("../DatotekeZaTestiranje/1000000b-file.txt");
        File _10000000b = new File("../DatotekeZaTestiranje/10000000b-file.txt");
        File _20mb = new File("../DatotekeZaTestiranje/20mb.txt");

        File file100 = new File("../DatotekeZaTestiranje/sample3.txt");
        scanner = new Scanner(file100);

        //rezultat se shrani v
        filewriter = new FileWriter("../DatotekeZaTestiranje/rezultat.txt");

        while(scanner.hasNextLine()){
            //if(scanner.nextLine().length()!=0) {
                bes = bes + scanner.nextLine();
            //}
        }
        scanner.close();
        //System.out.println(bes);
        nGram = bes.split(" ");

        //parallel
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
                    //System.out.println("del je velik "+piece+" in zadnji "+lastpiece);
                    //System.out.println("startan worker " + i+" delam od "+i*piece +" do "+((i * piece) + piece));
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
            for (int i = 0; i < numThreads ; i++) {
                filewriter.write(delavci[i].text);
            }

            double koncnicasthree = System.currentTimeMillis();
            System.out.println("Paralelni čas porabljen za besedilo iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasthree-zacetnicasthree)+" milisekund");
            String rez1 = "Paralelni čas porabljen za besedilo iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasthree-zacetnicasthree)+" milisekund";
            filewriter.write(rez1);
            filewriter.close();


        //sekvencni
        }else{
        double zacetnicasseq = System.currentTimeMillis();
        if (seq){
        //nGram = besedilo.split(" ");
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
        System.out.println("Sekvenčni čas porabljen za besedilo iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasseq-zacetnicasseq)+" milisekund");
        String rez1 = "Sekvenčni čas porabljen za besedilo iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicasseq-zacetnicasseq)+" milisekund";
         filewriter.write(rez1);
         filewriter.close();

    }
    else{
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
            System.out.println("Paralelni čas porabljen za besedilo iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicaspar-zacetnicaspar)+" milisekund");
            String rez1 = "Paralelni čas porabljen za besedilo iz "+nGram.length+" besed, ngrami dolzine "+ n +": "+(koncnicaspar-zacetnicaspar)+" milisekund";
            filewriter.write(rez1);
            filewriter.close();



        }}


    }
    //vrne array od indexa k besedila za n besed naprej
    public static String[] splitVSubstring(String [] bes, int k) {
        String[] zacasni = new String[n];
        if (bes.length >= k + n) {

            for (int i = 0; i < n; i++) {
                zacasni[i] = bes[k];
                k = k + 1;
            }

        }
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
