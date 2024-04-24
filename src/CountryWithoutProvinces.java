import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CountryWithoutProvinces extends Country {
    public CountryWithoutProvinces(String name) {
        super(name);
    }

    @Override
    public void addDailyStatistic(LocalDate date, int diseases, int deaths){
        statisticsList.add(new Statistics(date, diseases, deaths));
    }
    public int getDeaths(LocalDate date) {
        Statistics s = statisticsList.stream()
                .filter(obj -> obj.date.isEqual(date))
                .findFirst()
                .orElse(null);
        return s != null ? s.deaths() : 0;
    }

    @Override
    public int getConfirmedCases(LocalDate date) {
        Statistics s = statisticsList.stream()
                .filter(obj -> obj.date.isEqual(date))
                .findFirst()
                .orElse(null);
        return s != null ? s.diseases() : 0;
    }
    public record Statistics(LocalDate date, int deaths, int diseases){

        @Override
        public String toString() {
            return date.format(DateTimeFormatter.ofPattern("d.MM.yy")) + "," + deaths + diseases;
        }
    }
}
