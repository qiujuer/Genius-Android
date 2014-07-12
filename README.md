Android-Utils
=============

Android Utils 是集合了自己的所有常用安卓操纵类。

JSONHelper.java:
import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by Genius on 2014/7/10.
 */
public class User {
    private int Name;
    private String Password;
    private Boolean IsRemember;
    private long L;
    private float F;
    private double D;
    private List<String> List;
    private Array Array;
    private String[] Str;

    public long getL() {
        return L;
    }

    public void setL(long l) {
        this.L = l;
    }

    public float getF() {
        return F;
    }

    public void setF(float f) {
        this.F = f;
    }

    public double getD() {
        return D;
    }

    public void setD(double d) {
        this.D = d;
    }

    public List<String> getList() {
        return List;
    }

    public void setList(List<String> list) {
        this.List = list;
    }

    public Array getArray() {
        return Array;
    }

    public void setArray(Array array) {
        this.Array = array;
    }

    public String[] getStr() {
        return Str;
    }

    public void setStr(String[] str) {
        this.Str = str;
    }

    public int getName() {
        return Name;
    }

    public void setName(int name) {
        this.Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public Boolean getIsRemember() {
        return IsRemember;
    }

    public void setIsRemember(Boolean password) {
        this.IsRemember = password;
    }

/////////////////////////////////////////////////
User user = new User();
            user.setName(1);
            user.setPassword("123456");
            user.setIsRemember(true);
            user.setD(20);
            user.setF(10);
            user.setL(321412215);
            user.setStr(new String[]{"dsad","fafas","www"});

            List<String> list = new ArrayList<String>();
            list.add("dasf2");
            list.add("dasf1");
            list.add("dasf3");

            user.setList(list);


            String jsonStrUser = JSONHelper.toJSON(user);   //序列化
            User jsonUser = JSONHelper.parseObject(jsonStrUser, User.class);    //反序列化

            Logs.i("", jsonUser.getName() + jsonUser.getPassword() + jsonUser.getIsRemember());
