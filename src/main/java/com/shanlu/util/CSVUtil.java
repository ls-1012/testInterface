package com.shanlu.util;


import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    public static List<JsonObject> convertCscToJson(FileReader fileReader) {
        try {

            CSVReader csvReader = new CSVReader(fileReader);
            String[] content;
            String[] header = csvReader.readNext();
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<JsonObject> contentList = new ArrayList<>();

            while ((content = csvReader.readNext()) != null) {
                int length = content.length;
                for (int i = 0; i < length; i++) {
//                    header[i]=content[i];
//                    System.out.print(header[i]+":"+content[i]+", ");
                    stringBuilder.append("\"" + header[i] + "\":\"" + content[i] + "\",");
                }
                System.out.println("length:" + stringBuilder.length());
                String newContent = "{" + stringBuilder.substring(0, stringBuilder.length() - 1) + "}";
                System.out.println(newContent);
                JsonObject jsonObject1 = TestUtil.formatParam(newContent);
                contentList.add(jsonObject1);

            }
            System.out.println(contentList);
            return contentList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
