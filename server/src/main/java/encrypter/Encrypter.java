package encrypter;

import org.mindrot.jbcrypt.BCrypt;

public class Encrypter {

    public String encryptPassword(String clearText) {
        return BCrypt.hashpw(clearText, BCrypt.gensalt());
    }

    public boolean checkPassword(String clearText, String hashed) {
        return BCrypt.checkpw(clearText, hashed);
    }
}
