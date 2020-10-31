package com.epam.auction.main;

import com.epam.auction.logic.Auction;
import com.epam.auction.logic.Bidder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {

    public static void main(String[] args) throws InterruptedException {

        Auction auction = Auction.getInstance();
        List<Bidder> bidders = null;
        int size = bidders.size();
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        bidders.forEach(executorService::submit);
        auction.startAuction();
        executorService.shutdown();
    }
}
