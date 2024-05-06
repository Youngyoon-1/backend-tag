package com.tag.acceptance;

import java.util.List;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class AcceptanceTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(final TestContext testContext) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate(testContext);
        final List<String> truncateQueries = createTruncateQueries(jdbcTemplate);
        executeQueries(jdbcTemplate, truncateQueries);
        final RedisConnectionFactory redisConnectionFactory = getRedisConnectionFactory(testContext);
        redisConnectionFactory.getConnection()
                .commands()
                .flushAll();
    }

    private RedisConnectionFactory getRedisConnectionFactory(final TestContext testContext) {
        return testContext.getApplicationContext()
                .getBean(RedisConnectionFactory.class);
    }

    private JdbcTemplate getJdbcTemplate(final TestContext testContext) {
        return testContext.getApplicationContext()
                .getBean(JdbcTemplate.class);
    }

    private List<String> createTruncateQueries(final JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList(
                "SELECT 'TRUNCATE TABLE ' || TABLE_NAME "
                        + "FROM ALL_TABLES "
                        + "WHERE OWNER = 'ADMIN'",
                String.class
        );
    }

    private void executeQueries(final JdbcTemplate jdbcTemplate, final List<String> truncateQueries) {
        constrain(jdbcTemplate, "'DISABLE'");
        truncateQueries.forEach(jdbcTemplate::execute);
        constrain(jdbcTemplate, "'ENABLE'");
    }

    private void constrain(final JdbcTemplate jdbcTemplate, final String value) {
        final List<String> queryForList = jdbcTemplate.queryForList(
                "SELECT 'ALTER TABLE ' || at.TABLE_NAME || ' ' || "
                        + value
                        + " || ' CONSTRAINT ' || CONSTRAINT_NAME "
                        + "FROM ALL_TABLES at "
                        + "JOIN user_constraints ac ON at.table_name = ac.table_name "
                        + "WHERE at.OWNER = 'ADMIN' AND ac.CONSTRAINT_TYPE = 'R'",
                String.class
        );
        queryForList.forEach(jdbcTemplate::execute);
    }
}
