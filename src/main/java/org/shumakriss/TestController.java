package org.shumakriss;

import org.drools.core.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
public class TestController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    private KieContainer kieContainer;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/task")
    public Iterable<Task> updateTask(@RequestBody Task task) {
        System.out.println("Received Task: " + task);

        taskRepository.save(task);
        System.out.println("Saved task to database: " + task);

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(task);
        kieSession.fireAllRules();
        Collection<FactHandle> factHandles = kieSession.getFactHandles(new ClassObjectFilter(Task.class));

        for( FactHandle factHandle : factHandles){
            System.out.println("Fact Handle: " + factHandle); }


        QueryResults all_tasks = kieSession.getQueryResults("get all tasks");

        for (QueryResultsRow row : all_tasks) {
            Task queryTask = (Task) row.get("task");
            System.out.println("Query result Task: " + queryTask);
            if(queryTask.getId() == null) {
                queryTask.setId(2L);
                taskRepository.save(queryTask);
            }
        }

        kieSession.dispose();


        Iterable<Task> tasks = taskRepository.findAll();
        return tasks;
    }

}
