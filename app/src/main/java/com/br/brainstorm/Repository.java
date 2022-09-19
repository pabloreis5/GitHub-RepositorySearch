package com.br.brainstorm;

public class Repository {
    private String name, owner, language, starsCount;
    private final String nameStart = "Name: ";
    private final String ownerStart = "Owner: ";
    private final String languageStart = "Language: ";
    private final String starStart = "Stars Number: ";

    public Repository(String name, String owner, String language, String starsCount) {
        this.name = name;
        this.owner = owner;
        this.language = language;
        this.starsCount = starsCount;
    }

    public String getName() {
        return this.nameStart + name;
    }

    public String getOwner() {
        return this.ownerStart + owner;
    }

    public String getLanguage() {
        return this.languageStart + language;
    }

    public String getStarsCount() {
        return this.starStart + starsCount;
    }
}
