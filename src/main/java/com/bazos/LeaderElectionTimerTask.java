package com.bazos;

import java.util.TimerTask;

public class LeaderElectionTimerTask extends TimerTask {

    private final EventBroadcaster<LeaderElectionContext> broadcaster = new SimpleEventBroadcaster<>();

    private final LeaderElectionDao dao;

    public LeaderElectionTimerTask(LeaderElectionDao dao) {
        this.dao = dao;
    }

    @Override
    public void run() {
        dao.attemptLeadership();

        broadcaster.broadcast(new LeaderElectionContext(dao.isLeader(),
                                                        dao.getLeader()));
    }

    public void addListener(EventListener<LeaderElectionContext> listener) {
        broadcaster.addListener(listener);
    }
}
