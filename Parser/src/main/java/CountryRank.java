public class CountryRank {
    private final String country;
    private final String happinessRank;

    public CountryRank(String country, String happinessRank) {
        this.country = country;
        this.happinessRank = happinessRank;
    }

    public String getCountry() {
        return country;
    }

    public String getHappinessRank() {
        return happinessRank;
    }
}
