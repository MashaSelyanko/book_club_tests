package models.update_user;

public record PartialUpdateUserWithPatchBodyModel
        (
                String username,
                String email
        )
{
}
