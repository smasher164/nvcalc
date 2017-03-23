/**
 * Created by akhil on 3/19/17.
 */
class errorString implements error {
    String s;

    public errorString(String text) {
        s = text;
    }

    public String Error() {
        return s;
    }
}