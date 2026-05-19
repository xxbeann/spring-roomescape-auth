package roomescape.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dao.MemberDao;
import roomescape.domain.Member;
import roomescape.exception.AuthenticationException;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final MemberDao memberDao;

    public AuthService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public Member checkValidLogin(String email, String password) {
        Member member;
        try {
            member = memberDao.findByEmail(email);
        } catch (EmptyResultDataAccessException e) {
            throw new AuthenticationException();
        }
        if (!member.matchesPassword(password)) {
            throw new AuthenticationException();
        }
        return member;
    }
}
