package guru.qa.niffler.page.component.spend;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.BaseComponent;
import org.openqa.selenium.WebElement;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$;

public class SpendRow extends BaseComponent<SpendRow> {

    protected SpendRow(SelenideElement self) {
        super(self);
    }

    private SelenideElement checkBoxCell = self.$$("td").get(0);
    private SelenideElement categoryCell = cellTextElement(1);
    private SelenideElement amountCell = cellTextElement(2);
    private SelenideElement descriptionCell = cellTextElement(3);
    private SelenideElement dateCell = cellTextElement(4);
    private SelenideElement editButton = self.$$("td").get(5);

    private SelenideElement cellTextElement(int i){
        return self.$$("td").get(i).$("span");
    }

    protected EditSpendingPage editSpend(){
        editButton.click();
        return new EditSpendingPage();
    }

    protected MainPage clickCheckBox(){
        checkBoxCell.click();
        return new MainPage();
    }



    private SpendJson toJson(){
        return new SpendJson(
                null,
                getDate(),
                new CategoryJson(
                        null,
                        getCategory(),
                        null,
                        false
                ),
                getCurrency(),
                getAmount(),
                getDescription(),
                null
        );
    }

    public static SpendJson toJson(WebElement element){
        return new SpendRow($(element)).toJson();
    }

    private String getDescription() {
        return descriptionCell.getText();
    }

    private Double getAmount() {
        return Double.parseDouble(
                amountCell.getText()
                        .replaceAll("[^\\d.,]", "")
                        .replace(',','.')
        );
    }

    private CurrencyValues getCurrency() {
        return switch (
                Pattern.compile("[₽$€₸]")
                        .matcher(amountCell.getText())
                        .group()
                        .charAt(0)
                ){
            case '₽' -> CurrencyValues.RUB;
            case '$' -> CurrencyValues.USD;
            case '€' -> CurrencyValues.EUR;
            case '₸' -> CurrencyValues.KZT;
            default -> throw new RuntimeException("unknown currency");
        };
    }

    private String getCategory() {
        return categoryCell.getText();
    }

    private Date getDate() {
        try {
            return new SimpleDateFormat("MMM dd, yyyy")
                    .parse(dateCell.getText());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
