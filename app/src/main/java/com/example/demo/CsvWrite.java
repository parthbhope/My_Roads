package com.example.demo;

import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class CsvWrite {
   // Calendar name = Calendar.getInstance();
//int
    Date date = new Date();

    public void writeDataLineByLine(String filePath, double lat,double lng,double zacc)
    {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        String timestamp = hour+":"+minute+":"+second;

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = { "Lat", "Lng", "Zacc","Timestamp" };
            writer.writeNext(header);

            // add data to csv
           // String[] data1 = { "Aman", "10", "620" };
            String[] mydata = {lat+"",lng+"",zacc+""+timestamp} ;

            writer.writeNext(mydata);
           // String[] data2 = { "Suraj", "10", "630" };
           // writer.writeNext(data2);

            // closing writer connection
           // writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String createfile()
    {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/project");
        Boolean dirsMade = dir.mkdir();
        //System.out.println(dirsMade);
        Log.v("Accel", dirsMade.toString());

        File file = new File(dir, "journey "+date+".csv");
        try {
            FileOutputStream f = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //File file = new File();
        return file.getAbsolutePath();
    }
}
