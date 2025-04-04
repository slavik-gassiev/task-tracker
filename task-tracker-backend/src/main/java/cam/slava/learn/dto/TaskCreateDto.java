package cam.slava.learn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDto {

    @Size(min = 1, max = 40, message = "Title must be between 1 and 40 characters")
    @NotBlank(message = "title is required")
    private String taskTitle;

    @Size(min = 1, max = 40, message = "Description must be between 1 and 350 characters")
    @NotBlank(message = "description is required")
    private String taskDescription;

    @NotNull(message = "status is required")
    private Boolean done;
}
