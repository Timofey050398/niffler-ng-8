package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public class GhApiClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(Config.getInstance().ghUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final GhApi ghApi = retrofit.create(GhApi.class);


  public String issueState(@Nonnull String issueNumber) {
      JsonNode responseBody;
      try {
          responseBody = ghApi.issue(
              "Bearer " + System.getenv(GH_TOKEN_ENV),
              issueNumber
          ).execute().body();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      return Objects.requireNonNull(responseBody).get("state").asText();
  }
}
