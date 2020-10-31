package com.epam.auction.logic;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Auction {

    private static final int BID_TIME = 3;
    private static final ReentrantLock INSTANCE_LOCK = new ReentrantLock();
    private static final ReentrantLock BID_LOCK = new ReentrantLock();
    private static final ReentrantLock LOTS_LOCK = new ReentrantLock();
    private static Auction instance = null;
    private final Queue<Lot> lots = new LinkedList<>(
            Arrays.asList(
                    new Lot("Lot number 1"),
                    new Lot("Lot number 2"),
                    new Lot("Lot number 3")
            )
    );
    private int currentBid;
    private Bidder currentBidder;

    private Auction() {
    }

    public static Auction getInstance() {
        if (instance == null) {
            INSTANCE_LOCK.lock();
            try {
                Auction local = new Auction();
                if (instance == null) {
                    instance = local;
                }
            } finally {
                INSTANCE_LOCK.unlock();
            }
        }
        return instance;
    }

    public Bidder getCurrentBidder() {
        return currentBidder;
    }

    public Lot getCurrentLot() {
        LOTS_LOCK.lock();
        try {
            return lots.peek();
        } finally {
            LOTS_LOCK.unlock();
        }
    }

    public void increaseCurrentBid(Bidder bidder) {
        BID_LOCK.lock();
        try {
            currentBid++;
            currentBidder = bidder;
        } finally {
            BID_LOCK.lock();
        }
    }

    public void startAuction() throws InterruptedException {
        while (getCurrentLot() != null) {
            Bidder lastBidder = currentBidder;
            TimeUnit.SECONDS.sleep(BID_TIME);
            if (currentBidder == null || currentBidder.equals(lastBidder)) {
                BID_LOCK.lock();
                try {
                    removeCurrentLot();
                    currentBid = 0;
                } finally {
                    BID_LOCK.unlock();
                }
            }
        }
    }

    private void removeCurrentLot() {
        LOTS_LOCK.lock();
        try {
            Lot removed = lots.poll();
            if (currentBidder != null) {
                // sold to currentBidder
                currentBidder = null;
            }
        } finally {
            LOTS_LOCK.unlock();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "lots=" + lots +
                ", currentBid=" + currentBid +
                ", currentBidder=" + currentBidder +
                '}';
    }
}
