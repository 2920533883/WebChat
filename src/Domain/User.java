package Domain;

/**
 * @program: WebChat
 * @description: 用户类
 * @author: Mr.Zhang
 * @create: 2020-04-23 17:21
 **/

public class User {
    private final String name; // 姓名
    private final String _class; // 班级
    private final String userPort; // Port

    public User(String name, String _class, String userPort) {
        this.name = name;
        this._class = _class;
        this.userPort = userPort;
    }

    @Override
    public String toString() {
        return userPort + " " + _class + " " + name;
    }

    public String getName() {
        return name;
    }

    public String get_class() {
        return _class;
    }

    public String getUserPort() {
        return userPort;
    }
}
