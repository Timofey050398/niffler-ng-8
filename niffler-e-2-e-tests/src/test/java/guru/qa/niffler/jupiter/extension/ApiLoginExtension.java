package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.api.AuthApiClient;
import guru.qa.niffler.service.api.SpendApiClient;
import guru.qa.niffler.service.api.UsersApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import static guru.qa.niffler.jupiter.extension.UserExtension.setUser;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private static final Config CFG = Config.getInstance();

    private final AuthApiClient authApiClient = new AuthApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();

    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser){
        this.setupBrowser = setupBrowser;
    }
    public ApiLoginExtension(){
        this.setupBrowser = true;
    }

    public static ApiLoginExtension rest(){
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    UserJson user;
                    UserJson extensionUser = UserExtension.createdUser();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())){
                        if (extensionUser == null) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                        }
                        user = extensionUser;
                    } else {
                        if (extensionUser != null) {
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username and password!");
                        }
                        user = enrichmentUser(apiLogin);
                        setUser(user);
                    }

                    setToken(authApiClient.token(user));

                    if (setupBrowser) {
                        setupBrowserSession();
                    }

                });
    }

    private void setupBrowserSession(){
        Selenide.open(CFG.frontUrl());
        Selenide.localStorage().setItem("id_token", getToken());
        WebDriverRunner.getWebDriver().manage().addCookie(getJsessionIdCookie());
        Selenide.open(CFG.frontUrl(), MainPage.class).assertMainComponents();
    }

    private UserJson enrichmentUser(ApiLogin apiLogin){
        final String username = apiLogin.username();
        UserJson user = usersApiClient.currentUser(username);
        TestData testData = new TestData(
                apiLogin.password(),
                spendApiClient.getCategories(username, false),
                spendApiClient.getSpends(username, null, null, null),
                usersApiClient.getFriends(username),
                usersApiClient.getIncomeInvitations(username),
                usersApiClient.getOutcomeInvitations(username)
        );

        return user.withTestData(testData);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get("token", String.class);
    }

    public static void setToken(String token){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token",token);
    }

    public static String getToken(){
        return (String) TestMethodContextExtension.context().getStore(NAMESPACE).get("token");
    }

    public static void setCode(String code){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code",code);
    }

    public static String getCode(){
        return (String) TestMethodContextExtension.context().getStore(NAMESPACE).get("code");
    }

    public static Cookie getJsessionIdCookie(){
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}
