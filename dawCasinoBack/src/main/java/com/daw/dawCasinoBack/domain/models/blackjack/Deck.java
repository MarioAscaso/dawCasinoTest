package com.daw.dawCasinoBack.domain.models.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    // Quitamos 'final' para que Jackson pueda inyectar las cartas guardadas
    private List<Card> cards;

    // 1. Constructor VACÍO (Solo para Jackson)
    public Deck() {
        this.cards = new ArrayList<>();
    }

    // 2. Constructor LÓGICO (Para crear una baraja nueva llena)
    public Deck(boolean createFullDeck) {
        this.cards = new ArrayList<>();
        if (createFullDeck) {
            initializeDeck();
        }
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new RuntimeException("No more cards in the deck!");
        }
        return cards.remove(cards.size() - 1);
    }

    public int remainingCards() {
        return cards.size();
    }

    // --- GETTERS Y SETTERS ---

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}