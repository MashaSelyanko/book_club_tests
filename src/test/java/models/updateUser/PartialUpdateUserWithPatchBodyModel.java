package models.updateUser;

public record PartialUpdateUserWithPatchBodyModel
        (
                String username,
                String email
        )
{
}
