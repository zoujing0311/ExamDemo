package com.migu.schedule;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.TaskInfo;
import com.migu.schedule.info.TaskInfos;

/*
*类名和方法不能修改
 */
public class Schedule {

	private final Set<Integer> nodes = new TreeSet<>();
	private final Map<Integer, TaskInfos> taskMap = new TreeMap<>();
	private final Set<Integer> pending = new HashSet<>();

	/**
	 * 功能说明:
	 * 系统初始化，会清空所有数据，包括已经注册到系统的服务节点信息、以及添加的任务信息，全部都被清理。
	 * 执行该命令后，系统恢复到最初始的状态。
	 * 
	 * 参数说明： 无
	 * 
	 * 输出说明： 初始化成功，返回E001初始化成功。 未做此题返回 E000方法未实现。
	 * 
	 */
    public int init() {
    	nodes.clear();
    	taskMap.clear();
		pending.clear();
        return ReturnCodeKeys.E001;
    }

    /**
	 * 功能说明: 
	 * 系统初始化后，服务节点可以通过注册接口注册到本系统。
	 * 
	 * 参数说明：
	 * nodeId 服务节点编号, 每个服务节点全局唯一的标识, 取值范围： 大于0；
	 * 
	 * 输出说明： 
	 * 注册成功，返回E003:服务节点注册成功。
	 * 如果服务节点编号小于等于0, 返回E004:服务节点编号非法。
	 * 如果服务节点编号已注册, 返回E005:服务节点已注册。
	 * 
	 */
    public int registerNode(int nodeId) {
    	if (nodeId > 0) {
			if (nodes.contains(nodeId)) {
				return ReturnCodeKeys.E005;
			} else {
				nodes.add(nodeId);
				return ReturnCodeKeys.E003;
			}
		} else {
			return ReturnCodeKeys.E004;
		}
    }
    
    /**
	 * 功能说明: 
	 * 1、 从系统中删除服务节点； 
	 * 2、 如果该服务节点正运行任务，则将运行的任务移到任务挂起队列中，等待调度程序调度。
	 * 
	 * 参数说明： 
	 * nodeId服务节点编号, 每个服务节点全局唯一的标识, 取值范围： 大于0。
	 * 
	 * 输出说明：
	 * 注销成功，返回E006:服务节点注销成功。 
	 * 如果服务节点编号小于等于0, 返回E004:服务节点编号非法。
	 * 如果服务节点编号未被注册, 返回E007:服务节点不存在。
	 * 
	 */
    public int unregisterNode(int nodeId) {
    	if (nodeId > 0) {
    		if(nodes.remove(nodeId)) {
    			return ReturnCodeKeys.E006;
    		} else {
    			return ReturnCodeKeys.E007;
    		}
		} else {
			return ReturnCodeKeys.E004;
		}
    }

    /**
	 * 功能说明: 
	 * 将新的任务加到系统的挂起队列中，等待服务调度程序来调度。
	 * 
	 * 参数说明： 
	 * taskId任务编号；取值范围： 大于0。 consumption资源消耗率；
	 * 
	 * 输出说明：
	 * 添加成功，返回E008任务添加成功。
	 * 如果任务编号小于等于0, 返回E009:任务编号非法。
	 * 如果相同任务编号任务已经被添加, 返回E010:任务已添加。
	 * 
	 */
    public int addTask(int taskId, int consumption) {
    	if (taskId > 0 && consumption > 0) {
			if (!taskMap.containsKey(taskId)) {
				taskMap.put(taskId, new TaskInfos(taskId, consumption));
				pending.add(taskId);
				return ReturnCodeKeys.E008;
			} else {
				return ReturnCodeKeys.E010;
			}
		} else {
			return ReturnCodeKeys.E009;
		}
    }

    /**
	 * 功能说明: 
	 * 将在挂起队列中的任务 或 运行在服务节点上的任务删除。
	 * 
	 * 参数说明： 
	 * taskId任务编号；取值范围： 大于0。
	 * 
	 * 输出说明： 
	 * 删除成功，返回E011:任务删除成功。 
	 * 如果任务编号小于等于0, 返回E009:任务编号非法。 
	 * 如果指定编号的任务未被添加, 返回E012:任务不存在。
	 * 
	 */
    public int deleteTask(int taskId) {
    	if (taskId > 0) {
			if (taskMap.containsKey(taskId)) {
				taskMap.remove(taskId);
				pending.remove(taskId);
				return ReturnCodeKeys.E011;
			} else {
				return ReturnCodeKeys.E012;
			}
		} else {
			return ReturnCodeKeys.E009;
		}
    }

    /**
	 * 
	 * 功能说明: 
	 * 如果挂起队列中有任务存在，则进行根据上述的任务调度策略，获得最佳迁移方案，进行任务的迁移， 返回调度成功
	 * 如果没有挂起的任务，则将运行中的任务则根据上述的任务调度策略，获得最佳迁移方案；
	 * 如果在最佳迁移方案中，任意两台不同服务节点上的任务资源总消耗率的差值小于等于调度阈值， 则进行任务的迁移，返回调度成功，
	 * 如果在最佳迁移方案中，任意两台不同服务节点上的任务资源总消耗率的差值大于调度阈值，则不做任务的迁移，返回无合适迁移方案
	 * 
	 * 参数说明：
	 * threshold系统任务调度阈值，取值范围： 大于0；
	 * 
	 * 输出说明：
	 * 如果调度阈值取值错误，返回E002调度阈值非法。
	 * 如果获得最佳迁移方案, 进行了任务的迁移,返回E013: 任务调度成功;
	 * 如果所有迁移方案中，总会有任意两台服务器的总消耗率差值大于阈值。则认为没有合适的迁移方案,返回 E014:无合适迁移方案;
	 */
    public int scheduleTask(int threshold) {
    	if(threshold > 0 && !nodes.isEmpty()){
	    	if (taskMap.isEmpty()) {
				return ReturnCodeKeys.E013;
			}
    	}
    	return ReturnCodeKeys.E002;
    }

    /**
	 * 功能说明: 
	 * 查询获得所有已添加任务的任务状态, 以任务列表方式返回。
	 * 
	 * 参数说明：
	 * Tasks 保存所有任务状态列表；要求按照任务编号升序排列,
	 * 如果该任务处于挂起队列中, 所属的服务编号为-1;
	 * 在保存查询结果之前,要求将列表清空.
	 * 
	 * 输出说明：
	 * 未做此题返回 E000方法未实现。
	 * 如果查询结果参数tasks为null，返回E016:参数列表非法
	 * 如果查询成功, 返回E015: 查询任务状态成功;查询结果从参数Tasks返回。
	 */
    public int queryTaskStatus(List<TaskInfo> tasks) {
    	if (null != tasks) {
    		tasks.clear();
			for (TaskInfos item : taskMap.values()) {
				TaskInfo task = new TaskInfo();
				task.setNodeId(item.getNodeId());
				task.setTaskId(item.getTaskId());
				tasks.add(task);
			}
			return ReturnCodeKeys.E015;
		} else {
			return ReturnCodeKeys.E016;
		}
    }
}
