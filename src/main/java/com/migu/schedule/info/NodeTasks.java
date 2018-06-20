package com.migu.schedule.info;

import java.util.Map;
import java.util.TreeMap;


public class NodeTasks {
	final int nodeId;
	final Map<Integer, Integer> tasks = new TreeMap<>();
	int total = 0;

	NodeTasks(int nodeId) {
		this.nodeId = nodeId;
		this.total = 0;
	}

	void addTask(TaskInfos task) {
		tasks.put(task.getTaskId(), task.getConsumption());
		total += task.getConsumption();
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getNodeId() {
		return nodeId;
	}

	public Map<Integer, Integer> getTasks() {
		return tasks;
	}
}
