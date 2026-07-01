package models.update_user;

import java.util.List;

public record UpdateUserResponseInvalidEmailModel
        (
                List<String> email
        )
{
}
