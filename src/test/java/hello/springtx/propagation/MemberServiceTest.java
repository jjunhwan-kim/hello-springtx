package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LogRepository logRepository;

    /**
     * memberService     @Transactional: OFF
     * memberRepository  @Transactional: ON
     * logRepository     @Transactional: ON
     */
    @Test
    void outerTransactionOffSuccess() {
        // given
        String username = "outerTransactionOffSuccess";

        // when
        memberService.joinV1(username);

        // then: 모든 데이터가 정상 저장됨
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * memberService     @Transactional: OFF
     * memberRepository  @Transactional: ON
     * logRepository     @Transactional: ON Exception
     */
    @Test
    void outerTransactionOffFail() {
        // given
        String username = "로그예외_outerTransactionOffSuccess";

        // when
        Assertions.assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        // then: log 데이터만 롤백됨
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }

    /**
     * memberService     @Transactional: ON
     * memberRepository  @Transactional: OFF
     * logRepository     @Transactional: OFF
     */
    @Test
    void singleTransaction() {
        // given
        String username = "outerTransactionOffSuccess";

        // when
        memberService.joinV1(username);

        // then: 모든 데이터가 정상 저장됨
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * memberService     @Transactional: ON
     * memberRepository  @Transactional: ON
     * logRepository     @Transactional: ON
     */
    @Test
    void outerTransactionOnSuccess() {
        // given
        String username = "outerTransactionOnSuccess";

        // when
        memberService.joinV1(username);

        // then: 모든 데이터가 정상 저장됨
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * memberService     @Transactional: ON
     * memberRepository  @Transactional: ON
     * logRepository     @Transactional: ON Exception
     */
    @Test
    void outerTransactionOnFail() {
        // given
        String username = "로그예외_outerTransactionOnFail";

        // when
        Assertions.assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        // then: 모든 데이터가 롤백됨
        assertThat(memberRepository.find(username).isEmpty()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }

    /**
     * memberService     @Transactional: ON
     * memberRepository  @Transactional: ON
     * logRepository     @Transactional: ON Exception
     */
    @Test
    void recoverExceptionFail() {
        // given
        String username = "로그예외_recoverExceptionFail";

        // when
        Assertions.assertThatThrownBy(() -> memberService.joinV2(username)).isInstanceOf(UnexpectedRollbackException.class);

        // then: 모든 데이터가 롤백됨
        assertThat(memberRepository.find(username).isEmpty()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }

    /**
     * memberService     @Transactional: ON
     * memberRepository  @Transactional: ON
     * logRepository     @Transactional: ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverExceptionSuccess() {
        // given
        String username = "로그예외_recoverExceptionSuccess";

        // when
        memberService.joinV2(username);

        // then: member 저장, log 롤백
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }
}