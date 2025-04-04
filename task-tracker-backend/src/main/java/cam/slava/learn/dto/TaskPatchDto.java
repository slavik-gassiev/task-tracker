package cam.slava.learn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskPatchDto {
    private String taskTitle;
    private String taskDescription;
    private Boolean done;
}
