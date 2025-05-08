package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement historyBox = $("#spendings");
  private final SelenideElement statisticsBox = $("#stat");
  private final SelenideElement searchInput = $("input");
  private final SelenideElement statImg = $("canvas[role='img']");

  public EditSpendingPage editSpending(String spendingDescription) {
    searchInput.setValue(spendingDescription).pressEnter();
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public void checkThatTableContains(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .should(visible);
  }

  public MainPage assertMainComponents(){
    historyBox.shouldBe(visible, Duration.ofSeconds(10));
    statisticsBox.shouldBe(visible, Duration.ofSeconds(10));
    return this;
  }


  public BufferedImage getStatImage(){
      try {
          return ImageIO.read(Objects.requireNonNull(statImg.screenshot()));
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
  }

}
