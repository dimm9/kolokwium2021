public class CountryNotFoundException extends Exception{

    public CountryNotFoundException(String countryName) {
        super("Country not found: " + countryName);
    }

    public String getMessage(String countryName) {
        return "Country not found " +  countryName;
    }
}
