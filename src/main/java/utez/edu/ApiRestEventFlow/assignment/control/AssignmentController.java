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

    @PostMapping("/saveAssignment")
    public ResponseEntity<Message> saveAssignment(@Validated @RequestBody AssignmentDTO assignmentDTO) {
        return assignmentService.saveAssignment(assignmentDTO);
    }
}
