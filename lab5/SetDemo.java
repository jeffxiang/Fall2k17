import java.util.*;

public class SetDemo {
    public static void main(String[] args){
        Set s = new HashSet<String>();
        s.add("papa");
        s.add("bear");
        s.add("mama");
        s.add("bear");
        s.add("baby");
        s.add("bear");
        System.out.println(s);
    }
}
