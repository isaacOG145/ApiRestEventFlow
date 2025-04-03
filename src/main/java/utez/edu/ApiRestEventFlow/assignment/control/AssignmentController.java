package utez.edu.ApiRestEventFlow.assignment.control;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
import utez.edu.ApiRestEventFlow.assignment.model.AssignmentDTO;
import utez.edu.ApiRestEventFlow.utils.Message;

@RestController
@RequestMapping("/assignment")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<Message> getAll() {
        return assignmentService.findAll();
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Message> getById(@PathVariable Long id) {
        return assignmentService.findById(id);
    }

    @GetMapping("/findByActivity")
    public ResponseEntity<Message> findByActivity(@Validated @RequestBody AssignmentDTO assignmentDTO) {
        return assignmentService.findByEvent(assignmentDTO);
    }

    @GetMapping("/findByChecker")
    public ResponseEntity<Message> findByChecker(@Validated @RequestBody AssignmentDTO assignmentDTO) {
        return assignmentService.findByChecker(assignmentDTO);
    }

    @GetMapping("/events/findByOwner/{ownerId}")
    public ResponseEntity<Message> findEventByOwnerId(@PathVariable Long ownerId) {
        return assignmentService.findEventByOwnerId(ownerId);
    }

    @GetMapping("/workshops/findByOwner/{ownerId}")
    public ResponseEntity<Message> findWorkshopByOwnerId(@PathVariable Long ownerId) {
        return assignmentService.findWorkshopByOwnerId(ownerId);
    }

    @PostMapping("/saveAssignment")
    public ResponseEntity<Message> saveAssignment(@Validated @RequestBody AssignmentDTO assignmentDTO) {
        return assignmentService.saveAssignment(assignmentDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateAssignment(@Validated @RequestBody AssignmentDTO assignmentDTO) {
        return assignmentService.updateAssignment(assignmentDTO);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<Message> changeStatus(@Validated @PathVariable Long id) {
        return assignmentService.changeStatus(id);
    }
}
