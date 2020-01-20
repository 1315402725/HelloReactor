package main;

public class testReactor {
    public static void main(String[] args) {
        BossGroup bossGroup = new BossGroup(8080);
        Thread t = new Thread(bossGroup);
        t.start();

    }
}
