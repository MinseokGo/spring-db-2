package hello.springtransaction.propagation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
     * memberService        @Transactional: OFF
     * memberRepository     @Transactional: ON
     * logRepository        @Transactional: ON
     */
    @Test
    void outerTransactionOff_success() {
        // given
        String name = "outerTransactionOff_success";

        // when
        memberService.joinV1(name);

        // then
        assertTrue(memberRepository.findByName(name).isPresent());
        assertTrue(logRepository.findByMessage(name).isPresent());
    }
}