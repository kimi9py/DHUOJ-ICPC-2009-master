/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.dhu.acm.oj.judge.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sunkaiqi
 * 这个类为自己添加的用于实现自动配置功能
 * 主要进行的是文件操作，对judgeserver.ini进行读取操作
 * 时间:2016/4/8
 * 
 */
public class FileWorker {
    public static BufferedReader bufread;
    private  static final String judgeserverini = "judgeserver.ini";
    private  static File file = new File(judgeserverini); 
    
    /** *//** 
     * 读取文本文件 . 
     * 
     */ 
    public static String readTxtFile() { 
        String read="-1"; 
        FileReader fileread; 
        try { 
            fileread = new FileReader(file); 
            bufread = new BufferedReader(fileread); 
            try { 
                read = bufread.readLine();
                if(read==null)read = "-1";
                fileread.close();
            } catch (IOException e) { 
                // TODO Auto-generated catch block 
                e.printStackTrace(); 
            } 
        } catch (FileNotFoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        }
        return read; 
    } 
}
