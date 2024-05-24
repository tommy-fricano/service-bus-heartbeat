package org.example.Heartbeats.portal;


import okhttp3.*;

import java.util.Scanner;

public class Portal {

    public static void main(String[] args) {

        ProcessMessages processMessages = new ProcessMessages();

//        Thread thread = new Thread(processMessages);
//        thread.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter an option: \n1.Read heartbeats\n2.Send command\n3.Quit");
            int input = scanner.nextInt();
            if (input == 1) {

            } else if (input == 2) {
                System.out.println("Enter the storeId: ");
                String storeId = scanner.next();
                System.out.println("Enter the playerNum: ");
                String player = scanner.next();
                System.out.println("Enter a command: ");
                String command = scanner.next();

                String playerCode = "us-" + storeId + "-" + player;
                String jsonMessage = "{\"command\": \"" + command + "\", \"playerCode\": \"" + playerCode + "\", \"timestamp\": \"" + System.currentTimeMillis() + "\"}";


                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                RequestBody body = RequestBody.create(jsonMessage, JSON);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://localhost:8080/command")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    System.out.println("Response Code: " + response.code());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (input == 3) {
                System.exit(0);
            }
        }
    }
}
