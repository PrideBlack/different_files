package PullWords;

import java.io.*;
import java.net.URL;
import java.util.*;

public class PullWords {

    public static void main(String[] args) {
        String url = "http://madbrains.github.io/java_course_test";
        String path = "/home/duvan/Documents/input";
        String result_path = "/home/duvan/Documents/output";
        Integer cnt = 0;
        try {
            downloadUsingStream(url, path);
            Map words = countWords(path);
            List<Map.Entry<String, Integer>> result = new ArrayList<>(words.entrySet());
            result.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    if(o1.getValue() == o2.getValue()) return 0 - o1.getKey().compareTo(o2.getKey());
                    else return 0 - o1.getValue().compareTo(o2.getValue());
                }
            });
            FileWriter out = new FileWriter(result_path);
            for(Map.Entry<String, Integer> i : result) {
                cnt += i.getValue();
                System.out.println(i.getKey() + " : " + i.getValue());
            }
            out.close();
            System.out.println("Count words : " + cnt);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    private static String ReadFile(String path) {
        String res = new String();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            res = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static TreeMap<String, Integer> countWords(String path) {
        TreeMap<String, Integer> result = new TreeMap<String, Integer>();
        try {
            String input = ReadFile(path);
            input += "$";
            String word = "";
            for(int j = 0; j < input.length(); j++) {
                char i = input.charAt(j);
                if(Character.isAlphabetic(i) || Character.isDigit(i)) {
                    word += i;
                } else if (i == '-') {
                    if(!word.isEmpty()) word += (char)i;
                } else {
                    if(!word.isEmpty()) {
                        word = word.toLowerCase();
                        if(result.containsKey(word)) {
                            Integer cnt = result.get(word);
                            result.remove(word);
                            result.put(word, cnt + 1);
                        } else {
                            result.put(word,1);
                        }
                        word = "";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
