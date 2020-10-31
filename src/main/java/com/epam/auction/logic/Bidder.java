package com.epam.auction.logic;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Bidder implements Runnable {

    private static final int REACTION_TIME = 1;
    private long id;
    private Lot currentLot;
    private boolean isContesting;
    private Auction auction;

    public Bidder() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Lot getCurrentLot() {
        return currentLot;
    }

    public void setCurrentLot(Lot currentLot) {
        this.currentLot = currentLot;
    }

    public boolean isContesting() {
        return isContesting;
    }

    public void setContesting(boolean contesting) {
        isContesting = contesting;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    @Override
    public void run() {

        Lot nextLot = auction.getCurrentLot();
        while (nextLot != null) {
            if (!nextLot.equals(currentLot)) {
                currentLot = nextLot;
                try {
                    contestCurrentLot();
                } catch (InterruptedException e) {
                    // log
                }
            }
            try {
                TimeUnit.SECONDS.sleep(REACTION_TIME);
            } catch (InterruptedException e) {
                // log
            }
            nextLot = auction.getCurrentLot();
        }
    }

    private void contestCurrentLot() throws InterruptedException {
        Random random = new Random();
        isContesting = random.nextBoolean();
        while (isContesting) {
            Bidder currentBidder = auction.getCurrentBidder();
            if (!equals(currentBidder)) {
                auction.increaseCurrentBid(this);
            }
            TimeUnit.SECONDS.sleep(REACTION_TIME);
            isContesting = random.nextBoolean();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bidder bidder = (Bidder) o;
        return id == bidder.id &&
                isContesting == bidder.isContesting &&
                Objects.equals(currentLot, bidder.currentLot) &&
                Objects.equals(auction, bidder.auction);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (currentLot != null ? currentLot.hashCode() : 0);
        result = 31 * result + (isContesting ? 1 : 0);
        result = 31 * result + (auction != null ? auction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", currentLot=" + currentLot +
                ", isContesting=" + isContesting +
                ", auction=" + auction +
                '}';
    }
}
