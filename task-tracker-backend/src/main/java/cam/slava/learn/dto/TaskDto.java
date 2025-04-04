package cam.slava.learn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long taskId;
    private String taskTitle;
    private String taskDescription;
    private boolean done;
}
