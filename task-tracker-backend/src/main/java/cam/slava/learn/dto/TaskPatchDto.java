package cam.slava.learn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskPatchDto {

    @NotNull(message = "task id is required")
    private Long id;
    private String taskTitle;
    private String taskDescription;
    private Boolean done;
}
