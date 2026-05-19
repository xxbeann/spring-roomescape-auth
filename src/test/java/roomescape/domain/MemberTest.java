package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class MemberTest {

    @Test
    void 이메일이_null이면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                null,
                "password",
                "브라운"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일이_비어있으면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "",
                "password",
                "브라운"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일_형식이_아니면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "not-an-email",
                "password",
                "브라운"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 비밀번호가_null이면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "brown@email.com",
                null,
                "브라운"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 비밀번호가_비어있으면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "brown@email.com",
                "",
                "브라운"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름이_null이면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "brown@email.com",
                "password",
                null
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름이_비어있으면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "brown@email.com",
                "password",
                ""
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름이_두글자_미만이면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "brown@email.com",
                "password",
                "브"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름이_열글자_초과이면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "brown@email.com",
                "password",
                "브".repeat(11)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름에_특수문자가_포함되면_회원을_생성할_수_없다() {
        assertThatThrownBy(() -> new Member(
                1L,
                "brown@email.com",
                "password",
                "브라운!"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 입력한_비밀번호가_저장된_비밀번호와_일치하면_true를_반환한다() {
        Member member = new Member(1L, "brown@email.com", "password", "브라운");

        assertThat(member.matchesPassword("password")).isTrue();
    }

    @Test
    void 입력한_비밀번호가_저장된_비밀번호와_다르면_false를_반환한다() {
        Member member = new Member(1L, "brown@email.com", "password", "브라운");

        assertThat(member.matchesPassword("wrong-password")).isFalse();
    }
}
