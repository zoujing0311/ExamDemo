package com.migu.schedule.info;

public class TaskInfos {
	private int nodeId;//服务节点编号
	private int taskId;//任务编号
	private int consumption;//资源消耗率

	public TaskInfos(int taskId, int consumption) {
		this.nodeId = -1;
		this.taskId = taskId;
		this.consumption = consumption;
	}
	
	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getConsumption() {
		return consumption;
	}

	public void setConsumption(int consumption) {
		this.consumption = consumption;
	}


	
}
