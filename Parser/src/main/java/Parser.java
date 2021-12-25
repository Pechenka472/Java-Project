import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.swing.JFrame;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Parser
{
    public static void main(String[] args) {
        var statistics = new CountryStat(Paths.get("Показатель счастья по странам 2015.csv")
                .toAbsolutePath());
        //SaveToDatabase(statistics);
        DrawDiagrams();
        System.out.println("Task 2:\n");
        Task2("economy", "Latin America and Caribbean");
        Task2("economy", "Eastern Asia");
        System.out.println("\nTask 3:\n");
        //Task3("happinessScore", "Southern Asia");
        //Task3("happinessScore", "North America");
    }

    public static void Task2(String column, String region) {
        System.out.println(String.format("Max %s for \"%s\": ", column, region)
                + GetMaxScore(column, region));
    }

    public static void Task3(String column, String region) {
        String query = "";
        try {
            var set = MakeExecuteQuery(query);
            assert set != null;

            while (set.next()){
                System.out.println(set.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String GetMaxScore(String column, String region) {
        String query = String.format("""
                SELECT
                    MAX(%s)
                FROM
                    (SELECT
                        region,
                        economy
                    FROM
                        region as reg,
                        scores as sc
                    WHERE
                        reg.country = sc.country)
                WHERE
                    region = '%s'
                """, column, region);
        String maxEconomyValue = null;
        try {
            var set = MakeExecuteQuery(query);
            assert set != null;
            set.next();
            maxEconomyValue = set.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxEconomyValue;
    }

    public static void DrawDiagrams() {
        JFreeChart chart = ChartFactory.createBarChart(
                "График по показателю экономики объеденый по странам",
                null,
                "Экономика",
                GetDataSet(MakeExecuteQuery("SELECT country, economy FROM scores")));

        JFrame frame = new JFrame("Economy");
        frame.getContentPane().add(new ChartPanel(chart));
        frame.setSize(600,400);
        frame.setVisible(true);
    }

    public static CategoryDataset GetDataSet(CachedRowSet set) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            while (set.next()) {
                dataset.addValue(set.getDouble(2), set.getString(1), "Регионы");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public static CachedRowSet MakeExecuteQuery(String query)
    {
        Connection connection = null;
        Statement statement = null;

        try {
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet set = factory.createCachedRowSet();

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
            statement = connection.createStatement();

            var resSet = statement.executeQuery(query);
            set.populate(resSet);

            return set;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert statement != null;
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void SaveToDatabase(CountryStat statistics) {
        Connection connection = null;
        Statement regionStatement = null;
        Statement rankStatement = null;
        Statement scoresStatement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
            regionStatement = connection.createStatement();
            rankStatement = connection.createStatement();
            scoresStatement = connection.createStatement();
            for (var i:statistics.getRegions()) {
                regionStatement.executeUpdate(String.format("INSERT INTO region values ('%s', '%s')",
                        i.getCountry(),
                        i.getRegion()));
            }

            for (var i:statistics.getRanks()) {
                rankStatement.executeUpdate(String.format("INSERT INTO rank values ('%s', '%s')",
                        i.getCountry(),
                        i.getHappinessRank()));
            }

            for (var i:statistics.getScores()) {
                scoresStatement.executeUpdate(String.format("INSERT INTO scores values ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                        i.getCountry(),
                        i.getHappinessScore(),
                        i.getStandardError(),
                        i.getEconomy(),
                        i.getFamily(),
                        i.getHealth(),
                        i.getFreedom(),
                        i.getTrust(),
                        i.getGenerosity(),
                        i.getDystopiaResidual()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert regionStatement != null;
                regionStatement.close();
                assert rankStatement != null;
                rankStatement.close();
                assert scoresStatement != null;
                scoresStatement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
