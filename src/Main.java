import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class Main {
    public static void main(String[] args){
        String[] names = new String[]{"Australia", "Afghanistan", "Andorra", "Burma" , "Burundi"};
        try {
            Country.setFiles("deaths.csv", "confirmed_cases.csv");
            List<Country> countries = Country.fromCsv(names);
            LocalDate date1 = LocalDate.parse("4/8/20", DateTimeFormatter.ofPattern("M/d/yy"));
            LocalDate date2 = LocalDate.parse("7/17/20", DateTimeFormatter.ofPattern("M/d/yy"));
            Country.sortByDeaths(countries, date1, date2);
            for(Country c : countries){
                System.out.println(c.statisticsList.toString());
            }
            Country.saveToDataFile(Path.of("data.txt"), countries);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}