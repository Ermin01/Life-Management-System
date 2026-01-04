package ErmHam;

public class UserSession {

    private static Users loggedUser;

    private UserSession() {}
//    Slika = new JLabel(String.valueOf(this.getClass().getResource("/Pretrazivanje.png")));
    public static void setUser(Users user) {
        loggedUser = user;
    }

    public static Users getUser() {
        return loggedUser;
    }

    public static void clear() {
        loggedUser = null;
    }
}

//adminPlaceholder