package com.tag.application;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class PemToOneLine {
    public static void main(String[] args) {
        String filePath = "/Users/anyoungyoon/Downloads/duddbs774@gmail.com_2024-04-03T08_05_52.595Z.pem"; // PEM 파일의 경로
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            String oneLine = content.replace("\n", "\\n").replace("\r", ""); // Windows 환경일 경우 \r도 제거
            System.out.println(oneLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
