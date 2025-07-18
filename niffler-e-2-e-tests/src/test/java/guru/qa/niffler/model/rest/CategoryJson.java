package guru.qa.niffler.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import java.util.UUID;


public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("username")
    String username,
    @JsonProperty("archived")
    boolean archived) {


    @Nonnull
    public static CategoryJson fromEntity(@Nonnull CategoryEntity entity) {
        return new CategoryJson(
                entity.getId(),
                entity.getName(),
                entity.getUsername(),
                entity.isArchived()
        );
    }

    @Nonnull
    public static CategoryJson createArchived(){
        return new CategoryJson(
                null,
                "Archived",
                null,
                true
        );
    }
}
