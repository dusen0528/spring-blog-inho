package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.config.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class JdbcMemberRepository implements MemberRepository {

    @Override
    public int save(Member member) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    insert into members (
                        mb_email,
                        mb_name,
                        mb_password,
                        mb_mobile,
                        created_at
                    ) 
                    values(?,?,?,?,?,?,?);
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql) ){
            int index=1;
            psmt.setString(index++, member.getMbEmail());
            psmt.setString(index++, member.getMbName());
            psmt.setString(index++, member.getMbPassword());
            psmt.setString(index++, member.getMbMobile());
            //datetime <--> LocalDateTime간 변환 timestamp로 변환후 처리해야 함.
            psmt.setTimestamp(index++, Timestamp.valueOf(member.getCreatedAt()));

            int rows =  psmt.executeUpdate();

            if(rows > 0) {
                try(ResultSet rs = psmt.getGeneratedKeys()) {
                    if(rs.next()) {
                        long mbId = rs.getLong(1);
                        log.debug("inserted member with mbId={}", mbId);
                        ReflectionUtils.setField(member, "mbId", mbId);
                    }
                }
            }

            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(Member member) {
        return 0;
    }

    @Override
    public int delete(long mbNo) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from members where mb_no=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql) ){
            int index=1;
            psmt.setLong(index,mbNo);
            return psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Member> findByMbNo(long mbNo) {

        Connection connection = DbConnectionThreadLocal.getConnection();
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

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setLong(1,mbNo);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    int index=1;
                    long dbMbNo = rs.getLong(index++);
                    String mbEmail = rs.getString(index++);
                    String mbName = rs.getString(index++);
                    String mbPassword = rs.getString(index++);
                    String mbMobile = rs.getString(index++);

                    LocalDateTime createdAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;
                    index++;
                    LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;
                    index++;
                    LocalDateTime withdrawalAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;

                    Member member = Member.ofExistingMember(dbMbNo,mbEmail,mbName,mbPassword,mbMobile,createdAt,updatedAt,withdrawalAt);
                    return Optional.of(member);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Member> findByMbEmail(String mbEmail) {
        Connection connection = DbConnectionThreadLocal.getConnection();
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

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1,mbEmail);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    int index=1;
                    long dbMbNo = rs.getLong(index++);
                    String dbMbEmail = rs.getString(index++);
                    String mbName = rs.getString(index++);
                    String mbPassword = rs.getString(index++);
                    String mbMobile = rs.getString(index++);

                    LocalDateTime createdAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;
                    index++;
                    LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;
                    index++;
                    LocalDateTime withdrawalAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;

                    Member member = Member.ofExistingMember(dbMbNo,dbMbEmail,mbName,mbPassword,mbMobile,createdAt,updatedAt,withdrawalAt);
                    return Optional.of(member);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Member> findByMbMobile(String mBMobile) {
        Connection connection = DbConnectionThreadLocal.getConnection();
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

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1,mBMobile);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    int index=1;
                    long mbNo = rs.getLong(index++);
                    String mbEmail = rs.getString(index++);
                    String mbName = rs.getString(index++);
                    String mbPassword = rs.getString(index++);
                    String mbMobile = rs.getString(index++);

                    LocalDateTime createdAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;
                    index++;
                    LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;
                    index++;
                    LocalDateTime withdrawalAt = Objects.nonNull(rs.getTimestamp(index)) ? rs.getTimestamp(index).toLocalDateTime() : null;

                    Member member = Member.ofExistingMember(mbNo,mbEmail,mbName,mbPassword,mbMobile,createdAt,updatedAt,withdrawalAt);
                    return Optional.of(member);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public boolean existsByMbNo(long mbNo) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        count(mb_no) as cnt
                    from 
                        members 
                    where mb_no=?;
                """;
        int cnt =0;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,mbNo);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                   cnt = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cnt>0;
    }

    @Override
    public boolean existsByMbEmail(String mbEmail) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        count(mb_no) as cnt
                    from 
                        members 
                    where mb_email=?;
                """;
        int cnt =0;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1,mbEmail);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    cnt = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cnt>0;
    }

    @Override
    public boolean existsByMbMobile(String mbMobile) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        count(mb_no) as cnt
                    from 
                        members 
                    where mb_email=?;
                """;
        int cnt =0;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1,mbMobile);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    cnt = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cnt>0;
    }

    @Override
    public int updateWithdrawalAt(long mbNo, LocalDateTime updateWithdrawalAt) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    update members
                    set
                        withdrawal_at=?
                    where mb_no=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,mbNo);
            return  psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
