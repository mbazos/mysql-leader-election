package com.bazos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaderElectionDao {

    private static final Logger LOG = LoggerFactory.getLogger(LeaderElectionDao.class);

    private static final String ATTEMPT_LEADERSHIP = "insert ignore into service_election ( anchor, service_id, last_seen_active ) values ( 1, ?, now() ) on duplicate key update service_id = if(last_seen_active < now() - interval 20 second, values(service_id), service_id), last_seen_active = if(service_id = values(service_id), values(last_seen_active), last_seen_active)";
    private static final String FORCE_LEADERSHIP   = "replace into service_election ( anchor, service_id, last_seen_active ) values ( 1, ?, now() )";
    private static final String FORCE_RE_ELECTION  = "delete from service_election";
    private static final String IS_LEADER          = "select count(*) as is_leader from service_election where anchor=1 and service_id = ?";
    private static final String WHO_IS_THE_LEADER  = "select max(service_id) as leader from service_election where anchor=1;";

    private final DataSource dataSource;
    private final String     hostName;

    public LeaderElectionDao(DataSource dataSource, String hostName) {
        this.dataSource = dataSource;
        this.hostName = hostName;
    }

    public void attemptLeadership() {
        try (Connection con = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatement(con, ATTEMPT_LEADERSHIP, hostName);
            ResultSet ignored = ps.executeQuery()) {
        } catch (SQLException e) {
            LOG.error("Issue attempting leadership election", e);
        }
    }

    public void forceLeadership() {
        try (Connection con = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatement(con, FORCE_LEADERSHIP, hostName);
            ResultSet ignored = ps.executeQuery()) {
        } catch (SQLException e) {
            LOG.error("Issue attempting forced leadership election", e);
        }
    }

    public void forceReElection() {
        try (Connection con = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatement(con, FORCE_RE_ELECTION, hostName);
            ResultSet ignored = ps.executeQuery()) {
        } catch (SQLException e) {
            LOG.error("Issue attempting forced re-election", e);
        }
    }

    public boolean isLeader() {
        try (Connection con = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatement(con, IS_LEADER, hostName);
            ResultSet rs = ps.executeQuery()) {
            return rs.getBoolean("is_leader");
        } catch (SQLException e) {
            LOG.error("Issue attempting to get the leader", e);
        }

        return false;
    }

    public String getLeader() {
        try (Connection con = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatement(con, WHO_IS_THE_LEADER, hostName);
            ResultSet rs = ps.executeQuery()) {
            return rs.getString("leader");
        } catch (SQLException e) {
            LOG.error("Issue attempting to get the leader", e);
        }

        return null;
    }

    private PreparedStatement createPreparedStatement(Connection con, String sql, String hostName) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, hostName);
        return ps;
    }

}