package it.polito.tdp.exam.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.exam.model.Adiacenza;
import it.polito.tdp.exam.model.People;
import it.polito.tdp.exam.model.Team;

public class BaseballDAO {
	
	public List<Adiacenza> readAdiacenzePeso(String nome){
		String sql = "SELECT a1.year as y1, a2.year as y2, COUNT(distinct a1.playerID) AS peso, a1.playerID, a2.playerID "
				+ "FROM appearances a1, appearances a2, teams t1 "
				+ "WHERE a1.year > a2.year and a1.playerID = a2.playerID and t1.name = (?) and t1.teamCode = a1.teamCode AND t1.teamCode = a2.teamCode "
				+ "GROUP BY a1.year, a2.year "
				+ "ORDER BY peso desc";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, nome);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Adiacenza a = new Adiacenza(rs.getInt("y1"), rs.getInt("y2"));
				a.setPeso(rs.getInt("peso"));
				result.add(a);
				
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
		
	}
	
	
	public List<Adiacenza> readAdiacenze(String nome) {
		
		String sql = "SELECT a.year AS y1, b.year AS y2 "
				+ "FROM "
				+ "(SELECT teams.year, teams.teamCode "
				+ "FROM teams "
				+ "WHERE teams.name = (?) "
				+ "GROUP BY teams.year) a, "
				+ "(SELECT teams.year, teams.teamCode "
				+ "FROM teams "
				+ "WHERE teams.name = (?) "
				+ "GROUP BY teams.year) b "
				+ "WHERE a.year > b.year "
				+ "GROUP BY a.year, b.year";
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, nome);
			st.setString(2, nome);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Adiacenza(rs.getInt("y1"), rs.getInt("y2")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
		
	}
	
	
	
	public List<Integer> readYear(String nome) {
		
		String sql = "SELECT teams.year "
				+ "FROM teams "
				+ "WHERE teams.name = (?) "
				+ "GROUP BY teams.year";
		
		List<Integer> result = new ArrayList<Integer>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, nome);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(rs.getInt("year"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
		
		

	public List<People> readAllPlayers() {
		String sql = "SELECT * " + "FROM people";
		List<People> result = new ArrayList<People>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new People(rs.getString("playerID"), rs.getString("birthCountry"), rs.getString("birthCity"),
						rs.getString("deathCountry"), rs.getString("deathCity"), rs.getString("nameFirst"),
						rs.getString("nameLast"), rs.getInt("weight"), rs.getInt("height"), rs.getString("bats"),
						rs.getString("throws"), getBirthDate(rs), getDebutDate(rs), getFinalGameDate(rs),
						getDeathDate(rs)));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Team> readAllTeams() {
		String sql = "SELECT * " + "FROM  teams GROUP BY teams.name";
		List<Team> result = new ArrayList<Team>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Team(rs.getInt("iD"), rs.getInt("year"), rs.getString("teamCode"), rs.getString("divID"),
						rs.getInt("div_ID"), rs.getInt("teamRank"), rs.getInt("games"), rs.getInt("gamesHome"),
						rs.getInt("wins"), rs.getInt("losses"), rs.getString("divisionWinnner"),
						rs.getString("leagueWinner"), rs.getString("worldSeriesWinnner"), rs.getInt("runs"),
						rs.getInt("hits"), rs.getInt("homeruns"), rs.getInt("stolenBases"), rs.getInt("hitsAllowed"),
						rs.getInt("homerunsAllowed"), rs.getString("name"), rs.getString("park")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	// =================================================================
	// ==================== HELPER FUNCTIONS =========================
	// =================================================================

	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * 
	 * @param rs
	 * @return
	 */
	private LocalDate getBirthDate(ResultSet rs) {
		try {
			if (rs.getDate("birth_date") != null) {
				return rs.getDate("birth_date").toLocalDate();
			} else {
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * 
	 * @param rs
	 * @return
	 */
	private LocalDate getDebutDate(ResultSet rs) {
		try {
			if (rs.getDate("debut_date") != null) {
				return rs.getDate("debut_date").toLocalDate();
			} else {
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * 
	 * @param rs
	 * @return
	 */
	private LocalDate getFinalGameDate(ResultSet rs) {
		try {
			if (rs.getDate("finalgame_date") != null) {
				return rs.getDate("finalgame_date").toLocalDate();
			} else {
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * 
	 * @param rs
	 * @return
	 */
	private LocalDate getDeathDate(ResultSet rs) {
		try {
			if (rs.getDate("death_date") != null) {
				return rs.getDate("death_date").toLocalDate();
			} else {
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
