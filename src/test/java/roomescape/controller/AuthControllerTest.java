package roomescape.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Member;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerTest {

    private static final String INSERT_SINGLE_MEMBER_SQL = """
              INSERT INTO member (id, email, password, name)
              VALUES (1, 'brown@email.com', 'password', '브라운');
              """;
    private static final String USERNAME_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";
    private static final String EMAIL = "brown@email.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "브라운";

    @Test
    @Sql(statements = INSERT_SINGLE_MEMBER_SQL)
    void 세션_로그인에_성공하면_JSESSIONID_쿠키가_발급된다() {
        String setCookie = RestAssured
                .given().log().all()
                .param(USERNAME_FIELD, EMAIL)
                .param(PASSWORD_FIELD, PASSWORD)
                .when().post("/api/v1/auth/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().header("Set-Cookie");

        assertThat(setCookie).isNotBlank();
        assertThat(setCookie).contains("JSESSIONID=");
        assertThat(setCookie).contains("HttpOnly");
    }

    @Test
    @Sql(statements = INSERT_SINGLE_MEMBER_SQL)
    void 비밀번호가_틀리면_401을_반환한다() {
        RestAssured
                .given().log().all()
                .param(USERNAME_FIELD, EMAIL)
                .param(PASSWORD_FIELD, "wrong-password")
                .when().post("/api/v1/auth/login")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("errorCode", is("AUTH401_001"));
    }

    @Test
    void 존재하지_않는_이메일로_로그인하면_401을_반환한다() {
        RestAssured
                .given().log().all()
                .param(USERNAME_FIELD, "nobody@email.com")
                .param(PASSWORD_FIELD, PASSWORD)
                .when().post("/api/v1/auth/login")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("errorCode", is("AUTH401_001"));
    }

    @Disabled
    @Test
    @Sql(statements = INSERT_SINGLE_MEMBER_SQL)
    void sessionLogin() {
        String cookie = RestAssured
                .given().log().all()
                .param(USERNAME_FIELD, EMAIL)
                .param(PASSWORD_FIELD, PASSWORD)
                .when().post("/api/v1/auth/login")
                .then().log().all().extract().header("Set-Cookie").split(";")[0];

        Member member = RestAssured
                .given().log().all()
                .header("Cookie", cookie)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/members/me/session")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract().as(Member.class);

        assertThat(member.getEmail()).isEqualTo(EMAIL);
        assertThat(member.getName()).isEqualTo(NAME);
    }
}
