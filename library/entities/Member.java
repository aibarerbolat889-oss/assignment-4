package library.entities;

public class Member {
    public long id;
    public String fullName;
    public String phone;

    public Member(long id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }
}
