package org.shumakriss;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByName(String name);
    List<Task> findByProcessId(Long processId);
}
