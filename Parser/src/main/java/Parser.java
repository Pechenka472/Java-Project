import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.swing.JFrame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser
{
    public static void main(String[] args) throws SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Распарсить CSV в SQLite? Да или Нет?\n");
        System.out.print("Ответ: ");
        var line = in.next();

        CountryStatistic countryStatistic;

        if (line.equals("Да")) {
            System.out.println("Подождите...");
            countryStatistic = new CountryStatistic(Paths.get("Показатель счастья по странам 2015.csv")
                    .toAbsolutePath());
            SaveToDatabase(countryStatistic);
        }

        System.out.println("\nTask 1:\n");
        Task1();
        System.out.println("\nTask 2:");
        Task2("economy", "Latin America and Caribbean");
        Task2("economy", "Eastern Asia");
        System.out.println("\nTask 3:");
        Task3("happinessScore", "Western Europe");
        Task3("happinessScore", "North America");
    }

    public static void Task3(String column, String region) throws SQLException {
        String query = String.format("""
                SELECT region.country, region.region, ABS(scores.%s - avg) as abs
                FROM region
                LEFT JOIN scores ON scores.country = region.country
                LEFT JOIN (SELECT AVG(scores.%s) as avg, region.region as reg
                    FROM scores
                    LEFT JOIN region ON region.country = scores.country
                    WHERE reg = '%s')
                WHERE region = '%s'
                ORDER BY abs
                LIMIT 1""", column, column, region, region);

        var set = MakeExecuteQuery(query);
        assert set != null;
        set.next();
        System.out.println("\nОтвет: " + set.getString(1) + "\n");
        PrintData(set);
    }

    public static void Task2(String column, String region) throws SQLException {
        String query = String.format("""
                SELECT scores.country, scores.%s, region.region
                FROM scores
                LEFT JOIN region ON region.country = scores.country
                WHERE region = '%s'
                ORDER BY %s DESC
                LIMIT 1
                """, column , region , column);

        var set = MakeExecuteQuery(query);
        assert set != null;
        set.next();
        System.out.println("\nОтвет: " + set.getString(1) + "\n");
        PrintData(set);
    }

    public static void Task1() throws SQLException {
        var set = MakeExecuteQuery("SELECT country, economy FROM scores");
        JFreeChart chart = ChartFactory.createBarChart(
                "График по показателю экономики объеденый по странам",
                null,
                "Экономика",
                GetDataSet(set));

        JFrame frame = new JFrame("Economy");
        frame.getContentPane().add(new ChartPanel(chart));
        frame.setSize(600,400);
        frame.setVisible(true);
        PrintData(set);
    }

    private static void PrintData(CachedRowSet set) throws SQLException {
        System.out.println("Данные:");
        var columnNames = GetColumnNames(set);
        set.beforeFirst();
        for (var i = 1; set.next(); i++) {
            var sb = new StringBuilder();
            var str1 = i + ") ";
            sb.append(str1);
            for (var j = 1; j <= columnNames.size(); j++) {
                var str2 = columnNames.get(j - 1) + " = " + set.getString(j) + ", ";
                sb.append(str2);
            }
            sb.deleteCharAt(sb.length() - 1);
            System.out.println(sb);
        }
    }

    private static ArrayList<String> GetColumnNames(CachedRowSet set) throws SQLException {
        var metaData = set.getMetaData();
        var result = new ArrayList<String>();
        set.beforeFirst();
        set.next();
        for (var i = 1; i <= metaData.getColumnCount(); i++) {
            result.add(metaData.getColumnName(i));
        }
        return result;
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

    public static CachedRowSet MakeExecuteQuery(String query) {
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

    public static void SaveToDatabase(CountryStatistic countryStatistic) {
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

            for (var i: countryStatistic.getRegions()) {
                regionStatement.executeUpdate(String.format("INSERT INTO region values ('%s', '%s')",
                        i.getCountry(),
                        i.getRegion()));
            }

            for (var i: countryStatistic.getRanks()) {
                rankStatement.executeUpdate(String.format("INSERT INTO rank values ('%s', '%s')",
                        i.getCountry(),
                        i.getHappinessRank()));
            }

            for (var i: countryStatistic.getScores()) {
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
