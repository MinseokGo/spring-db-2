package hello.springtransaction.apply;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
class TransactionBasicTest {

    @Autowired
    BasicService basicService;

    @Test
    void proxyCheck() {
        log.info("aop class={}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void transactionTest() {
        basicService.transaction();
        basicService.nonTransaction();
    }

    @TestConfiguration
    static class TransactionApplyBasicConfig {

        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {

        @Transactional
        public void transaction() {
            log.info("call transaction");
            boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("transaction active={}", transactionActive);
        }

        public void nonTransaction() {
            log.info("call nonTransaction");
            boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("transaction active={}", transactionActive);
        }
    }
}
