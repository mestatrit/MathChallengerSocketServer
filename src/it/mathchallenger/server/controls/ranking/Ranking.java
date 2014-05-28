package it.mathchallenger.server.controls.ranking;

import it.mathchallenger.server.storage.DBConnectionPool;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class Ranking extends Thread {
	private static Ranking				   instance;
	private static Properties				properties;
	public static ArrayList<EntryClassifica> classifica;

	public static Ranking getInstance() {
		if (instance == null)
			instance = new Ranking();
		return instance;
	}

	private Ranking() {
		readProperties();
	}

	public static void readProperties() {
		if (properties == null)
			properties = new Properties();
		properties.clear();
		try {
			InputStream in = new FileInputStream("ranking.properties");
			properties.load(in);
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		while (true) {
			ArrayList<EntryClassifica> stat = getRankingFromDB();
			try {
				pubblica(stat);
				try {
					sleep(Integer.parseInt(properties.getProperty("TIME_SLEEP")));
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			catch (IOException e1) {
				e1.printStackTrace();
				try {
					sleep(Integer.parseInt(properties.getProperty("TIME_SLEEP_ERROR")));
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void pubblica(ArrayList<EntryClassifica> stats) throws IOException {
		StringBuilder params = new StringBuilder();
		params.append("username=" + properties.getProperty("Username") + "&password=" + properties.getProperty("Password"));
		for (int i = 0; i < stats.size(); i++) {
			EntryClassifica st = stats.get(i);
			params.append("&u");
			params.append(i);
			params.append("=");
			params.append(st.getUtente());
			params.append("&p");
			params.append(i);
			params.append("=");
			params.append(st.getPunteggio());
		}
		String urlParameters = params.toString();
		sendPost(properties.getProperty("URL_Publish"), urlParameters);
	}

	private void sendPost(String url, String urlParameters) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", properties.getProperty("User-Agent"));
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
		con.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
	}

	private ArrayList<EntryClassifica> getRankingFromDB() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		if (classifica == null)
			classifica = new ArrayList<EntryClassifica>(50);
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT punteggio,username FROM statistiche stat JOIN account acc ON stat.id_utente = acc.id ORDER BY stat.punteggio DESC LIMIT 50";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			classifica.clear();
			while (rs.next()) {
				int punt = rs.getInt("punteggio");
				String username = rs.getString("username");
				classifica.add(new EntryClassifica(username, punt));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return classifica;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		DBConnectionPool.init();
		Ranking r = new Ranking();
		r.start();
	}

	public ArrayList<EntryClassifica> getClassifica() {
		return classifica;
	}
}
