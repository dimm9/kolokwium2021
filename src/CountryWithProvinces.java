import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CountryWithProvinces extends Country {
    private List<Country> provinces = new ArrayList<>();

    public CountryWithProvinces(String name, List<Country> provinces) {
        super(name);
        this.provinces = provinces;
    }
    public void addDailyStatistic(LocalDate date, int diseases, int deaths){
        statisticsList.add(new CountryWithoutProvinces.Statistics(date, diseases, deaths));
    }

    @Override
    public int getConfirmedCases(LocalDate date) {
        return provinces.stream().mapToInt(p -> p.getConfirmedCases(date)).sum();
    }
    @Override
    public int getDeaths(LocalDate date) {
        return provinces.stream().mapToInt(p -> p.getDeaths(date)).sum();
    }
}

