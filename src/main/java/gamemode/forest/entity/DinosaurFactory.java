package gamemode.forest.entity;

import java.util.Random;

public class DinosaurFactory {

    public static Dinosaur createHerbivore(String type, double x, double y) {

        switch (type) {
            case "LongNeck":
                return new HerbivoreDinosaur(
                        "Long Neck",
                        150, 10, 40, 1,
                        x, y,
                        Dinosaur.Rarity.COMMON,
                        "/images/dinosaur/longneck.png",400,400,40
                );

            case "Triceratops":
                return new HerbivoreDinosaur(
                        "Triceratops",
                        250, 20, 80, 3,
                        x, y,
                        Dinosaur.Rarity.UNCOMMON,
                        "/images/dinosaur/triceratops.png",200,200,45
                );
        }

        return null;
    }

    public static Dinosaur createCarnivore(String type, double x, double y) {

        switch (type) {
            case "Raptor":
                return new CarnivoreDinosaur(
                        "Raptor",
                        120, 30, 100, 2,
                        x, y,
                        Dinosaur.Rarity.COMMON,
                        "/images/dinosaur/raptor.gif"
                        ,300,300,3.5,75
                );

            case "TRex":
                return new CarnivoreDinosaur(
                        "T-Rex",
                        400, 80, 300, 5,
                        x, y,
                        Dinosaur.Rarity.RARE,
                        "/images/dinosaur/trex.gif",300,300,2.2,90
                );
        }

        return null;
    }

    public static Dinosaur createMega(double x, double y) {
        return new MegaDinosaur(
                "Ancient Colossus",
                1500, 150, 1000, 10,
                x, y,
                Dinosaur.Rarity.RARE,
                "/images/dinosaur/mega.png",500,500,150
        );
    }
}