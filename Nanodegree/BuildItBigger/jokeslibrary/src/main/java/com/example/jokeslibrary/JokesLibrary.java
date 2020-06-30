package com.example.jokeslibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JokesLibrary {
    private List<String> jokes;
    private Random random;

    private void populateJokes() {
        jokes.add("What do you call a fish with no eyes? Fsh.");
    }

    public JokesLibrary() {
        jokes = new ArrayList<>();
        populateJokes();
        random = new Random();
    }

    public String getJokes() {
        return jokes.get(random.nextInt(jokes.size()));
    }
}