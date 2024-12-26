package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * TODO#2-1 JdbcMemberRepository를 참고하여 모든 Repository를 JdbcTemplate 기반으로 변경하세요.
 * 더 이상 개발자가 Connection을 직접 공유하고 하나의 트랜잭션으로 묶는 일에 관여할 필요가 없습니다.
 * connection을 획득하기 위해서 Connection connection = DbConnectionThreadLocal.getConnection();을 더 이상 사용하지 않습니다.
 * 대신 PlatformTransactionManager에 의해 관리됩니다.
 *
 * @Repository <- org.springframework.stereotype.Repository 입니다.
 */

@Slf4j
@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    // 생성자 주입 방식으로 JdbcTemplate을 주입받습니다. 생성자 주입은 필드 주입이나 세터 주입보다 더 안전하며 테스트가 용이합니다.
    // 테스트를 작성할 때 해당 객체를 Mocking하기 쉽고, 객체가 불변성을 가질 수 있기 때문에 권장되는 방식입니다.
    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Member member) {
        // 회원 정보를 members 테이블에 삽입하는 SQL 쿼리문
        String sql = """
                    insert into members (
                        mb_email,
                        mb_name,
                        mb_password,
                        mb_mobile,
                        created_at
                    ) 
                    values(?,?,?,?,?);
                """;

        // KeyHolder는 삽입된 레코드의 키 값을 반환받기 위한 객체
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // JdbcTemplate의 update 메서드를 사용하여 PreparedStatement를 생성하고 실행합니다.
        // PreparedStatement 객체를 통해 SQL 쿼리의 파라미터를 바인딩합니다.
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            psmt.setString(index++, member.getMbEmail());
            psmt.setString(index++, member.getMbName());
            psmt.setString(index++, member.getMbPassword());
            psmt.setString(index++, member.getMbMobile());
            psmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
            return psmt;
        }, keyHolder);

        // 삽입 성공 시, 생성된 키를 member 객체에 반영
        if (rows > 0) {
            long mbNo = Objects.requireNonNull(keyHolder.getKey()).longValue();
            log.debug("Inserted member with mbNo={}", mbNo);
            // Reflection을 사용하여 member 객체의 mbNo 필드를 동적으로 설정
            ReflectionUtils.setField(member, "mbNo", mbNo);
        }
    }

    @Override
    public void update(MemberUpdateRequest memberUpdateRequest) {
        // 회원 정보를 업데이트하는 SQL 쿼리문
        String sql = """
                update members set
                    mb_email=?,
                    mb_name=?,
                    mb_mobile=?,
                    updated_at=?
                where mb_no=?;
                """;

        // JdbcTemplate을 사용하여 쿼리 실행
        jdbcTemplate.update(connection -> {
            PreparedStatement psmt = connection.prepareStatement(sql);
            int index = 1;
            psmt.setString(index++, memberUpdateRequest.getMbEmail());
            psmt.setString(index++, memberUpdateRequest.getMbName());
            psmt.setString(index++, memberUpdateRequest.getMbMobile());
            psmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
            psmt.setLong(index++, memberUpdateRequest.getMbNo());
            return psmt;
        });
    }

    @Override
    public void deleteByMbNo(long mbNo) {
        // 회원을 삭제하는 SQL 쿼리문
        String sql = """
                    delete from members where mb_no=?;
                """;

        // JdbcTemplate을 사용하여 쿼리 실행
        jdbcTemplate.update(connection -> {
            PreparedStatement psmt = connection.prepareStatement(sql);
            psmt.setLong(1, mbNo);
            return psmt;
        });
    }

    @Override
    public void updatePassword(long mbNo, String mbPassword) {
        // 회원 비밀번호를 업데이트하는 SQL 쿼리문
        String sql = """
                    update members 
                    set 
                        mb_password=?
                    where mb_no=?;
                """;

        // JdbcTemplate을 사용하여 쿼리 실행
        jdbcTemplate.update(connection -> {
            PreparedStatement psmt = connection.prepareStatement(sql);
            int index = 1;
            psmt.setString(index++, mbPassword);
            psmt.setLong(index++, mbNo);
            return psmt;
        });
    }

    @Override
    public Optional<Member> findByMbNo(long mbNo) {
        // mbNo로 회원을 조회하는 SQL 쿼리문
        String sql = """
                    select
                        mb_no,
                        mb_email,
                        mb_name,
                        mb_password,
                        mb_mobile,
                        created_at,
                        updated_at,
                        withdrawal_at
                    from 
                        members 
                    where mb_no=?;
                """;

        // JdbcTemplate의 queryForObject 메서드를 사용하여 조회
        return jdbcTemplate.queryForObject(sql, new MemberRowMapper(), mbNo);
    }

    @Override
    public Optional<Member> findByMbEmail(String mbEmail) {
        // mbEmail로 회원을 조회하는 SQL 쿼리문
        String sql = """
                    select
                        mb_no,
                        mb_email,
                        mb_name,
                        mb_password,
                        mb_mobile,
                        created_at,
                        updated_at,
                        withdrawal_at
                    from 
                        members 
                    where mb_email=?;
                """;

        // JdbcTemplate의 queryForObject 메서드를 사용하여 조회
        return jdbcTemplate.queryForObject(sql, new MemberRowMapper(), mbEmail);
    }

    @Override
    public Optional<Member> findByMbMobile(String mBMobile) {
        // mbMobile로 회원을 조회하는 SQL 쿼리문
        String sql = """
                    select
                        mb_no,
                        mb_email,
                        mb_name,
                        mb_password,
                        mb_mobile,
                        created_at,
                        updated_at,
                        withdrawal_at
                    from 
                        members 
                    where mb_mobile=?;
                """;

        // JdbcTemplate의 queryForObject 메서드를 사용하여 조회
        return jdbcTemplate.queryForObject(sql, new MemberRowMapper(), mBMobile);
    }

    @Override
    public boolean existsByMbNo(long mbNo) {
        // mbNo로 회원이 존재하는지 확인하는 SQL 쿼리문
        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_no=?;
                """;

        // JdbcTemplate의 query 메서드를 사용하여 결과 존재 여부 확인
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbNo));
    }

    @Override
    public boolean existsByMbEmail(String mbEmail) {
        // mbEmail로 회원이 존재하는지 확인하는 SQL 쿼리문
        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_email=?;
                """;

        // JdbcTemplate의 query 메서드를 사용하여 결과 존재 여부 확인
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbEmail));
    }

    @Override
    public boolean existsByMbMobile(String mbMobile) {
        // mbMobile로 회원이 존재하는지 확인하는 SQL 쿼리문
        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_mobile=?;
                """;

        // JdbcTemplate의 query 메서드를 사용하여 결과 존재 여부 확인
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbMobile));
    }

    @Override
    public boolean isMemberWithdrawn(Long mbNo) {
        // 회원이 탈퇴한 상태인지 확인하는 SQL 쿼리문
        String sql = """
                    select
                        1
                    from
                        members 
                    where mb_no=? and withdrawal_at is not null;
                """;

        // JdbcTemplate의 query 메서드를 사용하여 결과 존재 여부 확인
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbNo));
    }

    @Override
    public void updateWithdrawalAt(long mbNo, LocalDateTime updateWithdrawalAt) {
        // 회원 탈퇴 시간을 업데이트하는 SQL 쿼리문
        String sql = """
                    update members
                    set
                        withdrawal_at=?
                    where mb_no=?;
                """;

        // JdbcTemplate을 사용하여 쿼리 실행
        jdbcTemplate.update(connection -> {
            PreparedStatement psmt = connection.prepareStatement(sql);
            psmt.setTimestamp(1, Timestamp.valueOf(updateWithdrawalAt));
            psmt.setLong(2, mbNo);
            return psmt;
        });
    }

    // RowMapper를 사용하여 ResultSet을 Member 객체로 변환
    private class MemberRowMapper implements RowMapper<Optional<Member>> {

        @Override
        public Optional<Member> mapRow(ResultSet rs, int rowNum) throws SQLException {
            long dbMbNo = rs.getLong("mb_no");
            String mbEmail = rs.getString("mb_email");
            String mbName = rs.getString("mb_name");
            String mbPassword = rs.getString("mb_password");
            String mbMobile = rs.getString("mb_mobile");

            LocalDateTime createdAt = Objects.nonNull(rs.getTimestamp("created_at")) ? rs.getTimestamp("created_at").toLocalDateTime() : null;
            LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null;
            LocalDateTime withdrawalAt = Objects.nonNull(rs.getTimestamp("withdrawal_at")) ? rs.getTimestamp("withdrawal_at").toLocalDateTime() : null;

            // ResultSet 데이터를 기반으로 Member 객체 생성
            Member member = Member.ofExistingMember(
                    dbMbNo,
                    mbEmail,
                    mbName,
                    mbPassword,
                    mbMobile,
                    createdAt,
                    updatedAt,
                    withdrawalAt
            );
            return Optional.of(member);
        }
    }

}
