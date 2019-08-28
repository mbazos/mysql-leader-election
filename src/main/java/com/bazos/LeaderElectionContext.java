package com.bazos;

public class LeaderElectionContext {

    private final boolean isLeader;
    private final String  leader;

    public LeaderElectionContext(boolean isLeader, String leader) {
        this.isLeader = isLeader;
        this.leader = leader;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public String getLeader() {
        return leader;
    }
}
