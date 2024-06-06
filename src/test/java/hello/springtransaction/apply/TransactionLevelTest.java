package hello.springtransaction.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
class TransactionLevelTest {

    @Autowired
    LevelService levelService;

    @Test
    void orderTest() {
        levelService.write();
        levelService.read();
    }

    @TestConfiguration
    static class TransactionLevelTestConfig {

        @Bean
        LevelService levelService() {
            return new LevelService();
        }
    }

    @Transactional(readOnly = true)
    static class LevelService {

        @Transactional
        public void write() {
            log.info("call write");
            printTransactionInfo();
        }

        public void read() {
            log.info("call read");
            printTransactionInfo();
        }

        private void printTransactionInfo() {
            boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("transaction active={}", transactionActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("transaction readOnly={}", readOnly);
        }
    }
}
