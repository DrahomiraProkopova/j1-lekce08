package cz.czechitas.lekce8.svatky;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Třída s informacemi o tom,kdo má kdy svátek.
 */
public class Svatky {
    private static final DateTimeFormatter MONTH_PARSER = DateTimeFormatter.ofPattern("d.M.");

    public Stream<Svatek> seznamSvatku() {
        try {
            Path path = Paths.get(Svatky.class.getResource("svatky.txt").toURI());
            return Files.lines(path).map(Svatky::parseLine);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Vrátí seznam všech svátků v daném měsíci.
     *
     * @param mesic Měsíc, pro který se mají svátky vypsat.
     * @return Stream svátků.
     */
    public Stream<Svatek> svatkyVMesici(Month mesic) {
        return seznamSvatku()
                .filter(svatek -> svatek.getDen().getMonth() == mesic);
    }

    /**
     * Vrátí den, kdy má dotyčné jméno svátek.
     *
     * @param jmeno
     * @return
     */
    public Stream<MonthDay> datumSvatku(String jmeno) {
        return seznamSvatku()
                .filter(svatek -> svatek.getJmeno().equals(jmeno))
                .map(Svatek::getDen);
    }

    /**
     * Vrátí všechna jména mužů.
     *
     * @return Stream jmen.
     */
    public Stream<String> muzi() {
        return seznamSvatku()
                .filter(svatek -> svatek.getGender() == Gender.MUZ)
                .map(Svatek::getJmeno);
    }

    /**
     * Vrátí všechna jména žen.
     *
     * @return Stream jmen.
     */
    public Stream<String> zeny() {
        return seznamSvatku()
                .filter(svatek -> svatek.getGender() == Gender.ZENA)
                .map(Svatek::getJmeno);
    }

    /**
     * Vrátí jména, která mají v daný den svátek.
     *
     * @return Stream jmen.
     */
    public Stream<String> den(MonthDay den) {
        return seznamSvatku()
                .filter(svatek -> svatek.getDen()
                        .equals(den))
                .map(Svatek::getJmeno);
    }

    /**
     * Vrátí ženská jména, která mají svátek v daném měsíci.
     *
     * @param mesic Vybraný měsíc.
     * @return Stream jmen.
     */
    public Stream<String> zenskaJmenaVMesici(Month mesic) {
        return seznamSvatku()
                .filter(svatek -> svatek.getDen().getMonth()
                        .equals(mesic) && svatek.getGender() == Gender.ZENA)
                .map(Svatek::getJmeno);
    }


    /**
     * Vrátí počet mužů, kteří mají svátek 1. den v měsíci.
     *
     * @return Počet mužských jmen.
     */
    public int pocetMuzuSvatekPrvniho() {
        return seznamSvatku().filter(svatek -> svatek.getGender() == Gender.MUZ)
                .filter(svatek -> svatek.getDen().getDayOfMonth() == 1)
                .map(Svatek::getJmeno).collect(Collectors.toList()).size();

    }

    /**
     * Vypíše do konzole seznam jmen, která mají svátek v listopadu.
     */
    public void vypsatJmenaListopad() {
        System.out.println(seznamSvatku()
                .filter(svatek -> svatek.getDen().getMonth() == Month.NOVEMBER)
                .map(Svatek::getJmeno)
                .collect(Collectors.toList()));
        ;
    }

    /**
     * Vypíše počet unikátních jmen v kalendáři.
     */
    public int pocetUnikatnichJmen() {
        return seznamSvatku()
                .map(Svatek::getJmeno).distinct().collect(Collectors.toList()).size();
    }

    /**
     * Vrátí seznam jmen, která mají svátek v červnu – přeskočí prvních 10 jmen.
     *
     * @see Stream#skip(long)
     */
    public Stream<String> cervenJmenaOdDesatehoJmena() {
        return seznamSvatku()
                .filter(svatek -> svatek.getDen().getMonth() == Month.JUNE)
                .map(Svatek::getJmeno).collect(Collectors.toList()).
                subList(10, 42).stream();
    }

    /**
     * Vrátí seznam jmen, která mají svátek od 24. 12. včetně do konce roku.
     *
     * @see Stream#dropWhile(java.util.function.Predicate)
     */
    public Stream<String> jmenaOdVanoc() {
        return seznamSvatku()
                .filter(svatek -> svatek.getDen().getMonth() == Month.DECEMBER
                        && svatek.getDen().getDayOfMonth() >= 24).map(Svatek::getJmeno)
                .collect(Collectors.toList()).stream();


    }

    private static Svatek parseLine(String line) {
        String[] parts = line.split("\\s");
        assert parts.length == 3;
        return new Svatek(
                MonthDay.parse(parts[0], MONTH_PARSER),
                parts[1],
                Gender.valueOf(parts[2].toUpperCase(Locale.ROOT))
        );
    }
}
