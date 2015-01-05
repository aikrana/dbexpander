/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.*;

/**
 *
 * @author dani
 */
public class Pb {
    public static void main(String[] args) throws IOException{
        String[] cmd = {
                        "/bin/sh",
                        "-c",
                        "cat /home/dani/archivo.txt | grep capullo"
                    };
        
        Process proc=Runtime.getRuntime().exec(cmd);
        BufferedReader read=new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while(read.ready())
        {
        System.out.println(read.readLine());
        }
    }
    
}
