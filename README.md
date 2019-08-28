# mysql-leader-election

Terms of the solution
Our proposed solution offers:

* Single leader election out of multiple nodes
* Leader actively reaffirms its leadership periodically
* Timeout based re-election: decision to re-elect new leader based on the fact current leader has not reaffirmed its leadership over X seconds
* A way to forcibly assume leadership for a specific node
* A way to forcibly call for re-elections by demoting existing leader
* A node/service can easily tell whether it's the leader or not
* Anyone can tell who the leader is

Solution
The solution is composed of a single table and a set of queries which implement the above offers. We assume a service can uniquely identify itself; this is easy to achieve:

Table
The following table will have a single row; the

service_id in that row is the active leader.

```
CREATE TABLE service_election ( 
  anchor tinyint(3) unsigned NOT NULL, 
  service_id varchar(128) NOT NULL, 
  last_seen_active timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
  PRIMARY KEY (anchor) 
) ENGINE=InnoDB 
```