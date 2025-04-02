package cam.slava.learn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogonDto {

    @NotBlank(message = "email is required")
    @Size(min = 3, max = 50, message = "User email must be between 3 and 50 characters")
    private String userEmail;

    @NotBlank(message = "password is required")
    private String password;
}
