package library.repositories;

import library.db.IDatabase;
import library.entities.Member;
import library.exceptions.MemberNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepository {

    private final IDatabase db;

    public MemberRepositoryImpl(IDatabase db) {
        this.db = db;
    }

    @Override
    public Member getById(long id) {
        String sql = "select id, full_name, phone from members where id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                );
            }
            throw new MemberNotFoundException("Member not found: id=" + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public long create(String fullName, String phone) {
        String sql = "insert into members(full_name, phone) values(?, ?) returning id";

        try (var con = db.getConnection();
             var ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, phone);

            var rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Member> getAll() {
        String sql = "select id, full_name, phone from members order by id";
        List<Member> res = new ArrayList<>();

        try (Connection con = db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                res.add(new Member(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                ));
            }
            return res;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
