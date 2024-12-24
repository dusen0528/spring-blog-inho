package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Member member) {

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

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int index=1;
            psmt.setString(index++, member.getMbEmail());
            psmt.setString(index++, member.getMbName());
            psmt.setString(index++, member.getMbPassword());
            psmt.setString(index++, member.getMbMobile());
            //datetime <--> LocalDateTime간 변환 timestamp로 변환후 처리해야 함.
            psmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
            return psmt;
        },keyHolder);

        if(rows>0) {
            long mbNo = Objects.requireNonNull(keyHolder.getKey()).longValue();
            log.debug("Inserted member with mbNo={}", mbNo);
            ReflectionUtils.setField(member, "mbNo", mbNo);
        }

    }

    @Override
    public void update(MemberUpdateRequest memberUpdateRequest) {

        String sql = """
                update members set
                    mb_email=?,
                    mb_name=?,
                    mb_mobile=?,
                    updated_at=?
                where mb_no=?;
                """;

        jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql);
            int index=1;
            psmt.setString(index++, memberUpdateRequest.getMbEmail());
            psmt.setString(index++, memberUpdateRequest.getMbName());
            psmt.setString(index++, memberUpdateRequest.getMbMobile());
            psmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
            psmt.setLong(index++, memberUpdateRequest.getMbNo());
            psmt.executeUpdate();
            return psmt;
        });

    }

    @Override
    public void deleteByMbNo(long mbNo) {

        String sql = """
                    delete from members where mb_no=?;
                """;

        jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql);
            psmt.setLong(1,mbNo);
            return psmt;
        });

    }

    @Override
    public void updatePassword(long mbNo, String mbPassword) {

        String sql = """
                    update members 
                    set 
                        mb_password=?
                    where mb_no=?;
                """;

        jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql);
            int index=1;
            psmt.setString(index++,mbPassword);
            psmt.setLong(index++,mbNo);
            return psmt;
        });
    }

    @Override
    public Optional<Member> findByMbNo(long mbNo) {

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

        return jdbcTemplate.queryForObject(sql, new MemberRowMapper(), mbNo);
    }

    @Override
    public Optional<Member> findByMbEmail(String mbEmail) {

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

        return jdbcTemplate.queryForObject(sql, new MemberRowMapper(), mbEmail);
    }

    @Override
    public Optional<Member> findByMbMobile(String mBMobile) {

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

        return jdbcTemplate.queryForObject(sql, new MemberRowMapper(), mBMobile);
    }

    @Override
    public boolean existsByMbNo(long mbNo) {

        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_no=?;
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbNo));
    }

    @Override
    public boolean existsByMbEmail(String mbEmail) {

        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_email=?;
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbEmail));
    }

    @Override
    public boolean existsByMbMobile(String mbMobile) {

        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_mobile=?;
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbMobile));
    }

    @Override
    public boolean isMemberWithdrawn(Long mbNo) {

        String sql = """
                    select
                        1
                    from
                        members 
                    where mb_no=? and withdrawal_at is not null;
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbNo));

    }

    @Override
    public void updateWithdrawalAt(long mbNo, LocalDateTime updateWithdrawalAt) {

        String sql = """
                    update members
                    set
                        withdrawal_at=?
                    where mb_no=?;
                """;

        jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql);
            psmt.setTimestamp(1, Timestamp.valueOf(updateWithdrawalAt));
            psmt.setLong(2,mbNo);
            return psmt;
        });

    }

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
