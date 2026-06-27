package models.updateUser;

public record UpdateUserBodyModel
        (
                String username,
                String firstName,
                String lastName,
                String email
        )
{
}
