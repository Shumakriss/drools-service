# Drools Service
This service shows how to use Drools for simple case management. This can be an alternative to expensive proprietary
solutions or even an alternative to BPM solutions. 

>The best feature of rules and business process management is that they make organizational issues obvious. The worst feature
of rules and business process management is that they make organizational issues obvious.  -- Me

I believe BPM systems can be useful, however, many organizations
adopt it for the wrong reasons or at the wrong time. In those cases, rule-based systems may be easier to adopt and
maintain.

**Tradeoffs vs jBPM-driven system:**
* Text-based
    * Easier to manage in version control
    * No plugins or IDE's required
    * No complex underlying XML to manage small tweaks
    * Not visual and not as useful for experience BPMN 2.0 analysts
    * Rules can sometimes be simpler to translate into business requirements
    * Decision tables and DSL's can simplify rule management
* No persistence
    * Complements a system with existing activity management because it incurs no additional data to manage
    * Can not manage long-running processes by itself
* Not opinionated about task or process object definitions or their lifecycles
    * Make any kind of object to represent activities (e.g. a "case")
    * Use any status you like for the object
* Easily manage events

**When does this matter?**
* Your BPM system (or any system) doesn't seem "flexible enough"
    * Are you defining a process that's not well-understood?
    * Are you enforcing a process too granularly (e.g. structuring every user behavior instead of broad steps)
    * Have you coupled your system too tightly?
    * Have you coupled otherwise separate user groups together?
    * Are you generally slow at deploying software?
        * Long release cycles
        * Manual testing
        * Manual deployments
        * Siloing between developers, testers, and ops?
    * Is your software generally tightly coupled to other systems?
* You have trouble tracking or reporting work in progress
    * Do you really understand how the work should be done?
    * Do your systems truly reflect the domain?
    * Do your systems help or inhibit the users?



## Instructions

1. Clone the repo [git@github.com:Shumakriss/drools-service.git](git@github.com:Shumakriss/drools-service.git)
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
