# Drools Service
This service shows how to use Drools for simple case management.

## Instructions

1. Clone the repo git@github.com:Shumakriss/drools-service.git
1. Import to Eclipse/IntelliJ with Maven
1. (Optional) Maven build
1. Run the Application.java class
1. Test out the URLs

## Endpoints

### Start process
POST: `/process/{processName}`
Begins a process of based on the name

### Get tasks
GET: `/task`

Optional Parameters:
* `processId`

Examples:
* `http://localhost:8080/task` Returns a list of all tasks
* `http://localhost:8080/task?processId=1` Returns a list of tasks for a specific case/process

### Update a task
PUT: `/task/{taskId}`

Request Body Example:
* Complete a task:
`{
 	"id":2,
 	"name":"First task",
 	"processId":1,
 	"processName":"SAMPLE",
 	"status":"Complete"
 }`
 * Rollback a task:
 `{
  	"id":5,
  	"name":"Third task",
  	"processId":1,
  	"processName":"SAMPLE",
  	"status":"Rollback"
  }`
    * Note: For accurate reporting, this will not delete any subsequent tasks nor will it revert the existing task to 
     "In Progress". Instead, the subsequent tasks are "Cancelled" and a new task of the same type is created.
 


## Additional Notes
* Rules can match on subtypes although this may not work well with generic database tables like process/task
* Additional rules may be desired for different processes unless these are managed by separate services
* While it looks like there are a lot of database round trips in the controller, JPA is actually managed data transfer
