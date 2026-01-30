package com.daw.dawCasinoBack.domain.models.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public int calculateScore() {
        int score = 0;
        int aceCount = 0;

        for (Card card : cards) {
            score += card.getValue();
            if (card.getRank() == Rank.ACE) {
                aceCount++;
            }
        }

        for (int i = 0; i < aceCount; i++) {
            if (score + 10 <= 21) {
                score += 10;
            }
        }
        return score;
    }

    public int getScore() {
        return calculateScore();
    }

    public boolean isBusted() {
        return calculateScore() > 21;
    }

    public boolean isBlackJack() {
        return calculateScore() == 21 && cards.size() == 2;
    }

    @Override
    public String toString() {
        return cards.toString() + " (Score: " + calculateScore() + ")";
    }
}