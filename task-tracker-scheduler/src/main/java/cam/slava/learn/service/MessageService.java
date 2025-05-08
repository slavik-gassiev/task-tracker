package cam.slava.learn.service;

import cam.slava.learn.dto.MessageDto;
import cam.slava.learn.dto.TaskDto;
import cam.slava.learn.dto.UserDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    public List<MessageDto> createMessages(List<UserDto> allUsers) {

        List<MessageDto> messages = new ArrayList<>();
        String emailText = "";

        for (UserDto user : allUsers) {
            String activeText = createActiveMessages(user).orElse("");
            String completedText = createCompletedMessages(user).orElse("");

            if (activeText.isBlank() && completedText.isBlank()) {continue;}

            emailText = activeText + " \n " + completedText;
            messages.add(new MessageDto(user.getName(), emailText));
        }

        return messages;
    }

    private Optional<String> createActiveMessages(UserDto user) {
        StringBuffer activeMessages = new StringBuffer();

        List<TaskDto> activeTasks = user.getTasks().stream()
                .filter(taskDto -> !taskDto.isDone())
                .toList();

        if (activeTasks.isEmpty()) {
            return Optional.empty();
        }

        activeMessages.append(String.format("You have %d active tasks:\n\n", activeTasks.size()));

        activeTasks.stream()
                .limit(5)
                .forEach(taskDto -> activeMessages.append(" * ").append(taskDto.getTitle()).append("\n"));

        return Optional.of(activeMessages.toString());
    }

    private Optional<String> createCompletedMessages(UserDto user) {
        StringBuffer completedMessages = new StringBuffer();
        LocalDateTime now = LocalDateTime.now();

        List<TaskDto> completedTasks = user.getTasks().stream()
                .filter(taskDto -> taskDto.isDone() && taskDto.getCreatedAt().isAfter(now.minusDays(1)))
                .toList();

        if (completedTasks.isEmpty()) {
            return Optional.empty();
        }

        completedMessages.append(String.format("You have %d completed tasks:\n\n", completedTasks.size()));

        completedTasks.stream()
                .limit(5)
                .forEach(taskDto -> completedMessages.append(" * ").append(taskDto.getTitle()).append("\n"));

        return Optional.of(completedMessages.toString());
    }
}
