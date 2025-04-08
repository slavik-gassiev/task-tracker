package cam.slava.learn.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskListResponseDto {
    private int count;
    private List<TaskDto> tasks;

    public TaskListResponseDto(List<TaskDto> tasks) {
        this.count = tasks.size();
        this.tasks = tasks;
    }
}
