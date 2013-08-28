package com.ptvag.activiti;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class HistoryServiceTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();
	
	private InputStream loadProcess(String name) {
		return getClass().getClassLoader().getResourceAsStream(name);
	}

	@Test
	public void startProcess() throws Exception {
		RepositoryService repositoryService = activitiRule.getRepositoryService();
		TaskService taskService = activitiRule.getTaskService();
		FormService formService = activitiRule.getFormService();
		HistoryService historyService = activitiRule.getHistoryService();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		
		repositoryService.createDeployment().addInputStream("Registration.bpmn20.xml",loadProcess("Registration.bpmn")).deploy();
		
		runtimeService.startProcessInstanceByKey("registrationProcess");
		
		Task task = taskService.createTaskQuery().active().singleResult();
		
		String taskId = task.getId();
		
		Map<String,String> formDataMap = new HashMap<String, String>();
		formDataMap.put("firstName", "Michael");
		
		formService.submitTaskFormData(taskId, formDataMap);
		
		HistoricTaskInstance singleResult = historyService.createHistoricTaskInstanceQuery().singleResult();
		List<HistoricVariableInstance> details = historyService.createHistoricVariableInstanceQuery().taskId(singleResult.getId()).list();
		
		assertEquals(false, details.isEmpty());
	}
}