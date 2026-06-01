import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class TempBcrypt {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("Crisf@123"));
    }
}
