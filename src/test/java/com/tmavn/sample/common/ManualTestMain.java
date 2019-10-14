package com.tmavn.sample.common;

public class ManualTestMain {

    public static void main(String[] args) {
        System.out.println("Start manual test main class");
        
        System.out.println("Start- get instance of RestClient");
        RestClient rest = RestClient.getInstance();
        System.out.println("End- get instance of RestClient");
        System.out.println("Start- get instance of AsyncRestClient");
        AsyncRestClient asyncrest = AsyncRestClient.getInstance();
        System.out.println("End- get instance of AsyncRestClient");
    }

}
