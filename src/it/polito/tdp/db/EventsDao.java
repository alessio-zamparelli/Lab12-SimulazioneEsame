package it.polito.tdp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.tdp.model.District;
import it.polito.tdp.model.Event;

public class EventsDao {

	public List<Event> listAllEvents() {
		String sql = "SELECT * FROM events";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			List<Event> list = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"), res.getInt("offense_code"),
							res.getInt("offense_code_extension"), res.getString("offense_type_id"),
							res.getString("offense_category_id"), res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"), res.getDouble("geo_lon"), res.getDouble("geo_lat"),
							res.getInt("district_id"), res.getInt("precinct_id"), res.getString("neighborhood_id"),
							res.getInt("is_crime"), res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Event> listAllEventsFiltered(int giorno, int mese, int anno) {
		String sql = "SELECT * FROM `events`  WHERE DAY(reported_date) = ? AND MONTH(reported_date) = ? AND YEAR(reported_date)=?";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, giorno);
			st.setInt(2, mese);
			st.setInt(3, anno);
			
			List<Event> list = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"), res.getInt("offense_code"),
							res.getInt("offense_code_extension"), res.getString("offense_type_id"),
							res.getString("offense_category_id"), res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"), res.getDouble("geo_lon"), res.getDouble("geo_lat"),
							res.getInt("district_id"), res.getInt("precinct_id"), res.getString("neighborhood_id"),
							res.getInt("is_crime"), res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Integer> listAllYears() {
		String sql = "SELECT DISTINCT YEAR(reported_date) AS anno FROM events";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			List<Integer> list = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(res.getInt("anno"));
			}

			conn.close();
			Collections.sort(list);
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<District> listAllDistrict(int anno) {
		String sql = "SELECT * FROM `events` WHERE YEAR(reported_date) = ? ORDER BY district_id";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);

			Map<Integer, District> results = new HashMap<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {
				int district_id = res.getInt("district_id");
				Event e = new Event(res.getLong("incident_id"), res.getInt("offense_code"),
						res.getInt("offense_code_extension"), res.getString("offense_type_id"),
						res.getString("offense_category_id"), res.getTimestamp("reported_date").toLocalDateTime(),
						res.getString("incident_address"), res.getDouble("geo_lon"), res.getDouble("geo_lat"),
						res.getInt("district_id"), res.getInt("precinct_id"), res.getString("neighborhood_id"),
						res.getInt("is_crime"), res.getInt("is_traffic"));
				District district;
				if (results.containsKey(district_id)) {
					district = results.get(district_id);
				} else {
					district = new District(district_id, this.getLatMedia(district_id, anno),
							this.getLonMedia(district_id, anno));
					results.put(district_id, district);
				}
				district.addEventToList(e);
			}

			conn.close();

			return new ArrayList<>(results.values());

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Double getLatMedia(int district_id, int anno) {
		String sql = "SELECT AVG(geo_lat) AS avgg FROM `events` WHERE YEAR(reported_date) = ? AND district_id = ?";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, district_id);

			ResultSet res = st.executeQuery();
			Double media = 0.0;
			if (res.next()) {
				media = res.getDouble("avgg");
			}

			conn.close();

			return media;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Double getLonMedia(int district_id, int anno) {
		String sql = "SELECT AVG(geo_lon) AS avgg FROM `events` WHERE YEAR(reported_date) = ? AND district_id = ?";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, district_id);

			ResultSet res = st.executeQuery();
			Double media = 0.0;
			if (res.next()) {
				media = res.getDouble("avgg");
			}

			conn.close();

			return media;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public District getDistrettoMinCriminalita(int anno, Set<District> distretti) {
		String sql = "SELECT district_id, COUNT(*) AS avgg FROM `events` WHERE YEAR(reported_date) = ? "
				+ "GROUP BY district_id ORDER BY avgg ASC  LIMIT 1";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet res = st.executeQuery();

			int dis_id = -1;
			Map<Integer, District> disIdMap = distretti.stream()
					.collect(Collectors.toMap(District::getDistrictId, a -> a));
			if (res.next())
				dis_id = res.getInt("district_id");

			conn.close();
			if (disIdMap.containsKey(dis_id))
				return disIdMap.get(dis_id);
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
