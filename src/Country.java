import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class Country {
    private final String name;
    List<CountryWithoutProvinces.Statistics> statisticsList = new ArrayList<>();
    private static String pathDeaths;
    private static String pathConfirmedCases;

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public static boolean setFiles(String pathDeaths, String pathConfirmedCases) throws FileNotFoundException {
        Country.pathConfirmedCases = pathConfirmedCases;
        Country.pathDeaths = pathDeaths;
        File file1 = new File(pathDeaths);
        File file2 = new File(pathDeaths);
        if(!file1.exists()){
            throw new FileNotFoundException(pathDeaths);
        }
        if(!file2.exists()){
            throw new FileNotFoundException(pathConfirmedCases);
        }
        return true;
    }
    public static Country fromCsv(String countryName) throws CountryNotFoundException {
        try (BufferedReader readerDeaths = new BufferedReader(new FileReader(Country.pathDeaths));
             BufferedReader readerCases = new BufferedReader(new FileReader(Country.pathConfirmedCases))) {
            String[] provinces = readerDeaths.readLine().split(";");
            String firstRow = readerCases.readLine();
            readerDeaths.readLine(); // Skip header row in deaths file

            CountryColumns columns = getCountryColumns(firstRow, countryName);
            int index = columns.firstColumnIndex;

            if (!provinces[index].equals("nan")) {
                List<Country> provincesList = new ArrayList<>();
                for (String province : provinces[index].split(",")) {
                    provincesList.add(new CountryWithoutProvinces(province));
                }
                CountryWithProvinces countryWith = new CountryWithProvinces(countryName, provincesList);
                String lineDeaths, lineCases;
                readerCases.readLine();
                while ((lineDeaths = readerDeaths.readLine()) != null && (lineCases = readerCases.readLine()) != null) {
                    String[] partsDeaths = lineDeaths.split(";");
                    String[] partsCases = lineCases.split(";");
                    LocalDate date = LocalDate.parse(partsDeaths[0], DateTimeFormatter.ofPattern("M/d/yy"));
                    int deaths = Integer.parseInt(partsDeaths[index]);
                    int diseases = Integer.parseInt(partsCases[index]);
                    countryWith.addDailyStatistic(date, diseases, deaths);
                }
                return countryWith;
            } else {
                CountryWithoutProvinces countryWithout = new CountryWithoutProvinces(countryName);
                String lineDeaths, lineCases;
                while ((lineDeaths = readerDeaths.readLine()) != null && (lineCases = readerCases.readLine()) != null) {
                    String[] partsDeaths = lineDeaths.split(";");
                    String[] partsCases = lineCases.split(";");
                    LocalDate date = LocalDate.parse(partsDeaths[0], DateTimeFormatter.ofPattern("M/d/yy"));
                    int deaths = Integer.parseInt(partsDeaths[index]);
                    int diseases = Integer.parseInt(partsCases[index]);
                    countryWithout.addDailyStatistic(date, diseases, deaths);
                }
                return countryWithout;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Country> fromCsv(String[] countryName) {
        List<Country> countries = new ArrayList<>();
        for (String name : countryName) {
            try {
                countries.add(Country.fromCsv(name));
            } catch (CountryNotFoundException e) {
                System.out.println(e.getMessage(name));
            }
        }
        return countries;
    }
    public abstract void addDailyStatistic(LocalDate date, int diseases, int deaths);
    public static void sortByDeaths(List<Country> countries, LocalDate date1, LocalDate date2){
        Comparator<Country> comparator = Comparator.comparingInt(c -> c.getDeaths(c.statisticsList.getFirst().date()));
        List<Country> sortedCountries = countries.stream()
                .filter(c -> c.statisticsList.getFirst().date().isAfter(date1) && c.statisticsList.getFirst().date().isBefore(date2) )
                .sorted(comparator.reversed())
                .toList();
        countries = sortedCountries;
    }
    public static void saveToDataFile(Path path, List<Country> countries){
        File file = new File(path.toString());
        try(BufferedWriter fw = new BufferedWriter(new FileWriter(file))){
            int i=0;
            for(Country c : countries){
                fw.write(c.statisticsList.get(i).date().format(DateTimeFormatter.ofPattern("d.MM.yy"))
                        + "\t" + c.getDeaths(c.statisticsList.get(i).date())
                        + "\t" + c.getDeaths(c.statisticsList.get(i).date()) + "\n");
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public abstract int getConfirmedCases(LocalDate date);
    public abstract int getDeaths(LocalDate date);
    private static CountryColumns getCountryColumns(String firstRow, String countryName) throws CountryNotFoundException {
        String[] parts = firstRow.split(";");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(countryName)) {
                return new CountryColumns(i, countOccurrences(parts, countryName));
            }
        }
        throw new CountryNotFoundException(countryName);
    }

    private static int countOccurrences(String[] arr, String target) {
        int count = 0;
        for (String s : arr) {
            if (s.equals(target)) {
                count++;
            }
        }
        return count;
    }
    private static class CountryColumns{
        public final int firstColumnIndex;
        public final int columnCount;

        public CountryColumns(int firstColumnIndex, int columnCount) {
            this.firstColumnIndex = firstColumnIndex;
            this.columnCount = columnCount;
        }
    }
}

