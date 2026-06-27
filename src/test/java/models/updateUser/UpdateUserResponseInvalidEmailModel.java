package models.updateUser;

import java.util.List;

public record UpdateUserResponseInvalidEmailModel
        (
                List<String> email
        )
{
}
