import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryStatistic {
    private final List<CountryRegion> Regions;
    private final List<CountryRank> Ranks;
    private final List<CountryScores> Scores;

    public CountryStatistic(Path path) {
        var regions = new ArrayList<CountryRegion>();
        var ranks = new ArrayList<CountryRank>();
        var scores = new ArrayList<CountryScores>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            br.readLine();
            while (br.ready()) {
                var row = splitRow(br.readLine());
                regions.add(new CountryRegion(row[0], row[1]));
                ranks.add(new CountryRank(row[0], row[2]));
                scores.add(new CountryScores(row[0], row[3], row[4], row[5], row[6], row[7], row[8], row[9], row[10], row[11]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Regions = regions;
        Ranks = ranks;
        Scores = scores;
    }

    private String[] splitRow(String row) {
        var result = row.split(",");
        return Arrays.copyOfRange(result, 0, 12);
    }

    public List<CountryRegion> getRegions() { return Regions; }

    public List<CountryRank> getRanks() {
        return Ranks;
    }

    public List<CountryScores> getScores() {
        return Scores;
    }
}
