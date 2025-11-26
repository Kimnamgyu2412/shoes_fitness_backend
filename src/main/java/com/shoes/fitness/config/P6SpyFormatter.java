package com.shoes.fitness.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.text.SimpleDateFormat;

/**
 * P6Spy Custom Log Formatter
 * - SQL에 파라미터를 채워서 표시
 * - 실행 시간 표시
 * - 스택 트레이스로 호출 위치 표시
 */
public class P6SpyFormatter implements MessageFormattingStrategy {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                 String prepared, String sql, String url) {

        // 빈 SQL은 무시
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append("=================================================\n");
        sb.append("[P6Spy] ").append(elapsed).append("ms | ").append(category).append("\n");

        // 호출 위치 추적 (스택 트레이스)
        String callStack = getCallStack();
        if (callStack != null && !callStack.isEmpty()) {
            sb.append("Called from: ").append(callStack);
        }

        sb.append("=================================================\n");

        // SQL (파라미터가 채워진 실제 SQL) - 복사하기 쉽게 그대로 출력
        String formattedSql = formatSql(sql);
        sb.append(formattedSql).append("\n");

        sb.append("=================================================\n");

        return sb.toString();
    }

    /**
     * SQL 포맷팅 (Hibernate 포맷터 사용)
     */
    private String formatSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }

        try {
            // DML (SELECT, INSERT, UPDATE, DELETE) 포맷팅
            if (sql.trim().toLowerCase().startsWith("select") ||
                sql.trim().toLowerCase().startsWith("with")) {
                return FormatStyle.BASIC.getFormatter().format(sql);
            } else if (sql.trim().toLowerCase().startsWith("insert") ||
                       sql.trim().toLowerCase().startsWith("update") ||
                       sql.trim().toLowerCase().startsWith("delete")) {
                return FormatStyle.BASIC.getFormatter().format(sql);
            } else {
                // DDL 등 기타 SQL
                return FormatStyle.DDL.getFormatter().format(sql);
            }
        } catch (Exception e) {
            // 포맷팅 실패시 원본 반환
            return sql;
        }
    }

    /**
     * 호출 스택 추적 (어느 서비스 메서드에서 호출되었는지 확인)
     */
    private String getCallStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();

            // 우리 애플리케이션 패키지만 필터링
            if (className.startsWith("com.shoes.fitness.domain") ||
                className.startsWith("com.shoes.fitness.service")) {

                // Repository 호출은 제외 (너무 깊은 레벨)
                if (className.contains("Repository") ||
                    className.contains("$$") ||
                    className.contains("CGLIB")) {
                    continue;
                }

                // 첫 번째 발견한 것만 반환
                return className + "." + element.getMethodName() +
                       "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
            }
        }

        return null;
    }
}
