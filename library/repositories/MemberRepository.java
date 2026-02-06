package library.repositories;

import library.entities.Member;
import java.util.List;

public interface MemberRepository {
    Member getById(long id);
    List<Member> getAll();
    long create(String fullName, String phone);

}
