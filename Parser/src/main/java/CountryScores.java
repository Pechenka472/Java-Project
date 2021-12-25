public class CountryScores {
    private final String country;
    private final String happinessScore;
    private final String standardError;
    private final String economy;
    private final String family;
    private final String health;
    private final String freedom;
    private final String trust;
    private final String generosity;
    private final String dystopiaResidual;

    public CountryScores(String country,
                         String happinessScore,
                         String standardError,
                         String economy,
                         String family,
                         String health,
                         String freedom,
                         String trust,
                         String generosity,
                         String dystopiaResidual) {
        this.country = country;
        this.happinessScore = happinessScore;
        this.standardError = standardError;
        this.economy = economy;
        this.family = family;
        this.health = health;
        this.freedom = freedom;
        this.trust = trust;
        this.generosity = generosity;
        this.dystopiaResidual = dystopiaResidual;
    }

    public String getCountry() {
        return country;
    }

    public String getHappinessScore() {
        return happinessScore;
    }

    public String getStandardError() {
        return standardError;
    }

    public String getEconomy() {
        return economy;
    }

    public String getFamily() {
        return family;
    }

    public String getHealth() {
        return health;
    }

    public String getFreedom() {
        return freedom;
    }

    public String getTrust() {
        return trust;
    }

    public String getGenerosity() {
        return generosity;
    }

    public String getDystopiaResidual() {
        return dystopiaResidual;
    }
}