package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository(name = JdbcMemberRepository.BEAN_NAME)
public class JdbcMemberRepository implements MemberRepository {
    public static final String BEAN_NAME="jdbcMemberRepository";

    @Override
    public void save(Member member) {

        Connection connection = DbConnectionThreadLocal.getConnection();

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

        try ( PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            int index=1;
            psmt.setString(index++, member.getMbEmail());
            psmt.setString(index++, member.getMbName());
            psmt.setString(index++, member.getMbPassword());
            psmt.setString(index++, member.getMbMobile());
            //datetime <--> LocalDateTime간 변환 timestamp로 변환후 처리해야 함.
            psmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));

            int rows =  psmt.executeUpdate();

            if(rows > 0) {
                try(ResultSet rs = psmt.getGeneratedKeys()) {
                    if(rs.next()) {
                        long mbNo = rs.getLong(1);
                        log.debug("inserted member with mbNo={}", mbNo);
                        ReflectionUtils.setField(member, "mbNo", mbNo);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(MemberUpdateRequest memberUpdateRequest) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                update members set
                    mb_email=?,
                    mb_name=?,
                    mb_mobile=?,
                    updated_at=?
                where mb_no=?;
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setString(index++, memberUpdateRequest.getMbEmail());
            psmt.setString(index++, memberUpdateRequest.getMbName());
            psmt.setString(index++, memberUpdateRequest.getMbMobile());
            psmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
            psmt.setLong(index++, memberUpdateRequest.getMbNo());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public void deleteByMbNo(long mbNo) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from members where mb_no=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql) ){
            psmt.setLong(1,mbNo);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public void updatePassword(long mbNo, String mbPassword) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    update members 
                    set 
                        mb_password=?
                    where mb_no=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql) ){
            int index=1;
            psmt.setString(index++,mbPassword);
            psmt.setLong(index++,mbNo);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
            throw new DatabaseException(e);
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
            throw new DatabaseException(e);
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
            throw new DatabaseException(e);
        }

        return Optional.empty();
    }

    @Override
    public boolean existsByMbNo(long mbNo) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_no=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,mbNo);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                   return true;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public boolean existsByMbEmail(String mbEmail) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_email=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1,mbEmail);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    return true;
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return false;
    }

    @Override
    public boolean existsByMbMobile(String mbMobile) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_mobile=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1,mbMobile);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    return true;
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return false;
    }

    @Override
    public boolean isMemberWithdrawn(Long mbNo) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        1
                    from 
                        members 
                    where mb_no=? and withdrawal_at is not null;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setLong(1,mbNo);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return false;
    }

    @Override
    public void updateWithdrawalAt(long mbNo, LocalDateTime updateWithdrawalAt) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    update members
                    set
                        withdrawal_at=?
                    where mb_no=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setTimestamp(1, Timestamp.valueOf(updateWithdrawalAt));
            psmt.setLong(2,mbNo);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
