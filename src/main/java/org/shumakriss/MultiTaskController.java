package org.shumakriss;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;


@RestController
public class MultiTaskController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ProcessRepository processRepository;

    @Autowired
    private KieContainer kieContainer;


    @RequestMapping(value = "/task", method = RequestMethod.GET)
    public Iterable<Task> getTasks(@PathParam("processId") Long processId) {
        System.out.println(processId);

        if(processId == null){
            return taskRepository.findAll();
        } else {
            return taskRepository.findByProcessId(processId);
        }
    }


    @RequestMapping(value = "/task", method = RequestMethod.POST)
    public Long createTask(@RequestBody Task task) {
        System.out.println(task);
        taskRepository.save(task);
        return task.getId();
    }

    @RequestMapping("/process/{processName}")
    public Long startProcess(@PathVariable String processName) {

        Process process = new Process();
        process.setName(processName);
        process.setStatus("In Progress");
        process = processRepository.save(process);

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(process);
        kieSession.fireAllRules();

        QueryResults all_tasks = kieSession.getQueryResults("get all tasks");

        for (QueryResultsRow row : all_tasks) {
            Task queryTask = (Task) row.get("task");
            System.out.println("Query result Task: " + queryTask);
            taskRepository.save(queryTask);
        }

        kieSession.dispose();

        return process.getId();
    }

    @RequestMapping("/task/{taskId}")
    public void complete(@RequestBody Task updatedTask) throws Exception {

        // Get all relevant objects
        List<Task> taskList = taskRepository.findByProcessId(updatedTask.getProcessId());
        Optional<Process> optionalProcess = processRepository.findById(updatedTask.getProcessId());
        Process process;
        if(optionalProcess.isPresent())
            process = optionalProcess.get();
        else
            throw new Exception("Could not find process");

        // Update the current object and add them all to working memory
        KieSession kieSession = kieContainer.newKieSession();
        for(Task task : taskList) {
            if (task.getId() == updatedTask.getId()) {
                task.setStatus(updatedTask.getStatus());
                taskRepository.save(task);
            }
            kieSession.insert(task);
        }
        kieSession.insert(process);

        // Run the rules
        kieSession.fireAllRules();

        // Collect the results and save them to the database
        QueryResults tasks = kieSession.getQueryResults("get all tasks");
        taskList.clear();
        for (QueryResultsRow row : tasks) {
            Task task = (Task) row.get("task");
            taskList.add(task);
            System.out.println("Query result Task: " + task);
        }
        taskRepository.saveAll(taskList);

        // Don't forget the process
        QueryResults processes = kieSession.getQueryResults("get all processes");
        for (QueryResultsRow row : processes) {
            Process updatedProcess = (Process) row.get("process");
            System.out.println("Query result Task: " + process);
            processRepository.save(process);
        }

        // Clean up the session
        kieSession.dispose();
    }
}
