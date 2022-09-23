package cybersoft.javabackend.java18.gamedoanso.repository;

import cybersoft.javabackend.java18.gamedoanso.jdbc.MySqlConnection;
import cybersoft.javabackend.java18.gamedoanso.model.GameSession;
import cybersoft.javabackend.java18.gamedoanso.model.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.sql.Connection;

public class GameSessionRepository extends AbstractRepository<GameSession> {
    public void save(GameSession gameSession) {
        final String query = """
                insert into game_session
                (id, target, start_time, is_completed, is_active, username)
                values(?, ?, ?, ?, ?, ?)
                """;
        executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, gameSession.getId());
            statement.setInt(2, gameSession.getTargetNumber());
            statement.setTimestamp(3, Timestamp.from(
                    gameSession.getStartTime().toInstant(ZoneOffset.of("+07:00")))
            );
            statement.setInt(4, gameSession.getIsCompleted() ? 1 : 0);
            statement.setInt(5, gameSession.isActive() ? 1 : 0);
            statement.setString(6, gameSession.getUsername());

            return statement.executeUpdate();
        });
    }

    public List<GameSession> findByUsername(String username) {
        final String query = """
                    select id,  target, start_time, end_time, is_completed, is_active, username
                    from game_session
                    where username = ?
                """;
        return executeQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            List<GameSession> sessions = new ArrayList<>();

            while (resultSet.next()) {
                sessions.add(new GameSession()
                        .id(resultSet.getString("id"))
                        .targetNumber(resultSet.getInt("target"))
                        .startTime(getDateTimeFromResultSet("start_time", resultSet))
                        .endTime(getDateTimeFromResultSet("end_time", resultSet))
                        .isCompleted(resultSet.getInt("is_completed") == 1)
                        .isActive(resultSet.getInt("is_active") == 1)
                        .username(resultSet.getString("username")));
            }

            return sessions;
        });
    }

    public GameSession findById(String id) {
        final String query = """
                    select id,  target, start_time, end_time, is_completed, is_active, username
                    from game_session
                    where id = ?
                """;
        return executeQuerySingle(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return null;

            return new GameSession()
                    .id(resultSet.getString("id"))
                    .targetNumber(resultSet.getInt("target"))
                    .startTime(getDateTimeFromResultSet("start_time", resultSet))
                    .endTime(getDateTimeFromResultSet("end_time", resultSet))
                    .isCompleted(resultSet.getInt("is_completed") == 1)
                    .isActive(resultSet.getInt("is_active") == 1)
                    .username(resultSet.getString("username"));
        });
    }

    public void deactivateAllGames(String username) {
        final String query = """
                update game_session
                set is_active = 0
                where username = ?
                """;
        executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            return statement.executeUpdate();
        });
    }

    public void completeGame(String sessionId) {
        final String query = """
                update game_session
                set is_completed = 1
                where id = ?
                """;
        executeUpdate(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, sessionId);
            return statement.executeUpdate();
        });
    }

    private LocalDateTime getDateTimeFromResultSet(String columnName, ResultSet resultSet) {
        Timestamp time;

        try {
            time = resultSet.getTimestamp(columnName);
        } catch (SQLException e) {
            return null;
        }

        if (time == null)
            return null;

        return time.toLocalDateTime();
    }

    public LinkedHashMap<String, Integer> listAllPlayersRanking() {
        try {
            String query = """
                    SELECT username, SUM(is_completed)
                    FROM gamedoanso.game_session 
                    GROUP BY username 
                    ORDER BY SUM(is_completed) 
                    DESC;
                    """;
            Connection connection = MySqlConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            LinkedHashMap<String, Integer> lhmap
                    = new LinkedHashMap<String, Integer>();

            while (resultSet.next()) {
                lhmap.put(resultSet.getString(1), resultSet.getInt(2));
            }

            return lhmap;

        } catch (Exception e) {

        }
        return null;
    }

}
